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

import static fr.ymanvieu.trading.util.MathUtils.NUMBER_PATTERN;
import static fr.ymanvieu.trading.util.MathUtils.NUMBER_WITH_SIGN_PATTERN;

import java.util.List;

import org.springframework.format.annotation.NumberFormat;

public class Portofolio {

	private final Asset baseCurrency;

	private final List<Asset> assets;

	@NumberFormat(pattern = NUMBER_PATTERN)
	private final float currentValue;

	@NumberFormat(pattern = NUMBER_WITH_SIGN_PATTERN)
	private final float percentChange;

	@NumberFormat(pattern = NUMBER_WITH_SIGN_PATTERN)
	private final float valueChange;

	public Portofolio(Asset baseCurrency, List<Asset> assets, float currentValue, float percentChange, float valueChange) {
		this.baseCurrency = baseCurrency;
		this.assets = assets;
		this.currentValue = currentValue;
		this.percentChange = percentChange;
		this.valueChange = valueChange;
	}

	public Asset getBaseCurrency() {
		return baseCurrency;
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public float getCurrentValue() {
		return currentValue;
	}

	public float getPercentChange() {
		return percentChange;
	}

	public float getValueChange() {
		return valueChange;
	}
}
