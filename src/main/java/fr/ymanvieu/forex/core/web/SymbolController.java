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
package fr.ymanvieu.forex.core.web;

import java.io.IOException;
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

import fr.ymanvieu.forex.core.model.Quote;
import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;
import fr.ymanvieu.forex.core.service.Stock;
import fr.ymanvieu.forex.core.service.SymbolService;

@RestController
@RequestMapping("/symbol")
@PreAuthorize("isAuthenticated()")
public class SymbolController {

	private static final Logger LOG = LoggerFactory.getLogger(SymbolController.class);

	@Autowired
	private Stock stock;

	@Autowired
	private SymbolService symbolService;

	@RequestMapping(method = RequestMethod.GET)
	public List<SymbolEntity> get() {
		return symbolService.getAllWithCurrency();
	}

	@RequestMapping(path = "/{code:.+}", method = RequestMethod.GET)
	public ResponseEntity<?> get(@PathVariable String code) throws IOException {
		Quote quote = stock.getQuoteFromProvider(code);

		return (quote == null) ? new ResponseEntity<>("Symbol unavailable: " + code, HttpStatus.NOT_FOUND)
				: new ResponseEntity<>(quote, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> add(@RequestParam String code) throws IOException {
		Stopwatch addWatch = Stopwatch.createStarted();

		SymbolEntity se = symbolService.getForCode(code);

		if (se != null) {
			return new ResponseEntity<>("Already added: " + se, HttpStatus.OK);
		}

		Quote latestQuote = stock.getQuoteFromProvider(code);

		if (latestQuote == null) {
			return new ResponseEntity<>("Symbol unavailable: " + code, HttpStatus.NOT_FOUND);
		}

		List<Quote> historicalQuotes;
		try {
			historicalQuotes = stock.getHistoricalRates(code);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ResponseEntity<>("Symbol available, but history not found: " + code, HttpStatus.NOT_FOUND);
		}

		se = symbolService.addSymbol(latestQuote, historicalQuotes);

		LOG.info("Symbol {}/{} added in: {}", se, se.getCurrency(), addWatch);

		return new ResponseEntity<>(latestQuote, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<String> delete(@RequestParam String code) {
		Stopwatch addWatch = Stopwatch.createStarted();

		SymbolEntity se = symbolService.getForCode(code);

		if (se == null) {
			return new ResponseEntity<>("Symbol not found: code=" + code, HttpStatus.NOT_FOUND);
		}

		List<SymbolEntity> seCurrencies = symbolService.getAllWithCurrency(code);

		if (!seCurrencies.isEmpty()) {
			return new ResponseEntity<>("Symbol cannot be deleted because used as currency for: " + seCurrencies, HttpStatus.OK);
		}

		symbolService.removeSymbol(code);

		LOG.info("Symbol {} deleted in: {}", se, addWatch);

		return new ResponseEntity<>("Symbol deleted: " + se, HttpStatus.OK);
	}
}