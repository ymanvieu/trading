package fr.ymanvieu.trading.common.user.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "users")
@ToString(exclude="password")
@Getter
@Accessors(chain = true)
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Setter
	@Nonnull
	@Column(nullable = false, unique = true, length = 255)
	private String username;

	@Setter
	@Column(length = 64)
	private String password;

	@Nonnull
	@Column(nullable = false, columnDefinition = "bool default true")
	private boolean enabled;

	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user")
	private List<AuthorityEntity> authorities = new ArrayList<>();

	@LastModifiedDate
	@Column(name = "last_modified_date")
	private Instant lastModifiedDate;

	@Nonnull
	@Column(nullable = false, length = 255)
	private String provider;

	@Setter
	@Column(length = 255)
	private String providerUserId;

	@Setter
	@Column(length = 255)
	private String email;
}
