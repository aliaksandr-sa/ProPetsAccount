package propets.account.dto;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
//S
public class UserDto {
	String avatar;
	String name;
	String email;
	String phone;
	boolean block;
	Set<String> roles;
	Set<String> favorites;
}
