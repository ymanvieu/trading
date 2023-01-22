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
