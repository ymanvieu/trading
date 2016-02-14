/**
 * Copyright (C) 2014 Yoann Manvieu
 * 
 * This software is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.forex.core.provider;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.ymanvieu.forex.core.http.ConnectionHandler;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;

public abstract class AProvider {

	@Autowired
	private ConnectionHandler handler;

	/**
	 * Returns the latest possible rates value (implementation dependent).
	 * 
	 * @return List of latest rates
	 * @throws IOException
	 */
	public abstract List<RateEntity> getRates() throws IOException;
	
	/**
	 * @throws IOException  
	 */
	public List<RateEntity> getHistoricalRates() throws IOException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public String sendGet(String query) {
		return handler.sendGet(query);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
