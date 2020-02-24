package propets.account.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import propets.account.dao.AccountRepository;
import propets.account.domain.User;
import propets.account.exceptions.ConflictException;

@Service
//S
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	AccountRepository accountRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = accountRepository.findById(username).orElseThrow(() -> new ConflictException());
		String password = user.getPassword();
		Set<String> roles = user.getRoles();
		
		return new org.springframework.security.core.userdetails.User(username, password, 
				AuthorityUtils.createAuthorityList(roles.toArray(new String[roles.size()])));
	}

}
