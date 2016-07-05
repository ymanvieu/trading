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
import static fr.ymanvieu.trading.util.MathUtils.divide;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import fr.ymanvieu.trading.exception.BusinessException;
import fr.ymanvieu.trading.portofolio.entity.AssetEntity;
import fr.ymanvieu.trading.portofolio.repository.AssetRepository;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.symbol.SymbolException;
import fr.ymanvieu.trading.symbol.SymbolService;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.util.SymbolUtils;

@Service
public class OrderService {

	@Autowired
	private AssetRepository assetRepo;

	@Autowired
	private PortofolioService portofolioService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private RateService rateService;

	public OrderInfo getInfo(String login, String symbolCode, float quantity) {
		List<SymbolEntity> buyableSymbols = portofolioService.getAvailableSymbols(login);

		// get info of selected stock
		SymbolEntity selectedSymbol = null;

		if (!Strings.isNullOrEmpty(symbolCode)) {
			selectedSymbol = SymbolUtils.getFromList(symbolCode, buyableSymbols);
		}

		if (selectedSymbol == null && !buyableSymbols.isEmpty()) {
			selectedSymbol = buyableSymbols.get(0);
		}

		Asset selectedAsset = null;
		Asset selectedCurrency = null;
		BigDecimal gainCost = null;

		if (selectedSymbol != null) {
			selectedAsset = portofolioService.getAsset(login, selectedSymbol.getCode());

			if (selectedAsset == null) {
				selectedAsset = new Asset(SymbolUtils.convert(selectedSymbol),
						SymbolUtils.convert(portofolioService.getCurrency(login, selectedSymbol)));
			}

			String currencyCode = selectedAsset.getCurrency().getCode();

			Quote q = rateService.getLatest(selectedSymbol.getCode(), currencyCode);
			selectedAsset.setCurrentRate(q.getPrice().floatValue());

			if (quantity != 0) {
				gainCost = q.getPrice().multiply(new BigDecimal(quantity));
			}

			selectedCurrency = portofolioService.getAsset(login, currencyCode);
		}

		return new OrderInfo(buyableSymbols, selectedAsset, selectedCurrency, gainCost);
	}

	@Transactional
	public Order buy(String login, String code, float quantity) throws BusinessException {

		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity must be positive: " + quantity);
		}

		SymbolEntity se = symbolService.getForCode(code);

		if (se == null) {
			throw SymbolException.UNKNOWN_SYMBOL(code);
		}

		SymbolEntity fromSymbol = se;
		SymbolEntity toSymbol = portofolioService.getCurrency(login, se);

		AssetEntity availableFund = assetRepo.findByUserLoginAndSymbolCode(login, toSymbol.getCode());

		if (availableFund == null) {
			throw OrderException.NO_FUND(fromSymbol.getCode());
		}

		Quote q = rateService.getLatest(fromSymbol.getCode(), toSymbol.getCode());

		BigDecimal amountNeeded = q.getPrice().multiply(new BigDecimal(quantity));

		if (availableFund.getQuantity().compareTo(amountNeeded) < 0) {
			throw OrderException.NOT_ENOUGH_FUND(code, quantity, toSymbol.getCode(), availableFund.getQuantity().floatValue(),
					amountNeeded.floatValue());
		}

		AssetEntity ownedAsset = assetRepo.findByUserLoginAndSymbolCode(login, fromSymbol.getCode());

		if (ownedAsset == null) {
			ownedAsset = new AssetEntity(availableFund.getUser(), fromSymbol, ZERO, ZERO);
		}

		BigDecimal fundAfterPurchase = availableFund.getQuantity().subtract(amountNeeded);
		BigDecimal fundTotalPriceAfterPurchase = availableFund.getTotalPrice()
				.subtract(divide(availableFund.getTotalPrice(), availableFund.getQuantity()).multiply(amountNeeded));

		availableFund.setQuantity(fundAfterPurchase);
		availableFund.setTotalPrice(fundTotalPriceAfterPurchase);

		ownedAsset.setQuantity(ownedAsset.getQuantity().add(new BigDecimal(quantity)));
		ownedAsset.setTotalPrice(ownedAsset.getTotalPrice().add(amountNeeded));

		assetRepo.save(availableFund);
		assetRepo.save(ownedAsset);

		return new Order(convert(availableFund.getSymbol()), amountNeeded.floatValue(), convert(ownedAsset.getSymbol()), quantity);
	}

	@Transactional
	public Order sell(String login, String code, float quantity) throws BusinessException {

		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity must be positive: " + quantity);
		}

		SymbolEntity se = symbolService.getForCode(code);

		if (se == null) {
			throw SymbolException.UNKNOWN_SYMBOL(code);
		}

		SymbolEntity fromSymbol = se;
		SymbolEntity toSymbol = portofolioService.getCurrency(login, se);

		AssetEntity ownedAsset = assetRepo.findByUserLoginAndSymbolCode(login, fromSymbol.getCode());

		if (ownedAsset == null) {
			throw OrderException.NO_QUANTITY_OWNED(code);
		}

		BigDecimal quantityAfterSell = ownedAsset.getQuantity().subtract(new BigDecimal(quantity));

		if (quantityAfterSell.signum() < 0) {
			throw OrderException.NOT_ENOUGH_OWNED(code, ownedAsset.getQuantity().floatValue(), quantity);
		}

		AssetEntity purchasedAsset = assetRepo.findByUserLoginAndSymbolCode(login, toSymbol.getCode());

		if (purchasedAsset == null) {
			purchasedAsset = new AssetEntity(ownedAsset.getUser(), toSymbol, ZERO, ZERO);
		}

		Quote q = rateService.getLatest(fromSymbol.getCode(), toSymbol.getCode());

		BigDecimal purchasedValue = q.getPrice().multiply(new BigDecimal(quantity));
		BigDecimal purchasedValueTotalPrice = rateService
				.getLatest(purchasedAsset.getSymbol().getCode(), portofolioService.getCurrency(login, purchasedAsset.getSymbol()).getCode())
				.getPrice().multiply(purchasedValue);

		// buy currency
		purchasedAsset.setQuantity(purchasedAsset.getQuantity().add(purchasedValue));
		purchasedAsset.setTotalPrice(purchasedAsset.getTotalPrice().add(purchasedValueTotalPrice));

		// sell owned

		if (quantityAfterSell.signum() <= 0) {
			assetRepo.delete(ownedAsset);
		} else {
			BigDecimal priceToSubstract = divide(ownedAsset.getTotalPrice(), ownedAsset.getQuantity()).multiply(new BigDecimal(quantity));
			BigDecimal totalPriceAfterSell = ownedAsset.getTotalPrice().subtract(priceToSubstract);
			ownedAsset.setTotalPrice(totalPriceAfterSell);

			ownedAsset.setQuantity(quantityAfterSell);

			assetRepo.save(ownedAsset);
		}

		assetRepo.save(purchasedAsset);

		return new Order(convert(ownedAsset.getSymbol()), quantity, convert(purchasedAsset.getSymbol()), purchasedValue.floatValue());
	}
}
