/**
 * Copyright (C) 2016 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.trading.common.user.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
