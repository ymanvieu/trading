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
package fr.ymanvieu.trading.user.controller.form;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import fr.ymanvieu.trading.recaptcha.RecaptchaForm;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(includeFieldNames = true, exclude = { "password1", "password2" })
public class UserCreateForm extends RecaptchaForm {

	@NotEmpty
	@Size(min = 3, max = 64)
	private String login;

	@NotEmpty
	@Size(min = 8, max = 64)
	private String password1;

	private String password2;

	private boolean rememberMe;
}