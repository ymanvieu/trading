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

import static fr.ymanvieu.trading.symbol.util.SymbolUtils.convert;
import static fr.ymanvieu.trading.util.MathUtils.percentChange;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ymanvieu.trading.portofolio.entity.AssetEntity;
import fr.ymanvieu.trading.portofolio.entity.PortofolioEntity;
import fr.ymanvieu.trading.portofolio.repository.AssetRepository;
import fr.ymanvieu.trading.portofolio.repository.PortofolioRepository;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.symbol.SymbolService;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.user.entity.UserEntity;
import fr.ymanvieu.trading.user.repository.UserRepository;

@Service
public class PortofolioService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AssetRepository assetRepo;

	@Autowired
	private PortofolioRepository portofolioRepo;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private RateService rateService;

	public Portofolio createPortofolio(String login, String baseCurrencyCode, int baseCurrencyAmount) {
		UserEntity ue = userRepo.findByLogin(login);
		SymbolEntity se = symbolService.getForCode(baseCurrencyCode);

		// FIXME add checks

		BigDecimal amount = new BigDecimal(baseCurrencyAmount);

		AssetEntity ae = new AssetEntity(ue, se, amount, amount);
		PortofolioEntity pe = new PortofolioEntity(ae);

		assetRepo.save(ae);
		portofolioRepo.saveAndFlush(pe);

		return getPortofolio(login);
	}

	public Portofolio getPortofolio(String login) {
		List<AssetEntity> assetEntities = assetRepo.findAllByUserLoginOrderBySymbolName(login);

		Asset baseCurrency = getAsset(portofolioRepo.findByAssetUserLogin(login).getAsset());

		List<Asset> assets = new ArrayList<>();

		float globalBaseCurrencyValueChange = 0f;
		float globalBaseCurrencyInvestedValue = 0f;

		for (AssetEntity ae : assetEntities) {

			if (baseCurrency.getSymbol().getCode().equals(ae.getSymbol().getCode())) {
				continue;
			}

			Asset asset = getAsset(ae);

			String currencyCode = asset.getCurrency().getCode();

			BigDecimal assetCurrencyToBaseCurrencyRate = rateService.getLatest(currencyCode, baseCurrency.getSymbol().getCode()).getPrice();
			BigDecimal baseCurrencyValueChange = assetCurrencyToBaseCurrencyRate.multiply(BigDecimal.valueOf(asset.getValueChange()));
			BigDecimal baseCurrencyInvestedValue = assetCurrencyToBaseCurrencyRate.multiply(BigDecimal.valueOf(asset.getValue()));

			globalBaseCurrencyValueChange += baseCurrencyValueChange.floatValue();
			globalBaseCurrencyInvestedValue += baseCurrencyInvestedValue.floatValue();

			assets.add(asset);
		}

		float globalPercentChange = (globalBaseCurrencyInvestedValue != 0) ? globalBaseCurrencyValueChange / globalBaseCurrencyInvestedValue * 100f
				: 0f;

		float globalBaseCurrencyValue = globalBaseCurrencyInvestedValue + globalBaseCurrencyValueChange;

		return new Portofolio(baseCurrency, assets, globalBaseCurrencyValue, globalPercentChange, globalBaseCurrencyValueChange);
	}

	public Asset getAsset(String login, String symbolCode) {
		AssetEntity ae = assetRepo.findByUserLoginAndSymbolCode(login, symbolCode);

		return (ae != null) ? getAsset(ae) : null;
	}

	private Asset getAsset(AssetEntity ae) {

		SymbolEntity targetEntity = getCurrency(ae.getUser().getLogin(), ae.getSymbol());

		Asset asset = new Asset(convert(ae.getSymbol()), convert(targetEntity),
				ae.getQuantity().floatValue());

		// if same -> base currency
		if (!ae.getSymbol().getCode().equals(targetEntity.getCode())) {

			Quote q = rateService.getLatest(ae.getSymbol().getCode(), targetEntity.getCode());

			BigDecimal currentValue = q.getPrice().multiply(ae.getQuantity());
			float percentChange = percentChange(ae.getTotalPrice(), currentValue);

			asset.setValueChange(currentValue.subtract(ae.getTotalPrice()).floatValue());

			asset.setValue(ae.getTotalPrice().floatValue());
			asset.setCurrentValue(currentValue.floatValue());
			asset.setPercentChange(percentChange);
			asset.setCurrentRate(q.getPrice().floatValue());
		}

		return asset;
	}

	public SymbolEntity getCurrency(String login, SymbolEntity se) {
		return (se.getCurrency() != null) ? se.getCurrency() : portofolioRepo.findByAssetUserLogin(login).getAsset().getSymbol();
	}

	public List<SymbolEntity> getAvailableSymbols(String login) {
		List<AssetEntity> currencyAssets = assetRepo.findAllByUserLoginAndSymbolCurrencyIsNull(login);

		List<String> availableCurrencies = new ArrayList<>();

		for (AssetEntity ae : currencyAssets) {
			availableCurrencies.add(ae.getSymbol().getCode());
		}

		Portofolio portofolio = getPortofolio(login);

		List<SymbolEntity> availableSymbols = symbolService.getAllWithCurrency(availableCurrencies);
		availableSymbols.addAll(symbolService.getCurrencies());
		availableSymbols.remove(symbolService.getForCode(portofolio.getBaseCurrency().getSymbol().getCode()));

		return availableSymbols;
	}
}