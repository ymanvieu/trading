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
package fr.ymanvieu.trading.common.provider;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.provider.entity.PairEntity;
import fr.ymanvieu.trading.common.provider.mapper.PairMapper;
import fr.ymanvieu.trading.common.provider.repository.PairRepository;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;

@Service
@Transactional
public class PairService {

	@Autowired
	private PairRepository pairRepo;
	
	@Autowired
	private PairMapper pairMapper;

	public Pair getForCodeAndProvider(String code, String provider) {
		return pairMapper.mapToPair(pairRepo.findBySymbolAndProviderCode(code, provider));
	}

	public Pair create(String code, String name, String source, String target, String exchange, String provider) {
		PairEntity pe = new PairEntity(code, name, new SymbolEntity(source), new SymbolEntity(target), exchange, provider);
		pe = pairRepo.save(pe);
		return pairMapper.mapToPair(pe);
	}

	public void remove(String symbol, String providerCode) {
		var pe = pairRepo.findBySymbolAndProviderCode(symbol, providerCode);
		
		if(pe == null) {
			throw PairException.notFound(symbol, providerCode);
		}
		
		pairRepo.delete(pe);
	}

	public List<UpdatedPair> getAllWithSymbolOrNameContaining(String symbolOrName) {
		return pairRepo.findAllUpdatedPairBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(symbolOrName, symbolOrName);
	}
	
	public List<Pair> getAllFromProvider(String providerCode) {
		return pairMapper.mapToPairs(pairRepo.findAllByProviderCode(providerCode));
	}

	public List<UpdatedPair> getAll() {
		return pairRepo.findAllUpdatedPair();
	}
}
