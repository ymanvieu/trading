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
package fr.ymanvieu.trading.common.admin;

import fr.ymanvieu.trading.common.exception.BusinessException;

public class AdminException extends BusinessException {

	private static final long serialVersionUID = 3847358680371605765L;

	private AdminException(String key, Object... args) {
		super(key, args);
	}
	
	public static AdminException currencyAlreadyExists(String code) {
		return new AdminException("admin.error.currency-already-exists", code);
	}
}