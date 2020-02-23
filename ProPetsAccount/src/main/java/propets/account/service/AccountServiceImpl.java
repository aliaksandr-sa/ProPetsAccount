package propets.account.service;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import propets.account.convertor.AccountConverter;
import propets.account.dao.AccountRepository;
import propets.account.domain.User;
import propets.account.dto.BlockUserDto;
import propets.account.dto.EditUserDto;
import propets.account.dto.NewUserDto;
import propets.account.dto.RegisterUserDto;
import propets.account.dto.UserDto;
import propets.account.exceptions.ConflictException;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accountRepository;
	@Autowired
	AccountConverter convertor;
	
	@Override
	public RegisterUserDto registerUser(NewUserDto newUser) {
		String login = newUser.getEmail();
		String password = newUser.getPassword();
		if (login == null || password == null) {
			throw new ConflictException();
		}
		if (accountRepository.existsById(login)) {
			throw new ConflictException();
		}
		User user = User.builder()
				.avatar("https://www.gravatar.com/avatar/0?d=mp")
				.email(newUser.getEmail())
				.name(newUser.getName())
				.block(false)
				.role("ROLE_USER")
				.favoritePosts(new HashSet<String>())
				.build();
		user = accountRepository.save(user);
		return convertor.convertToRegisterUserDto(user);
	}

	@Override
	public UserDto loginUser(String token) {
		String login = getLoginFromCredential(token);
		System.out.println(login);
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public UserDto userInformation(String login, String token) {
//		String email = getLoginFromCredential(token);
//		if (email != login) {
//			throw new ConflictException();
//		}
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public UserDto editUserProfile(EditUserDto editUser, String login, String token) {
//		String email = getLoginFromCredential(token);
//		if (email != login) {
//			throw new ConflictException();
//		}
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
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
		if (email != login) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).get();
		accountRepository.deleteById(login);
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public Set<String> addRoles(String login, String role, String token) {
		String email = getLoginFromCredential(token);
		User admin = accountRepository.findById(email).orElseThrow(() -> new ConflictException());
		if (!admin.getRoles().contains("ROLE_ADMIN")) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).get();
		user.addRole(role);
		accountRepository.save(user);
		return user.getRoles();
	}

	@Override
	public UserDto blockUserAccount(String login, BlockUserDto blockUser, String token, boolean status) {
		User user = accountRepository.findById(login).orElseThrow(() -> new ConflictException());
		user.setBlock(status);
		accountRepository.save(user);
		return convertor.convertUserToUserDto(user);
	}

	@Override
	public Set<String> addUserFavorite(String login, String id, String token) {
		String email = getLoginFromCredential(token);
		if (email != login) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).get();
		user.addFavorite(id);
		accountRepository.save(user);
		return user.getFavoritePosts();
	}

	@Override
	public Set<String> removeUserFavorite(String login, String id, String token) {
		String email = getLoginFromCredential(token);
		if (email != login) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).get();
		user.removeFavorite(id);
		accountRepository.save(user);
		return user.getFavoritePosts();
	}

	@Override
	public Set<String> getUserFavorites(String login, String token) {
		String email = getLoginFromCredential(token);
		if (email != login) {
			throw new ConflictException();
		}
		User user = accountRepository.findById(login).get();
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

}
