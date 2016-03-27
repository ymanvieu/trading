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
package fr.ymanvieu.forex.core.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import fr.ymanvieu.forex.core.model.entity.rate.HistoricalRate;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;

@Service
public class RateService {

	private static Sort SORT_ASC_DATE = new Sort(Direction.ASC, "date");
	private static Sort SORT_DESC_DATE = new Sort(Direction.DESC, "date");

	private final HistoricalRateRepository repo;

	@Autowired
	public RateService(HistoricalRateRepository repo) {
		this.repo = repo;
	}

	public Date getMin(String fromcur, String tocur) {
		HistoricalRate r = repo.findFirstByFromcurCodeAndTocurCode(fromcur, tocur, SORT_ASC_DATE);
		return (r == null) ? null : r.getDate();
	}

	public Date getMax(String fromcur, String tocur) {
		HistoricalRate r = repo.findFirstByFromcurCodeAndTocurCode(fromcur, tocur, SORT_DESC_DATE);
		return (r == null) ? null : r.getDate();
	}

}
