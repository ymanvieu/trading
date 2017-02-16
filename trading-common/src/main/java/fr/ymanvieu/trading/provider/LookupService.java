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
package fr.ymanvieu.trading.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ymanvieu.trading.provider.lookup.LookupProvider;

@Service
public class LookupService {

	@Autowired
	private List<LookupProvider> lProviders;

	public List<LookupInfo> search(String symbolOrName) throws IOException {

		List<LookupInfo> result = new ArrayList<>();

		for (LookupProvider lp : lProviders) {
			result.addAll(lp.search(symbolOrName));
		}

		return result;
	}

	public LookupDetails getDetails(String code, String provider) throws IOException, ProviderException {
		for (LookupProvider lp : lProviders) {
			if (lp.getProviderCode().equals(provider)) {
				return lp.getDetails(code);
			}
		}

		return null;
	}

}
