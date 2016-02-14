/**
 * Copyright (C) 2015 Yoann Manvieu
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
package fr.ymanvieu.forex.core.util;

import java.text.MessageFormat;

public class StringUtils {

	public static String toOneLine(String str) {
		if (str == null)
			throw new IllegalArgumentException("str is null");

		return str.replaceAll("[\r\n]+", " ");
	}
	
	public static String format(String text, Object... args) {
		return (text == null) ? null : MessageFormat.format(text, args);
	}
}
