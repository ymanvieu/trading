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

import java.util.List;

public class Portofolio {

	private final AssetInfo baseCurrency;

	private final List<AssetInfo> assets;

	private final float currentValue;

	private final float percentChange;

	private final float valueChange;

	public Portofolio(AssetInfo baseCurrency, List<AssetInfo> assets, float currentValue, float percentChange, float valueChange) {
		this.baseCurrency = baseCurrency;
		this.assets = assets;
		this.currentValue = currentValue;
		this.percentChange = percentChange;
		this.valueChange = valueChange;
	}

	public AssetInfo getBaseCurrency() {
		return baseCurrency;
	}

	public List<AssetInfo> getAssets() {
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
