package app.vero.auth;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PasswordPolicy {

	private static final int MINIMUM_PASSWORD_BYTES = 12;
	private static final int MAXIMUM_PASSWORD_BYTES = 72;
	private static final Set<String> COMMON_PASSWORDS = Set.of(
			"12345", "123456", "12345678", "123456789", "1234567890",
			"password", "password1", "password123", "qwerty", "qwerty123",
			"letmein", "welcome", "admin", "admin123", "iloveyou", "abc123",
			"111111", "000000", "vero", "financeapp");

	public void validate(String password, String normalizedEmail) {
		if (password == null) {
			throw invalidPassword();
		}

		int passwordBytes = password.getBytes(StandardCharsets.UTF_8).length;
		if (passwordBytes < MINIMUM_PASSWORD_BYTES || passwordBytes > MAXIMUM_PASSWORD_BYTES) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Password must be between 12 and 72 bytes.");
		}

		String normalizedPassword = password.toLowerCase(Locale.ROOT);
		String emailLocalPart = normalizedEmail.substring(0, normalizedEmail.indexOf('@'));
		if (COMMON_PASSWORDS.contains(normalizedPassword)
				|| normalizedPassword.equals(normalizedEmail)
				|| normalizedPassword.equals(emailLocalPart)
				|| password.chars().distinct().count() == 1) {
			throw invalidPassword();
		}
	}

	private ResponseStatusException invalidPassword() {
		return new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"Choose a longer, less common password or passphrase.");
	}
}
