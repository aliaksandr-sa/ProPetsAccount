package propets.account.security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenService {
	
	private static String SECRET_KEY = "123_pasWoRD_QweRtY_456_pe4enka_snEgUr04ka_789_SHliapA_neZnajKaNaLuNE";
	private static long TERM = 900000;
	
	public String[] decodeToken(String token) {
		int pos = token.indexOf(" ");
		String newToken = token.substring(pos + 1);
		byte[] decodeBytes = Base64.getDecoder().decode(newToken);
		String credential = new String(decodeBytes);
		String[] credentials = credential.split(":");
		return credentials;
	}

	public String getLoginFromCredential(String token) {
		String[] credential = decodeToken(token);
		return credential[0];
	}

	public String createJWT(String id) {
		return Jwts.builder()
				.setId(id)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TERM))
				.signWith(SignatureAlgorithm.HS256, 
						new SecretKeySpec(DatatypeConverter.parseBase64Binary(SECRET_KEY),
								SignatureAlgorithm.HS256.getJcaName()))
				.compact();
				
	}
	
	public Claims verifyJwt(String jwt) {
		Claims claims = Jwts.parser()
				.setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
				.parseClaimsJws(jwt)
				.getBody();	
		System.out.println("qwe");
		return claims;
	}
}
