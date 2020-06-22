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
package fr.ymanvieu.trading.webapp.rate.controller;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.webapp.rate.dto.DateValueDTO;
import fr.ymanvieu.trading.webapp.rate.dto.DateValueMapper;
import fr.ymanvieu.trading.webapp.rate.dto.RateDTO;
import fr.ymanvieu.trading.webapp.rate.dto.RateDTOMapper;

@RestController
@RequestMapping("/api/rate")
public class RateController {

	@Autowired
	private RateService rateService;
	
	@Autowired
	private RateDTOMapper rateMapper;
	
	@Autowired
	private DateValueMapper dateValueMapper;

	@GetMapping("/latest")
	public List<RateDTO> findAllLatest(Principal p) {
		return rateMapper.favoriteRatesToRateDtos(rateService.getAllLatestWithFavorites(p != null ? p.getName() : null));
	}
	
	@GetMapping(path = "/latest", params = { "fromcur", "tocur" })
	public RateDTO findLatest(@RequestParam String fromcur, @RequestParam String tocur) {
		return rateMapper.toRateDto(rateService.getLatest(fromcur, tocur));
	}

	@GetMapping("/history")
	public List<DateValueDTO> findHistoricalValues(@RequestParam String fromcur, @RequestParam String tocur, 
			@DateTimeFormat(iso = ISO.DATE_TIME) Instant startDate,
			@DateTimeFormat(iso = ISO.DATE_TIME) Instant endDate) {

		if (startDate == null) {
			startDate = rateService.getOldestRateDate(fromcur, tocur);
		}

		if (endDate == null) {
			endDate = rateService.getNewestRateDate(fromcur, tocur);
		}

		return dateValueMapper.toDateValueDto(rateService.getHistoricalValues(fromcur, tocur, startDate, endDate));
	}
}