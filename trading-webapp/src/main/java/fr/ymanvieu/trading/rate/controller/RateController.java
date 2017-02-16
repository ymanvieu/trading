/**
 * Copyright (C) 2015 Yoann Manvieu
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
package fr.ymanvieu.trading.rate.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.rate.DateValue;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.rate.dto.DateValueDTO;
import fr.ymanvieu.trading.rate.dto.RateMapper;
import fr.ymanvieu.trading.rate.dto.RateDTO;
import fr.ymanvieu.trading.rate.entity.LatestRate;

@RestController
@RequestMapping("/rate")
public class RateController {

	private static final String CRITERIA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final String RAW_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	@Autowired
	private RateService rateService;

	@RequestMapping("/latest")
	public Page<RateDTO> findLatestByCriteria(String fromcur, String tocur, @DateTimeFormat(pattern = CRITERIA_DATE_PATTERN) Date date,
			Integer pageNumber, String sortDir, String sortedBy) {

		Page<LatestRate> page = rateService.getLatest(fromcur, tocur, date, pageNumber, sortDir, sortedBy);

		PageRequest pr = new PageRequest(page.getNumber(), page.getSize(), page.getSort());

		List<RateDTO> dtos = RateMapper.MAPPER.toRateDto(page.getContent());

		return new PageImpl<>(dtos, pr, page.getTotalElements());
	}

	@RequestMapping("/raw")
	public List<DateValueDTO> findRawValues(String fromcur, String tocur, @DateTimeFormat(pattern = RAW_DATE_PATTERN) Date startDate,
			@DateTimeFormat(pattern = RAW_DATE_PATTERN) Date endDate) {

		if (fromcur == null || tocur == null) {
			return null;
		}

		if (startDate == null) {
			startDate = rateService.getOldestRateDate(fromcur, tocur);
		}

		if (endDate == null) {
			endDate = rateService.getNewestRateDate(fromcur, tocur);
		}

		List<DateValue> values = rateService.getHistoricalValues(fromcur, tocur, startDate, endDate);

		List<DateValueDTO> dtos = RateMapper.MAPPER.toDateValueDto(values);

		return dtos;
	}
}