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
package fr.ymanvieu.trading.common.provider.lookup;

import java.io.IOException;
import java.util.List;

import fr.ymanvieu.trading.common.provider.LookupDetails;
import fr.ymanvieu.trading.common.provider.LookupInfo;

public interface LookupProvider {

	List<LookupInfo> search(String symbolOrName) throws IOException;

	String getProviderCode();

	LookupDetails getDetails(String code) throws IOException;
}
