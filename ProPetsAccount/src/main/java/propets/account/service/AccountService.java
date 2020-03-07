package propets.account.service;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

import propets.account.dto.EditUserDto;
import propets.account.dto.RoleDto;
import propets.account.dto.UserDto;

public interface AccountService {

	String registerUser(String auth);

	UserDto loginUser(String auth, HttpServletResponse response);

	UserDto userInformation(String login);

	UserDto editUserProfile(EditUserDto editUser, String login, String token);

	UserDto removeUser(String login, String token);

	Set<String> addRoles(String login, RoleDto roles, String token);

	UserDto blockUserAccount(String login, boolean status, String token);

	Set<String> addUserFavorite(String login, String id, String token);

	Set<String> removeUserFavorite(String login, String id, String token);

	Set<String> getUserFavorites(String login, String token);

	ResponseEntity<String> tokenValidation(String token);

}
