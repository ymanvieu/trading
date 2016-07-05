/**
 *	Copyright (C) 2016	Yoann Manvieu
 *
 *	This software is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.trading.exception;

public class BusinessException extends Exception {

	private static final long serialVersionUID = 5894630703257348547L;

	private final Object[] args;

	private final String key;

	protected BusinessException(String key, Object... args) {
		this.key = key;
		this.args = args;
	}

	public String getKey() {
		return key;
	}

	public Object[] getArgs() {
		return args;
	}
	
	@Override
	public String getMessage() {
		return super.getMessage();
	}
}