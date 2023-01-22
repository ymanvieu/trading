package fr.ymanvieu.trading.common.user.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "authorities")
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class AuthorityEntity implements Serializable {

	private static final long serialVersionUID = 6237201323904853508L;

	@Id
	@ManyToOne
	private UserEntity user;

	@Id
	@Column(name = "authority", length = 64, nullable = false)
	private String authority;

	protected AuthorityEntity() {
	}
}
