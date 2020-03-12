package propets.account.security;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import propets.account.dao.AccountRepository;

public class CustomFilter extends GenericFilterBean {

	@Autowired
	AccountRepository accountRepository;
	@Autowired
	TokenService tokenService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String auth = request.getHeader("X-Token");
		String path = request.getServletPath();
		String method = request.getMethod();
		System.out.println(auth);
		System.out.println(path);
		System.out.println(method);
		if (!checkPointCut(path, method)) {
			Claims claims;
			try {
				claims = tokenService.verifyJwt(auth);
				System.out.println(claims.toString());
			} catch (Exception e) {
				response.sendError(401, "Header X-Token is not valid");
				System.out.println("error");
				return;
			}
			
			String login = claims.getId();
			String jwt = tokenService.createJWT(login);
			response.addHeader("X-Token", jwt);
			chain.doFilter(new WrapperRequest(request, login), response);
			return;
			
		}else {
			chain.doFilter(request, response);
		}
		

	}

	private boolean checkPointCut(String path, String method) {
		return path.matches("/\\w*/account/v1/login") && "POST".equalsIgnoreCase(method);
	}

	private class WrapperRequest extends HttpServletRequestWrapper {

		String user;

		public WrapperRequest(HttpServletRequest request, String user) {
			super(request);
			this.user = user;
		}

		@Override
		public Principal getUserPrincipal() {
			return new Principal() { // or return () -> user;

				@Override
				public String getName() {
					return user;
				}
			};
		}
	}

}
