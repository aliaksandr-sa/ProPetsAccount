package propets.account.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import propets.account.dto.BlockUserDto;
import propets.account.dto.EditUserDto;
import propets.account.dto.NewUserDto;
import propets.account.dto.RegisterUserDto;
import propets.account.dto.UserDto;
import propets.account.service.AccountService;

@RestController
@RequestMapping("/{lang}/account/v1")
public class AccountController {

	@Autowired
	AccountService accountService;
	
	@PostMapping
	public RegisterUserDto registerUser(@RequestBody NewUserDto newUser) {
		return accountService.registerUser(newUser);
	}
	
	@PostMapping("/login")
	public UserDto loginUser(@RequestBody String token) {
		return accountService.loginUser(token);
	}
	
	@GetMapping("/{login}/info")
	public UserDto userInformation(@PathVariable String login, String token) {
		return accountService.userInformation(login, token);
	}
	
	@PutMapping("/{login}")
	public UserDto editUserProfile(@RequestBody EditUserDto editUser, @PathVariable String login, String token) {
		return accountService.editUserProfile(editUser, login, token);
	}
	
	@DeleteMapping("/{login}")
	public UserDto removeUser(@PathVariable String login, String token) {
		return accountService.removeUser(login, token);
	}
	
	@PutMapping("/{login}/roles")
	//Nado vozvraschat' UserDto
	public Set<String> addUserRoles(@PathVariable String login, @RequestBody String role, String token) {
		//access permitted only to admin
		return accountService.addRoles(login, role, token);
	}
	
	@PutMapping("/{login}/block/{status}")
	public UserDto blockUserAccount(@PathVariable String login, @RequestBody BlockUserDto blockUser, String token, @PathVariable boolean status) {
		return accountService.blockUserAccount(login, blockUser, token, status);
	}
	
	@PutMapping("/{login}/favorite/{id}")
	public Set<String> addUserFavorite(@PathVariable String login, @PathVariable String id, String token) {
		return accountService.addUserFavorite(login, id, token);
	}
	
	@DeleteMapping("/{login}/favorite/{id}")
	//nado vozvr... 1 ID, a ne Iterable
	public Set<String> removeUserFavorite(@PathVariable String login, @PathVariable String id, String token) {
		return accountService.removeUserFavorite(login, id, token);
	}
	
	@GetMapping("/{login}/favorites")
	public Set<String> getUserFavorites(@PathVariable String login, String token){
		return accountService.getUserFavorites(login, token);
	}
	
}
