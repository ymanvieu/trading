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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.expr.BooleanExpression;

import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.provider.entity.QPairEntity;
import fr.ymanvieu.trading.provider.repository.PairRepository;
import fr.ymanvieu.trading.symbol.util.SymbolUtils;

@Service
public class PairService {

	@Autowired
	private PairRepository pairRepo;

	public Pair getForCode(String symbol) {
		PairEntity pe = pairRepo.findBySymbol(symbol);

		return (pe == null) ? null : convert(pe);
	}

	@Transactional
	public Pair create(String code, String name, String source, String target, String provider) {
		PairEntity pe = new PairEntity(code, name, source, target, provider);
		pe = pairRepo.save(pe);
		return convert(pe);

	}

	@Transactional
	public void remove(String code) {
		pairRepo.delete(code);
	}

	private Pair convert(PairEntity pe) {
		return new Pair(pe.getSymbol(), pe.getName(), SymbolUtils.convert(pe.getSource()), SymbolUtils.convert(pe.getTarget()), pe.getProviderCode());
	}

	private List<Pair> convert(Iterable<PairEntity> pes) {
		List<Pair> pairs = new ArrayList<>();

		for (PairEntity pe : pes) {
			pairs.add(convert(pe));
		}

		return pairs;
	}

	public List<Pair> getAllWithSymbolOrNameContaining(String symbolOrName) {
		QPairEntity qe = QPairEntity.pairEntity;
		BooleanExpression builder = qe.symbol.containsIgnoreCase(symbolOrName).or(qe.name.containsIgnoreCase(symbolOrName));
		return convert(pairRepo.findAll(builder));
	}

	public List<Pair> getAll() {
		return convert(pairRepo.findAll());
	}
}
