package propets.account.controller;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import propets.account.dto.EditUserDto;
import propets.account.dto.RoleDto;
import propets.account.dto.UserDto;
import propets.account.service.AccountService;

@RestController
@RequestMapping("/{lang}/account/v1")
public class AccountController {

	@Autowired
	AccountService accountService;
	
	@PostMapping
	public String registerUser(@RequestHeader("Authorization") String auth) {
		return accountService.registerUser(auth);
	}
	
	@PostMapping("/login")
	public UserDto loginUser(@RequestHeader("Authorization") String auth, HttpServletResponse response) {
		return accountService.loginUser(auth, response);
	}
	
	@GetMapping("/{login}/info")
	public UserDto userInformation(@PathVariable String login) {
		return accountService.userInformation(login);
	}
	
	@PutMapping("/{login}")
	public UserDto editUserProfile(@RequestBody EditUserDto editUser, @PathVariable String login, @RequestHeader("Authorization") String token) {
		return accountService.editUserProfile(editUser, login, token);
	}
	
	@DeleteMapping("/{login}")
	public UserDto removeUser(@PathVariable String login, @RequestHeader("Authorization") String token) {
		return accountService.removeUser(login, token);
	}
	
	@PutMapping("/{login}/roles")
	public Set<String> addUserRoles(@PathVariable String login, @RequestBody RoleDto roles, String token) {
		//access permitted only to admin
		return accountService.addRoles(login, roles, token);
	}
	
	@PutMapping("/{login}/block/{status}")
	public UserDto blockUserAccount(@PathVariable String login, @PathVariable boolean status, String token) {
		return accountService.blockUserAccount(login, status, token);
	}
	
	@PutMapping("/{login}/favorite/{id}")
	public Set<String> addUserFavorite(@PathVariable String login, @PathVariable String id, @RequestHeader("Authorization") String token) {
		return accountService.addUserFavorite(login, id, token);
	}
	
	@DeleteMapping("/{login}/favorite/{id}")
	public Set<String> removeUserFavorite(@PathVariable String login, @PathVariable String id, @RequestHeader("Authorization") String token) {
		return accountService.removeUserFavorite(login, id, token);
	}
	
	@GetMapping("/{login}/favorites")
	public Set<String> getUserFavorites(@PathVariable String login, @RequestHeader("Authorization") String token){
		return accountService.getUserFavorites(login, token);
	}
	
	@GetMapping
	public ResponseEntity<String> tokenValidation(@RequestHeader (value = "X-Token") String token){
		return accountService.tokenValidation(token); 
	}
	
}
