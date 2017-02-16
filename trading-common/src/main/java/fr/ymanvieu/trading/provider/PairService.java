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
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.querydsl.core.types.dsl.BooleanExpression;

import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.provider.entity.QPairEntity;
import fr.ymanvieu.trading.provider.repository.PairRepository;

@Service
@Transactional(readOnly = true)
public class PairService {

	@Autowired
	private PairRepository pairRepo;

	@Autowired
	private LookupService clService;

	public PairEntity getForCode(String symbol) {
		PairEntity pe = pairRepo.findBySymbol(symbol);
		return pe;
	}

	@Transactional
	public PairEntity create(String code, String name, String source, String target, String provider) {
		PairEntity pe = new PairEntity(code, name, source, target, provider);
		pe = pairRepo.save(pe);
		return pe;

	}

	@Transactional
	public void remove(String code) {
		pairRepo.delete(code);
	}

	@VisibleForTesting
	public List<PairEntity> getAllWithSymbolOrNameContaining(String symbolOrName) {
		QPairEntity qe = QPairEntity.pairEntity;
		BooleanExpression builder = qe.symbol.containsIgnoreCase(symbolOrName).or(qe.name.containsIgnoreCase(symbolOrName));
		return pairRepo.findAll(builder);
	}

	public List<PairEntity> getAll() {
		return pairRepo.findAll();
	}

	public PairsResult search(String code) throws IOException {
		final List<PairEntity> existingSymbols;
		final List<LookupInfo> availableSymbols;

		if (Strings.isNullOrEmpty(code)) {
			existingSymbols = pairRepo.findAll();
			availableSymbols = new ArrayList<>();
		} else {
			existingSymbols = getAllWithSymbolOrNameContaining(code);
			availableSymbols = clService.search(code);
			removeDuplicates(availableSymbols, existingSymbols);
		}

		return new PairsResult(existingSymbols, availableSymbols);
	}

	private void removeDuplicates(List<LookupInfo> availableSymbols, List<PairEntity> existingSymbols) {
		availableSymbols.removeIf(as -> existingSymbols.stream().map(s -> s.getSymbol()).anyMatch(s -> s.equals(as.getCode())));
	}
}
