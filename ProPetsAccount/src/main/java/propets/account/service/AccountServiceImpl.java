package propets.account.service;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import propets.account.convertor.AccountConverter;
import propets.account.dao.AccountRepository;
import propets.account.domain.User;
import propets.account.dto.EditUserDto;
import propets.account.dto.RoleDto;
import propets.account.dto.UserDto;
import propets.account.exceptions.ConflictException;
import propets.account.security.TokenService;

@Service
public class AccountServiceImpl implements AccountService {

	
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	AccountConverter convertor;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	TokenService tokenService;

	@Override
	public String registerUser(String auth) {
		String login = tokenService.decodeToken(auth)[0];
		String password = tokenService.decodeToken(auth)[1];
		if (login == null || password == null) {
			throw new ConflictException();
		}
		if (accountRepository.existsById(login)) {
			throw new ConflictException();
		}
		String hashPassword = passwordEncoder.encode(password);
		User user = User.builder()
				.avatar("https://www.gravatar.com/avatar/0?d=mp")
				.email(login)
				.name(login)
				.password(hashPassword)
				.block(false)
				.role("ROLE_USER")
				.favoritePosts(new HashSet<String>())
				.build();
		user = accountRepository.save(user);
		return "Registered";
	}

	@Override
	public UserDto loginUser(String auth, HttpServletResponse response) {
		String login = tokenService.getLoginFromCredential(auth);
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		String token = tokenService.createJWT(login);
		response.addHeader("X-Token", token);
		response.addHeader("U-Name", login);
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public UserDto userInformation(String login) {
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public UserDto editUserProfile(EditUserDto editUser, String login, String token) {
		String email = tokenService.getLoginFromCredential(token);
		if (!email.equals(login)) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(email).orElseThrow(() -> new ConflictException());
		if (editUser.getAvatar() != null) {
			user.setAvatar(editUser.getAvatar());
		}
		if (editUser.getName() != null) {
			user.setName(editUser.getName());
		}
		if (editUser.getPhone() != null) {
			user.setPhone(editUser.getPhone());
		}
		accountRepository.save(user);
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public UserDto removeUser(String login, String token) {
		String email = tokenService.getLoginFromCredential(token);
		if (!email.equals(login)) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).get();
		accountRepository.deleteById(login);
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public Set<String> addRoles(String login, RoleDto roles, String token) {
		String email = tokenService.getLoginFromCredential(token);
		User admin = accountRepository.findById(email).orElseThrow(() -> new ConflictException());
		if (!admin.getRoles().contains("ROLE_ADMIN")) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		for (String role : roles.getRoles()) {
			user.addRole("ROLE_" + role.toUpperCase());
		}

		accountRepository.save(user);
		return user.getRoles();
	}

	@Override
	public UserDto blockUserAccount(String login, boolean status, String token) {
		String email = tokenService.getLoginFromCredential(token);
		User admin = accountRepository.findById(email).orElseThrow(() -> new ConflictException());
		if (!admin.getRoles().contains("ROLE_ADMIN")) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		user.setBlock(status);
		accountRepository.save(user);
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public Set<String> addUserFavorite(String login, String id, String token) {
		String email = tokenService.getLoginFromCredential(token);
		if (!email.equals(login)) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		user.addFavorite(id);
		accountRepository.save(user);
		return user.getFavoritePosts();
	}

	@Override
	public Set<String> removeUserFavorite(String login, String id, String token) {
		String email = tokenService.getLoginFromCredential(token);
		if (!email.equals(login)) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		user.removeFavorite(id);
		accountRepository.save(user);
		return user.getFavoritePosts();
	}

	@Override
	public Set<String> getUserFavorites(String login, String token) {
		String email = tokenService.getLoginFromCredential(token);
		if (!email.equals(login)) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		return user.getFavoritePosts();
	}

	

	@Override
	public ResponseEntity<String> tokenValidation(String token) {
		Claims claims = null;		
		try {
		claims = tokenService.verifyJwt(token);
		} catch (Exception e) {			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}			
		User user = accountRepository.findById(claims.getId()).orElseThrow(() -> new ConflictException());		
		String jwt = tokenService.createJWT(claims.getId());
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Token", jwt);
		headers.add("U-Name", user.getName());
		return new ResponseEntity<>(headers, HttpStatus.OK);
	}

}
