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
package fr.ymanvieu.trading.portofolio.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "portofolio")
public class PortofolioEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@OneToOne(optional = false)
	@JoinColumn(name = "asset_id")
	private AssetEntity asset;

	public PortofolioEntity() {
	}

	public PortofolioEntity(AssetEntity asset) {
		this.asset = asset;
	}

	public AssetEntity getAsset() {
		return asset;
	}

	@Override
	public int hashCode() {
		return Objects.hash(asset);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof PortofolioEntity))
			return false;

		PortofolioEntity other = (PortofolioEntity) obj;

		return Objects.equals(asset, other.asset);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("asset", asset).toString();
	}
}
