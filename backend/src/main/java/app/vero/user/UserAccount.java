package app.vero.user;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserAccount {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "display_name", nullable = false, length = 100)
	private String displayName;

	@Column(name = "base_currency", nullable = false, length = 3)
	private String baseCurrency;

	protected UserAccount() {
	}

	public UserAccount(UUID id, String email, String passwordHash, String displayName, String baseCurrency) {
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.displayName = displayName;
		this.baseCurrency = baseCurrency;
	}

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}
}
