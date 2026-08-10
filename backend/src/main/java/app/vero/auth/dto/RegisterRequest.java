package app.vero.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Size(min = 2, max = 100) String displayName,
		@NotBlank @Email @Size(max = 255) String email,
		@NotBlank String password,
		@NotBlank @Pattern(regexp = "^[A-Za-z]{3}$") String baseCurrency) {
}
