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
package fr.ymanvieu.trading.common.portofolio;

import static com.querydsl.jpa.JPAExpressions.select;
import static fr.ymanvieu.trading.common.util.MathUtils.equalsByComparingTo;
import static fr.ymanvieu.trading.common.util.MathUtils.percentChange;
import static fr.ymanvieu.trading.common.provider.entity.QPairEntity.pairEntity;
import static fr.ymanvieu.trading.common.symbol.entity.QSymbolEntity.symbolEntity;
import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.dsl.BooleanExpression;

import fr.ymanvieu.trading.common.portofolio.entity.AssetEntity;
import fr.ymanvieu.trading.common.portofolio.entity.PortofolioEntity;
import fr.ymanvieu.trading.common.portofolio.repository.PortofolioRepository;
import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.common.symbol.SymbolException;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.symbol.mapper.SymbolMapper;
import fr.ymanvieu.trading.common.symbol.repository.SymbolRepository;
import fr.ymanvieu.trading.common.user.entity.UserEntity;
import fr.ymanvieu.trading.common.user.repository.UserRepository;

@Service
@Transactional
public class PortofolioService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PortofolioRepository portofolioRepo;

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private RateService rateService;
	
	@Autowired
	private SymbolMapper symbolMapper;

	public Portofolio createPortofolio(String login, String baseCurrencyCode, int baseCurrencyAmount) {
		UserEntity ue = userRepo.findByUsername(login);
		SymbolEntity se = symbolRepo.findById(baseCurrencyCode).get();

		// FIXME add checks

		PortofolioEntity pe = new PortofolioEntity(ue, se, BigDecimal.valueOf(baseCurrencyAmount));

		portofolioRepo.save(pe);

		return getPortofolio(login);
	}
	
	public AssetInfo getBaseCurrency(String login) {
		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		return new AssetInfo(portofolio.getBaseCurrency(), null, portofolio.getAmount().doubleValue());
	}

	public Portofolio getPortofolio(String login) {
		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		AssetInfo baseCurrencyAsset = new AssetInfo(portofolio.getBaseCurrency(), null, portofolio.getAmount().doubleValue());
		
		List<AssetInfo> assets = new ArrayList<>();

		double globalBaseCurrencyValueChange = 0f;
		double globalBaseCurrencyInvestedValue = 0f;

		for (AssetEntity ae : portofolio.getAssets()) {

			AssetInfo asset = getAsset(ae);

			String currencyCode = asset.getCurrency().getCode();

			BigDecimal assetCurrencyToBaseCurrencyRate = rateService.getLatest(currencyCode, portofolio.getBaseCurrency().getCode()).getValue();
			BigDecimal baseCurrencyValueChange = assetCurrencyToBaseCurrencyRate.multiply(BigDecimal.valueOf(asset.getValueChange()));
			BigDecimal baseCurrencyInvestedValue = assetCurrencyToBaseCurrencyRate.multiply(BigDecimal.valueOf(asset.getValue()));

			globalBaseCurrencyValueChange += baseCurrencyValueChange.doubleValue();
			globalBaseCurrencyInvestedValue += baseCurrencyInvestedValue.doubleValue();

			assets.add(asset);
		}

		double globalPercentChange = 0;

		if (globalBaseCurrencyInvestedValue != 0) {
			globalPercentChange = globalBaseCurrencyValueChange / globalBaseCurrencyInvestedValue * 100d;
		}

		double globalBaseCurrencyValue = globalBaseCurrencyInvestedValue + globalBaseCurrencyValueChange;

		return new Portofolio(baseCurrencyAsset, assets, globalBaseCurrencyValue, globalPercentChange, globalBaseCurrencyValueChange);
	}

	protected AssetInfo getAsset(AssetEntity ae) {

		SymbolEntity currency = ae.getCurrency();

		AssetInfo asset = new AssetInfo(ae.getSymbol(), currency, ae.getQuantity().doubleValue());

		Rate q = rateService.getLatest(ae.getSymbol().getCode(), currency.getCode());

		if (!equalsByComparingTo(ae.getQuantity(), ZERO)) {
			BigDecimal currentValue = q.getValue().multiply(ae.getQuantity());
			double percentChange = percentChange(ae.getCurrencyAmount(), currentValue);

			asset.setValue(ae.getCurrencyAmount().doubleValue());
			asset.setCurrentValue(currentValue.doubleValue());
			asset.setValueChange(currentValue.subtract(ae.getCurrencyAmount()).doubleValue());
			asset.setPercentChange(percentChange);
		}
		asset.setCurrentRate(q.getValue().doubleValue());

		return asset;
	}

	public OrderInfo getOrderInfo(String login, String symbolCode, double quantity) {

		Objects.requireNonNull(symbolCode, "symbolCode is null");

		SymbolEntity selectedSymbol = symbolRepo.findById(symbolCode).get();

		if (selectedSymbol == null) {
			throw SymbolException.UNKNOWN(symbolCode);
		}

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		AssetEntity selectedAssetEntity = portofolio.getAsset(selectedSymbol.getCode());

		if (selectedAssetEntity == null) {
			selectedAssetEntity = new AssetEntity(portofolio, selectedSymbol, ZERO, portofolio.getCurrencyFor(selectedSymbol), ZERO);
		}

		String currencyCode = selectedAssetEntity.getCurrency().getCode();

		Rate q = rateService.getLatest(selectedSymbol.getCode(), currencyCode);

		AssetInfo selectedAsset = new AssetInfo(selectedAssetEntity.getSymbol(), selectedAssetEntity.getCurrency(),
				selectedAssetEntity.getQuantity().doubleValue());

		selectedAsset.setCurrentRate(q.getValue().doubleValue());

		final AssetInfo selectedCurrency;

		if (selectedAssetEntity.getCurrency().equals(portofolio.getBaseCurrency())) {
			selectedCurrency = new AssetInfo(portofolio.getBaseCurrency(), null, portofolio.getAmount().doubleValue());
		} else {
			AssetEntity currencyAE = portofolio.getAsset(selectedAssetEntity.getCurrency().getCode());
			selectedCurrency = new AssetInfo(currencyAE.getSymbol(), currencyAE.getCurrency(), currencyAE.getQuantity().doubleValue());
		}

		BigDecimal gainCost = null;

		if (quantity != 0) {
			gainCost = q.getValue().multiply(BigDecimal.valueOf(quantity));
		}

		return new OrderInfo(selectedAsset, selectedCurrency, gainCost);
	}

	/**
	 * Get all available symbols not already owned : <br>
	 * - currencies not already owned and still collected <br>
	 * - symbols which can be bought with owned currencies <br>
	 */
	public List<Symbol> getAvailableSymbols(String login) {

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		List<SymbolEntity> ownedCurrencies = portofolio.getCurrencies();
		List<SymbolEntity> ownedAssets = portofolio.getAssets().stream().map(a -> a.getSymbol()).collect(toList());

		BooleanExpression exp = symbolEntity.notIn(ownedAssets) //
				.and(symbolEntity.notIn(ownedCurrencies)) //
				.and(symbolEntity.currency.in(ownedCurrencies) //
						.and(symbolEntity.code.in(select(pairEntity.source.code).from(pairEntity))) //
						.or(symbolEntity.currency.isNull()));

		return symbolMapper.mapToSymbols(symbolRepo.findAll(exp, symbolEntity.code.asc()));
	}

	public Order buy(String login, String code, double quantity) {

		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity must be positive: " + quantity);
		}

		SymbolEntity fromSymbol = symbolRepo.findById(code).get();

		if (fromSymbol == null) {
			throw SymbolException.UNKNOWN(code);
		}

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);
		Order order = portofolio.buy(fromSymbol, quantity, rateService);

		portofolioRepo.save(portofolio);

		return order;
	}

	public Order sell(String login, String code, double quantity) {

		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity must be positive: " + quantity);
		}

		final SymbolEntity fromSymbol = symbolRepo.findById(code).get();

		if (fromSymbol == null) {
			throw SymbolException.UNKNOWN(code);
		}

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);
		Order order = portofolio.sell(fromSymbol, quantity, rateService);

		portofolioRepo.save(portofolio);

		return order;
	}
}