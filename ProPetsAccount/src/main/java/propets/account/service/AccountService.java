package propets.account.service;

import java.util.Set;

import propets.account.dto.BlockUserDto;
import propets.account.dto.EditUserDto;
import propets.account.dto.NewUserDto;
import propets.account.dto.RegisterUserDto;
import propets.account.dto.UserDto;

public interface AccountService {

	RegisterUserDto registerUser(NewUserDto newUser);

	UserDto loginUser(String token);

	UserDto userInformation(String login, String token);

	UserDto editUserProfile(EditUserDto editUser, String login, String token);

	UserDto removeUser(String login, String token);

	Set<String> addRoles(String login, String role, String token);

	UserDto blockUserAccount(String login, BlockUserDto blockUser, String token, boolean status);

	Set<String> addUserFavorite(String login, String id, String token);

	Set<String> removeUserFavorite(String login, String id, String token);

	Set<String> getUserFavorites(String login, String token);

}
