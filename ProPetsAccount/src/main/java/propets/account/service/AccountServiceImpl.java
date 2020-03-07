package propets.account.service;

import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import propets.account.convertor.AccountConverter;
import propets.account.dao.AccountRepository;
import propets.account.domain.User;
import propets.account.dto.EditUserDto;
import propets.account.dto.RoleDto;
import propets.account.dto.UserDto;
import propets.account.exceptions.ConflictException;

@Service
public class AccountServiceImpl implements AccountService {

	private static String SECRET_KEY = "123_pasWoRD_QweRtY_456_pe4enka_snEgUr04ka_789_SHliapA_neZnajKaNaLuNE";
	private static long TERM = 900000;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	AccountConverter convertor;
	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public String registerUser(String auth) {
		String login = decodeToken(auth)[0];
		String password = decodeToken(auth)[1];
		if (login == null || password == null) {
			throw new ConflictException();
		}
		if (accountRepository.existsById(login)) {
			throw new ConflictException();
		}
		String hashPassword = passwordEncoder.encode(password);
		User user = User.builder().avatar("https://www.gravatar.com/avatar/0?d=mp").email(login).name(login)
				.password(hashPassword).block(false).role("ROLE_USER").favoritePosts(new HashSet<String>()).build();
		user = accountRepository.save(user);
		return "Registered";
	}

	@Override
	public UserDto loginUser(String auth, HttpServletResponse response) {
		String login = getLoginFromCredential(auth);
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		List<String> uRoles = user.getRoles().stream().collect(Collectors.toList());
		String token = createJWT(login);
		HttpHeaders headers = new HttpHeaders();
		headers.add("U-Name", login);
		headers.addAll("U-Roles", uRoles);
		headers.add("X-Token", token);
		new ResponseEntity<>(headers, HttpStatus.OK);
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
		String email = getLoginFromCredential(token);
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
		String email = getLoginFromCredential(token);
		if (!email.equals(login)) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).get();
		accountRepository.deleteById(login);
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public Set<String> addRoles(String login, RoleDto roles, String token) {
		String email = getLoginFromCredential(token);
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
		String email = getLoginFromCredential(token);
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
		String email = getLoginFromCredential(token);
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
		String email = getLoginFromCredential(token);
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
		String email = getLoginFromCredential(token);
		if (!email.equals(login)) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		return user.getFavoritePosts();
	}

	private String[] decodeToken(String token) {
		int pos = token.indexOf(" ");
		String newToken = token.substring(pos + 1);
		byte[] decodeBytes = Base64.getDecoder().decode(newToken);
		String credential = new String(decodeBytes);
		String[] credentials = credential.split(":");
		return credentials;
	}

	private String getLoginFromCredential(String token) {
		String[] credential = decodeToken(token);
		return credential[0];
	}

	public static String createJWT(String id) {
		return Jwts.builder().setId(id).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TERM))
				.signWith(new SecretKeySpec(DatatypeConverter.parseBase64Binary(SECRET_KEY),
						SignatureAlgorithm.HS256.getJcaName()))
				.compact();
	}

	@Override
	public ResponseEntity<String> tokenValidation(String token) {
		// TODO Auto-generated method stub
		return null;
	}

}
