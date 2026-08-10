package app.vero.auth;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import app.vero.user.UserAccount;

public record VeroUserPrincipal(
		UUID id,
		String email,
		String passwordHash,
		String displayName,
		String baseCurrency) implements UserDetails {

	public static VeroUserPrincipal from(UserAccount user) {
		return new VeroUserPrincipal(
				user.getId(),
				user.getEmail(),
				user.getPasswordHash(),
				user.getDisplayName(),
				user.getBaseCurrency());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return email;
	}
}
