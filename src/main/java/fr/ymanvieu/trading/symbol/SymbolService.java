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

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@Service
public class SymbolService {

	@Autowired
	private SymbolRepository symbolRepo;

	// TODO add tests
	@Transactional
	public SymbolEntity addSymbol(String code, String name, String countryFlag, String currencyCode) {
		Objects.requireNonNull(code, "code is null");

		SymbolEntity se = symbolRepo.findByCode(code);

		// FIXME do not handle if symbol not found

		if (se == null) {
			se = new SymbolEntity(code);
			se.setName(name);
			se.setCountryFlag(countryFlag);

			if (currencyCode != null) {
				SymbolEntity seCurrency = symbolRepo.findByCode(currencyCode);

				if (seCurrency == null) {
					CurrencyInfo ci = getCurrencyInfo(currencyCode);
					seCurrency = addSymbol(ci.getCode(), ci.getName(), ci.getCountryFlag(), null);
				}

				se.setCurrency(seCurrency);
			}

			se = symbolRepo.save(se);
		}

		return se;
	}

	public List<SymbolEntity> getAll() {
		return symbolRepo.findAll();
	}

	public List<SymbolEntity> getAllWithCurrency(List<String> currencies) {
		return symbolRepo.findAllByCurrencyCodeInOrderByCode(currencies);
	}

	public List<SymbolEntity> getCurrencies() {
		return symbolRepo.findAllByCurrencyCodeIsNullOrderByCode();
	}

	public CurrencyInfo getCurrencyInfo(String code) {
		return new CurrencyInfo(code, nameForCurrency(code), countryFlagForCurrency(code));
	}

	public SymbolEntity getForCode(String code) {
		return symbolRepo.findByCode(code);
	}

	public void remove(String code) {
		symbolRepo.delete(code);
	}
}
