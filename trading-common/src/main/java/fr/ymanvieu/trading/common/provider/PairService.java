package fr.ymanvieu.trading.common.provider;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.provider.entity.PairEntity;
import fr.ymanvieu.trading.common.provider.mapper.PairMapper;
import fr.ymanvieu.trading.common.provider.repository.PairRepository;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.symbol.repository.SymbolRepository;

@Service
@Transactional
public class PairService {

	@Autowired
	private PairRepository pairRepo;

	@Autowired
	private SymbolRepository symbolRepository;
	
	@Autowired
	private PairMapper pairMapper;

	public Pair getForCodeAndProvider(String code, String provider) {
		return pairMapper.mapToPair(pairRepo.findBySymbolAndProviderCode(code, provider));
	}

	public Pair create(String code, String name, String source, String target, String exchange, String provider) {
		var sourceSymbol = symbolRepository.findById(source).orElseThrow();
		var targetSymbol = symbolRepository.findById(target).orElseThrow();

		PairEntity pe = new PairEntity(code, name, sourceSymbol, targetSymbol, exchange, provider);
		pe = pairRepo.save(pe);
		return pairMapper.mapToPair(pe);
	}

	public Pair remove(Integer pairId) {
		var pe = pairRepo.findById(pairId).orElseThrow(() -> PairException.notFound(pairId));
		
		pairRepo.delete(pe);
		return pairMapper.mapToPair(pe);
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

	public Pair getForId(Integer pairId) {
		return pairRepo.findById(pairId).map(pairMapper::mapToPair).orElse(null);
	}

    public boolean existsAsSource(String symbolCode) {
		return pairRepo.existsBySourceCode(symbolCode);
    }
}
