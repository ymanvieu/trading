/**
 * Copyright (C) 2019 Yoann Manvieu
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
package fr.ymanvieu.trading.webapp.symbol.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.common.symbol.SymbolService;

@RestController
@RequestMapping("/api/symbol")
@PreAuthorize("isAuthenticated()")
public class SymbolController {

	@Autowired
	private SymbolService symbolService;
	
	@PostMapping("/favorite")
	public void addFavoriteSymbol(@RequestBody Map<String, String> symbolCode, Principal p) {
		symbolService.addFavoriteSymbol(symbolCode.get("fromSymbolCode"), symbolCode.get("toSymbolCode"), Integer.valueOf(p.getName()));
	}
	
	@DeleteMapping("/favorite/{fromSymbolCode}/{toSymbolCode}")
	public void deleteFavoriteSymbol(@PathVariable String fromSymbolCode, @PathVariable String toSymbolCode, Principal p) {
		symbolService.deleteFavoriteSymbol(fromSymbolCode, toSymbolCode, Integer.valueOf(p.getName()));
	}
}