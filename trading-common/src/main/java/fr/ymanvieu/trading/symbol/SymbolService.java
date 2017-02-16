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
package fr.ymanvieu.trading.symbol;

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.countryFlagForCurrency;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.nameForCurrency;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@Service
@Transactional(readOnly = true)
public class SymbolService {

	@Autowired
	private SymbolRepository symbolRepo;

	// TODO add tests

	/**
	 * @throws IllegalArgumentException
	 *             if currencyCode is not found.
	 */
	@Transactional
	public SymbolEntity addSymbol(String code, String name, String countryFlag, String currencyCode) {
		Objects.requireNonNull(code, "code is null");

		SymbolEntity se = symbolRepo.findOne(code);

		if (se == null) {
			se = new SymbolEntity(code);
			se.setName(name);
			se.setCountryFlag(countryFlag);

			if (currencyCode != null) {
				SymbolEntity seCurrency = symbolRepo.findOne(currencyCode);

				if (seCurrency == null) {
					throw new IllegalArgumentException("currencyCode not found: " + currencyCode);
				}

				se.setCurrency(seCurrency);
			}

			se = symbolRepo.save(se);
		}

		return se;
	}

	public CurrencyInfo getCurrencyInfo(String code) {
		return new CurrencyInfo(code, nameForCurrency(code), countryFlagForCurrency(code));
	}

	public SymbolEntity getForCode(String code) {
		return symbolRepo.findOne(code);
	}
}
