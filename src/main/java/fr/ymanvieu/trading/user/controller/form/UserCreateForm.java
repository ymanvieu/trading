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

import static com.google.common.base.MoreObjects.toStringHelper;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class UserCreateForm extends RecaptchaForm {

	@NotEmpty
	@Size(min = 3, max = 64)
	private String login;
	
	@NotEmpty
	@Size(min = 8, max = 64)
	private String password1;
	
	private String password2;

	private boolean rememberMe;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	@Override
	public String getRecaptchaResponse() {
		return recaptchaResponse;
	}

	@Override
	public void setRecaptchaResponse(String recaptchaResponse) {
		this.recaptchaResponse = recaptchaResponse;
	}

	@Override
	public String toString() {
		return toStringHelper(this) //
				.add("login", login) //
				.add("password1", "****") //
				.add("password2", "****") //
				.add("rememberMe", rememberMe) //
				.add("recaptchaResponse", recaptchaResponse).toString();
	}

}
