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
package fr.ymanvieu.forex.core.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Stopwatch;

import fr.ymanvieu.forex.core.model.entity.rate.HistoricalRate;
import fr.ymanvieu.forex.core.model.entity.rate.LatestRate;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;
import fr.ymanvieu.forex.core.model.repositories.LatestRateRepository;
import fr.ymanvieu.forex.core.model.repositories.SymbolRepository;
import fr.ymanvieu.forex.core.service.Stock;

@RestController
@RequestMapping("/symbol")
public class SymbolController {

	private static final Logger LOG = LoggerFactory.getLogger(SymbolController.class);

	@Autowired
	private Stock stock;

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private HistoricalRateRepository repo;

	@Autowired
	private LatestRateRepository latestrepo;

	@RequestMapping(method = RequestMethod.GET)
	public List<SymbolEntity> get() {
		List<SymbolEntity> symbols = symbolRepo.findAllByOrderByCode();

		return symbols;
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path = "/{code:.+}", method = RequestMethod.GET)
	public ResponseEntity<?> get(@PathVariable String code) throws IOException {
		RateEntity re = getRateFromProvider(code);

		return (re == null) ? new ResponseEntity<>("Symbol unavailable: " + code, HttpStatus.NOT_FOUND) : new ResponseEntity<>(re, HttpStatus.OK);
	}

	private RateEntity getRateFromProvider(String code) throws IOException {
		String currency = stock.getCurrency(code);

		if (currency == null) {
			return null;
		}

		currency = currency.toUpperCase();

		RateEntity re = stock.getLatestRate(code, currency);

		return re;
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> add(@RequestParam String code) throws IOException {
		Stopwatch addWatch = Stopwatch.createStarted();

		SymbolEntity se = symbolRepo.findByCode(code);

		if (se != null) {
			return new ResponseEntity<>("Already added: " + se, HttpStatus.OK);
		}

		RateEntity re = getRateFromProvider(code);

		if (re == null) {
			return new ResponseEntity<>("Symbol unavailable: " + code, HttpStatus.NOT_FOUND);
		}

		List<HistoricalRate> hRates = new ArrayList<>();

		List<RateEntity> histoRates;
		try {
			histoRates = stock.getHistoricalRates(code, re.getTocur());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ResponseEntity<>("Symbol available, but history not found: " + code, HttpStatus.NOT_FOUND);
		}

		for (RateEntity hRate : histoRates) {
			hRates.add(new HistoricalRate(hRate));
		}

		hRates.add(new HistoricalRate(re));

		symbolRepo.save(new SymbolEntity(re.getFromcur(), re.getTocur(), re.getFromName()));
		latestrepo.save(new LatestRate(re));
		repo.save(hRates);

		LOG.info("Symbol {}/{} added in: {}", re.getFromcur(), re.getTocur(), addWatch);

		return new ResponseEntity<>(re, HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@RequestParam String code) {
		Stopwatch addWatch = Stopwatch.createStarted();

		SymbolEntity se = symbolRepo.findByCode(code);

		if (se == null) {
			return new ResponseEntity<>("Symbol not found: code=" + code, HttpStatus.NOT_FOUND);
		}

		symbolRepo.deleteByCodeAndCurrency(se.getCode(), se.getCurrency());
		latestrepo.deleteByFromcurAndTocur(se.getCode(), se.getCurrency());
		repo.deleteByFromcurAndTocur(se.getCode(), se.getCurrency());

		LOG.info("Symbol {}/{} deleted in: {}", se.getCode(), se.getCurrency(), addWatch);

		return new ResponseEntity<>("Symbol deleted: code=" + se.getCode() + ", currency=" + se.getCurrency(), HttpStatus.OK);
	}
}