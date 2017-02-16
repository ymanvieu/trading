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
package fr.ymanvieu.trading.portofolio;

import static fr.ymanvieu.trading.util.MathUtils.equalsByComparingTo;
import static fr.ymanvieu.trading.util.MathUtils.percentChange;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.dsl.BooleanExpression;

import fr.ymanvieu.trading.exception.BusinessException;
import fr.ymanvieu.trading.portofolio.entity.AssetEntity;
import fr.ymanvieu.trading.portofolio.entity.PortofolioEntity;
import fr.ymanvieu.trading.portofolio.repository.PortofolioRepository;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.symbol.SymbolException;
import fr.ymanvieu.trading.symbol.entity.QSymbolEntity;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;
import fr.ymanvieu.trading.user.entity.UserEntity;
import fr.ymanvieu.trading.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class PortofolioService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PortofolioRepository portofolioRepo;

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private RateService rateService;

	@Transactional
	public Portofolio createPortofolio(String login, String baseCurrencyCode, int baseCurrencyAmount) {
		UserEntity ue = userRepo.findByLogin(login);
		SymbolEntity se = symbolRepo.findOne(baseCurrencyCode);

		// FIXME add checks

		PortofolioEntity pe = new PortofolioEntity(ue, se, new BigDecimal(baseCurrencyAmount));

		portofolioRepo.save(pe);

		return getPortofolio(login);
	}
	
	public AssetInfo getBaseCurrency(String login) {
		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		return new AssetInfo(portofolio.getBaseCurrency(), null, portofolio.getAmount().floatValue());
	}

	public Portofolio getPortofolio(String login) {
		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		AssetInfo baseCurrencyAsset = new AssetInfo(portofolio.getBaseCurrency(), null, portofolio.getAmount().floatValue());
		
		List<AssetInfo> assets = new ArrayList<>();

		float globalBaseCurrencyValueChange = 0f;
		float globalBaseCurrencyInvestedValue = 0f;

		for (AssetEntity ae : portofolio.getAssets()) {

			AssetInfo asset = getAsset(ae);

			String currencyCode = asset.getCurrency().getCode();

			BigDecimal assetCurrencyToBaseCurrencyRate = rateService.getLatest(currencyCode, portofolio.getBaseCurrency().getCode()).getPrice();
			BigDecimal baseCurrencyValueChange = assetCurrencyToBaseCurrencyRate.multiply(BigDecimal.valueOf(asset.getValueChange()));
			BigDecimal baseCurrencyInvestedValue = assetCurrencyToBaseCurrencyRate.multiply(BigDecimal.valueOf(asset.getValue()));

			globalBaseCurrencyValueChange += baseCurrencyValueChange.floatValue();
			globalBaseCurrencyInvestedValue += baseCurrencyInvestedValue.floatValue();

			assets.add(asset);
		}

		float globalPercentChange = 0;

		if (globalBaseCurrencyInvestedValue != 0) {
			globalPercentChange = globalBaseCurrencyValueChange / globalBaseCurrencyInvestedValue * 100f;
		}

		float globalBaseCurrencyValue = globalBaseCurrencyInvestedValue + globalBaseCurrencyValueChange;

		return new Portofolio(baseCurrencyAsset, assets, globalBaseCurrencyValue, globalPercentChange, globalBaseCurrencyValueChange);
	}

	protected AssetInfo getAsset(AssetEntity ae) {

		SymbolEntity currency = ae.getCurrency();

		AssetInfo asset = new AssetInfo(ae.getSymbol(), currency, ae.getQuantity().floatValue());

		Quote q = rateService.getLatest(ae.getSymbol().getCode(), currency.getCode());

		if (!equalsByComparingTo(ae.getQuantity(), ZERO)) {
			BigDecimal currentValue = q.getPrice().multiply(ae.getQuantity());
			float percentChange = percentChange(ae.getCurrencyAmount(), currentValue);

			asset.setValue(ae.getCurrencyAmount().floatValue());
			asset.setCurrentValue(currentValue.floatValue());
			asset.setValueChange(currentValue.subtract(ae.getCurrencyAmount()).floatValue());
			asset.setPercentChange(percentChange);
		}
		asset.setCurrentRate(q.getPrice().floatValue());

		return asset;
	}

	public OrderInfo getInfo(String login, String symbolCode, float quantity) throws SymbolException {

		Objects.requireNonNull(symbolCode, "symbolCode is null");

		SymbolEntity selectedSymbol = symbolRepo.findOne(symbolCode);

		if (selectedSymbol == null) {
			throw SymbolException.UNKNOWN(symbolCode);
		}

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		AssetEntity selectedAssetEntity = portofolio.getAsset(selectedSymbol.getCode());

		if (selectedAssetEntity == null) {
			selectedAssetEntity = new AssetEntity(portofolio, selectedSymbol, ZERO, portofolio.getCurrencyFor(selectedSymbol), ZERO);
		}

		String currencyCode = selectedAssetEntity.getCurrency().getCode();

		Quote q = rateService.getLatest(selectedSymbol.getCode(), currencyCode);

		AssetInfo selectedAsset = new AssetInfo(selectedAssetEntity.getSymbol(), selectedAssetEntity.getCurrency(),
				selectedAssetEntity.getQuantity().floatValue());

		selectedAsset.setCurrentRate(q.getPrice().floatValue());

		final AssetInfo selectedCurrency;

		if (selectedAssetEntity.getCurrency().equals(portofolio.getBaseCurrency())) {
			selectedCurrency = new AssetInfo(portofolio.getBaseCurrency(), null, portofolio.getAmount().floatValue());
		} else {
			AssetEntity currencyAE = portofolio.getAsset(selectedAssetEntity.getCurrency().getCode());
			selectedCurrency = new AssetInfo(currencyAE.getSymbol(), currencyAE.getCurrency(), currencyAE.getQuantity().floatValue());
		}

		BigDecimal gainCost = null;

		if (quantity != 0) {
			gainCost = q.getPrice().multiply(new BigDecimal(quantity));
		}

		return new OrderInfo(selectedAsset, selectedCurrency, gainCost);
	}

	/**
	 * Get all available symbols not already owned : <br>
	 * - currencies not already owned <br>
	 * - symbols which can be bought with owned currencies <br>
	 */
	public List<SymbolEntity> getAvailableSymbols(String login) {

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		List<SymbolEntity> ownedCurrencies = portofolio.getCurrencies();
		List<SymbolEntity> ownedAssets = portofolio.getAssets().stream().map(a -> a.getSymbol()).collect(Collectors.toList());

		QSymbolEntity qs = QSymbolEntity.symbolEntity;

		BooleanExpression exp = (qs.notIn(ownedAssets) //
				.and(qs.notIn(ownedCurrencies)) //
				.and(qs.currency.in(ownedCurrencies) //
						.or(qs.currency.isNull())));

		return symbolRepo.findAll(exp, qs.code.asc());
	}

	@Transactional(rollbackFor = BusinessException.class)
	public Order buy(String login, String code, float quantity) throws BusinessException {

		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity must be positive: " + quantity);
		}

		SymbolEntity fromSymbol = symbolRepo.findOne(code);

		if (fromSymbol == null) {
			throw SymbolException.UNKNOWN(code);
		}

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);
		Order order = portofolio.buy(fromSymbol, quantity, rateService);

		portofolioRepo.save(portofolio);

		return order;
	}

	@Transactional(rollbackFor = BusinessException.class)
	public Order sell(String login, String code, float quantity) throws BusinessException {

		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity must be positive: " + quantity);
		}

		final SymbolEntity fromSymbol = symbolRepo.findOne(code);

		if (fromSymbol == null) {
			throw SymbolException.UNKNOWN(code);
		}

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);
		Order order = portofolio.sell(fromSymbol, quantity, rateService);

		portofolioRepo.save(portofolio);

		return order;
	}
}