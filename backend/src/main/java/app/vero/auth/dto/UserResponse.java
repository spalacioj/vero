package app.vero.auth.dto;

import java.util.UUID;

import app.vero.auth.VeroUserPrincipal;
import app.vero.user.UserAccount;

public record UserResponse(UUID id, String displayName, String email, String baseCurrency) {

	public static UserResponse from(UserAccount user) {
		return new UserResponse(user.getId(), user.getDisplayName(), user.getEmail(), user.getBaseCurrency());
	}

	public static UserResponse from(VeroUserPrincipal principal) {
		return new UserResponse(
				principal.id(), principal.displayName(), principal.email(), principal.baseCurrency());
	}
}
