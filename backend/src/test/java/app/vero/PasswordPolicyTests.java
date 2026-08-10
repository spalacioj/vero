package app.vero;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import app.vero.auth.PasswordPolicy;

class PasswordPolicyTests {

	private final PasswordPolicy passwordPolicy = new PasswordPolicy();

	@Test
	void rejectsShortPasswordsSuchAs12345() {
		assertThrows(ResponseStatusException.class,
				() -> passwordPolicy.validate("12345", "samuel@example.com"));
	}

	@Test
	void rejectsCommonPasswordsEvenWhenTheyMeetTheLengthRequirement() {
		assertThrows(ResponseStatusException.class,
				() -> passwordPolicy.validate("password123", "samuel@example.com"));
	}

	@Test
	void acceptsALongPassphrase() {
		assertDoesNotThrow(() -> passwordPolicy.validate("violet river ember 2026", "samuel@example.com"));
	}
}
