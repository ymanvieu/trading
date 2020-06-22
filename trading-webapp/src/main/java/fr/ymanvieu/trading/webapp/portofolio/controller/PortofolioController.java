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
package fr.ymanvieu.trading.webapp.portofolio.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.common.exception.BusinessException;
import fr.ymanvieu.trading.common.portofolio.Order;
import fr.ymanvieu.trading.common.portofolio.OrderInfo;
import fr.ymanvieu.trading.common.portofolio.PortofolioService;
import fr.ymanvieu.trading.common.symbol.SymbolException;
import fr.ymanvieu.trading.webapp.portofolio.dto.OrderInfoDTO;
import fr.ymanvieu.trading.webapp.portofolio.dto.OrderRequestDTO;
import fr.ymanvieu.trading.webapp.portofolio.dto.PortofolioDTO;
import fr.ymanvieu.trading.webapp.portofolio.dto.PortofolioDTOMapper;
import fr.ymanvieu.trading.webapp.symbol.dto.SymbolDTO;
import fr.ymanvieu.trading.webapp.symbol.dto.SymbolDTOMapper;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/portofolio")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class PortofolioController {

	public enum Type {
		BUY, 
		SELL
	}

	@Autowired
	private PortofolioService portofolioService;
	
	@Autowired
	private PortofolioDTOMapper portofolioMapper;
	
	@Autowired
	private SymbolDTOMapper symbolMapper;

	@GetMapping("/portofolio")
	public PortofolioDTO getPortofolio(Principal p) {
		return portofolioMapper.toPortofolioDto(portofolioService.getPortofolio(p.getName()));
	}

	@GetMapping("/available-symbols")
	public List<SymbolDTO> getAvailableSymbols(Principal p) {
		return symbolMapper.toDto(portofolioService.getAvailableSymbols(p.getName()));
	}

	@GetMapping("/order-info")
	public OrderInfoDTO getOrderInfo(Principal p, String selected, Double quantity) throws SymbolException {
		log.debug("selected code: {}, quantity: {}", selected, quantity);

		OrderInfo oi = portofolioService.getOrderInfo(p.getName(), selected, (quantity == null ? 0 : quantity));

		return portofolioMapper.toOrderInfoDto(oi);
	}

	@PostMapping("/order")
	public Order order(Principal p, @RequestBody OrderRequestDTO or) throws BusinessException {

		Order order = null;

		switch (or.getType()) {
		case BUY:
			order = portofolioService.buy(p.getName(), or.getCode(), or.getQuantity());
			break;
		case SELL:
			order = portofolioService.sell(p.getName(), or.getCode(), or.getQuantity());
			break;
		default:
		}

		return order;
	}
}