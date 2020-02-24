package propets.account.configuration;

import java.util.Base64;

import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedResource;

import propets.account.exceptions.UserAuthenticationException;

@Configuration
@ManagedResource
public class UserConfiguration {
	public UserCredentials tokenDecode(String auth) {
		try {
			int pos = auth.indexOf(" ");
			String token = auth.substring(pos + 1);
			byte[] decodeBytes = Base64.getDecoder().decode(token);
			String credential = new String(decodeBytes);
			String[] credentials = credential.split(":");
			return new UserCredentials(credentials[0], credentials[1]);
		} catch (Exception e) {
			throw new UserAuthenticationException();
		}
	}
}
