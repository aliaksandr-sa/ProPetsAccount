package propets.account.convertor;

import org.springframework.stereotype.Component;

import propets.account.domain.User;
import propets.account.dto.RegisterUserDto;
import propets.account.dto.UserDto;

@Component
//S
public class AccountConverter {

	public RegisterUserDto convertToRegisterUserDto(User user) {
		return RegisterUserDto.builder()
				.avatar(user.getAvatar())
				.name(user.getName())
				.email(user.getEmail())
				.roles(user.getRoles())
				.build();
	}

	public UserDto convertUserToUserDto(User user) {
		return UserDto.builder()
				.avatar(user.getAvatar())
				.name(user.getName())
				.email(user.getEmail())
				.phone(user.getPhone())
				.roles(user.getRoles())
				.block(user.getBlock())
				.favorites(user.getFavoritePosts())
				.build();

	
	}
}
