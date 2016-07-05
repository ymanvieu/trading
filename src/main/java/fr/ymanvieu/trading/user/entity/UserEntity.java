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
package fr.ymanvieu.trading.user.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(nullable = false, unique = true, length = 64)
	private String login;

	@Column(nullable = false, length = 64)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "VARCHAR(32) default 'USER'")
	private Role role = Role.USER;

	public UserEntity() {
	}

	public UserEntity(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public int hashCode() {
		return Objects.hash(login, password, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof UserEntity))
			return false;

		UserEntity other = (UserEntity) obj;

		return Objects.equals(login, other.login) //
				&& Objects.equals(password, other.password) //
				&& Objects.equals(role, other.role);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("login", login) //
				.add("password", "[PROTECTED]") //
				.add("role", role).toString();
	}
}
