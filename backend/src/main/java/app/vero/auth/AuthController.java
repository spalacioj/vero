package app.vero.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import app.vero.auth.dto.LoginRequest;
import app.vero.auth.dto.RegisterRequest;
import app.vero.auth.dto.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

	private final AuthenticationService authenticationService;
	private final SecurityContextRepository securityContextRepository;
	private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
	private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

	public AuthController(
			AuthenticationService authenticationService,
			SecurityContextRepository securityContextRepository,
			SessionAuthenticationStrategy sessionAuthenticationStrategy) {
		this.authenticationService = authenticationService;
		this.securityContextRepository = securityContextRepository;
		this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse register(@Valid @RequestBody RegisterRequest request) {
		return authenticationService.register(request);
	}

	@PostMapping("/login")
	public UserResponse login(
			@Valid @RequestBody LoginRequest request,
			HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {
		try {
			Authentication authentication = authenticationService.login(request);
			sessionAuthenticationStrategy.onAuthentication(authentication, servletRequest, servletResponse);

			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);
			securityContextRepository.saveContext(context, servletRequest, servletResponse);

			return UserResponse.from((VeroUserPrincipal) authentication.getPrincipal());
		} catch (AuthenticationException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
		}
	}

	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken csrfToken) {
		return csrfToken;
	}

	@GetMapping("/me")
	public UserResponse currentUser(@AuthenticationPrincipal VeroUserPrincipal principal) {
		return UserResponse.from(principal);
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(
			HttpServletRequest servletRequest,
			HttpServletResponse servletResponse,
			Authentication authentication) {
		logoutHandler.logout(servletRequest, servletResponse, authentication);
	}
}
