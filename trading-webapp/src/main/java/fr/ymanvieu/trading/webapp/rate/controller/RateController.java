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
		return rateMapper.favoriteRatesToRateDtos(rateService.getAllLatestWithFavorites(p != null ? Integer.valueOf(p.getName()) : null));
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
