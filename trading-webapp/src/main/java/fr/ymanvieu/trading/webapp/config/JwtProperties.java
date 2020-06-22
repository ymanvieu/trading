/**
 * Copyright (C) 2019 Yoann Manvieu
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
package fr.ymanvieu.trading.webapp.config;

import java.time.Duration;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("jwt")
@Getter
@Setter
@Valid
public class JwtProperties {

	@NotBlank
	private String header;
	@NotBlank
	private String secret;
	
	/**
	 * Access token expiration duration
	 */
	@NotNull
	private Duration expiration;
	
	/**
	 * Refresh token expiration duration
	 */
	@NotNull
	private Duration refreshExpiration;
}