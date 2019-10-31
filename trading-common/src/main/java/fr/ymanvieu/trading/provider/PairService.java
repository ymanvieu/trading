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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.provider.repository.PairRepository;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

@Service
@Transactional(readOnly = true)
public class PairService {

	@Autowired
	private PairRepository pairRepo;

	public PairEntity getForCodeAndProvider(String code, String provider) {
		return pairRepo.findBySymbolAndProviderCode(code, provider);
	}

	@Transactional
	public PairEntity create(String code, String name, String source, String target, String exchange, String provider) {
		PairEntity pe = new PairEntity(code, name, new SymbolEntity(source), new SymbolEntity(target), exchange, provider);
		pe = pairRepo.save(pe);
		return pe;
	}

	@Transactional
	public void remove(PairEntity pe) {
		pairRepo.delete(pe);
	}

	public List<PairEntity> getAllWithSymbolOrNameContaining(String symbolOrName) {
		return pairRepo.findAllBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(symbolOrName, symbolOrName);
	}
	
	public List<PairEntity> getAllFromProvider(String providerCode) {
		return pairRepo.findAllByProviderCode(providerCode);
	}

	public List<PairEntity> getAll() {
		return pairRepo.findAll();
	}
}
