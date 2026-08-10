package app.vero.auth;

import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import app.vero.auth.dto.LoginRequest;
import app.vero.auth.dto.RegisterRequest;
import app.vero.auth.dto.UserResponse;
import app.vero.user.UserAccount;
import app.vero.user.UserAccountRepository;

@Service
public class AuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final PasswordPolicy passwordPolicy;
	private final UserAccountRepository userAccountRepository;

	public AuthenticationService(
			AuthenticationManager authenticationManager,
			PasswordEncoder passwordEncoder,
			PasswordPolicy passwordPolicy,
			UserAccountRepository userAccountRepository) {
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.passwordPolicy = passwordPolicy;
		this.userAccountRepository = userAccountRepository;
	}

	@Transactional
	public UserResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());
		passwordPolicy.validate(request.password(), email);

		if (userAccountRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with that email already exists.");
		}

		UserAccount user = new UserAccount(
				UUID.randomUUID(),
				email,
				passwordEncoder.encode(request.password()),
				request.displayName().trim(),
				request.baseCurrency().toUpperCase(Locale.ROOT));

		return UserResponse.from(userAccountRepository.save(user));
	}

	public Authentication login(LoginRequest request) {
		return authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(normalizeEmail(request.email()), request.password()));
	}

	public static String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
