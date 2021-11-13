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
package fr.ymanvieu.trading.common.symbol;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.symbol.entity.FavoriteSymbolEntity;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.symbol.mapper.SymbolMapper;
import fr.ymanvieu.trading.common.symbol.repository.FavoriteSymbolRepository;
import fr.ymanvieu.trading.common.symbol.repository.SymbolRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class SymbolService {

	@Autowired
	private SymbolRepository symbolRepo;
	
	@Autowired
	private FavoriteSymbolRepository favoriteSymbolRepository;
	
	@Autowired
	private SymbolMapper symbolMapper;

	// TODO add tests

	public Symbol addSymbol(String code, String name, String countryFlag, String currencyCode) {
		requireNonNull(code, "code is null");
		requireNonNull(name, "name is null");
		
		if(symbolRepo.existsById(code)) {
			throw SymbolException.alreadyExists(code);
		}

		SymbolEntity se = new SymbolEntity(code);
		se.setName(name);
		se.setCountryFlag(countryFlag);
		
		if(currencyCode != null) {
			se.setCurrency(new SymbolEntity(currencyCode));
		}

		return symbolMapper.mapToSymbol(symbolRepo.save(se));
	}

	public Optional<Symbol> getForCode(String code) {
		return symbolRepo.findById(code).map(symbolMapper::mapToSymbol);
	}
	
	public Optional<Symbol> getForCodeWithNoCurrency(String code) {
		return symbolRepo.findOneByCodeAndCurrencyIsNull(code).map(symbolMapper::mapToSymbol);
	}

	public void addFavoriteSymbol(String fromSymbolCode, String toSymbolCode, Integer userId) {
		favoriteSymbolRepository.save(new FavoriteSymbolEntity(userId, fromSymbolCode, toSymbolCode));
		log.info("Favorite symbol added:{}/{} user:{}", fromSymbolCode, toSymbolCode, userId);
	}
	
	public void deleteFavoriteSymbol(String fromSymbolCode, String toSymbolCode, Integer userId) {
		favoriteSymbolRepository.deleteByFromSymbolCodeAndToSymbolCodeAndUserId(fromSymbolCode, toSymbolCode, userId);
		log.info("Favorite symbol removed:{}/{} user:{}", fromSymbolCode, toSymbolCode, userId);
	}
}
