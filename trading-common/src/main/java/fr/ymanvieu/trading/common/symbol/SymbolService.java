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

	public void delete(String symbolCode) {
		symbolRepo.deleteById(symbolCode);
		log.info("Symbol removed:{}", symbolCode);
	}
}
