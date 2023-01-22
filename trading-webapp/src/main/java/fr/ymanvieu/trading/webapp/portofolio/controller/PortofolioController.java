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

	@Autowired
	private PortofolioService portofolioService;
	
	@Autowired
	private PortofolioDTOMapper portofolioMapper;
	
	@Autowired
	private SymbolDTOMapper symbolMapper;

	@GetMapping
	public PortofolioDTO getPortofolio(Principal p) {
		return portofolioMapper.toPortofolioDto(portofolioService.getPortofolio(Integer.valueOf(p.getName())));
	}

	@GetMapping("/available-symbols")
	public List<SymbolDTO> getAvailableSymbols(Principal p) {
		return symbolMapper.toDto(portofolioService.getAvailableSymbols(Integer.valueOf(p.getName())));
	}

	@GetMapping("/order-info")
	public OrderInfoDTO getOrderInfo(Principal p, String selected, Double quantity) throws SymbolException {
		log.debug("selected code: {}, quantity: {}", selected, quantity);

		OrderInfo oi = portofolioService.getOrderInfo(Integer.valueOf(p.getName()), selected, (quantity == null ? 0 : quantity));

		return portofolioMapper.toOrderInfoDto(oi);
	}

	@PostMapping("/order")
	public Order order(Principal p, @RequestBody OrderRequestDTO or) throws BusinessException {

		Order order = null;

		switch (or.getType()) {
		case BUY:
			order = portofolioService.buy(Integer.valueOf(p.getName()), or.getCode(), or.getQuantity());
			break;
		case SELL:
			order = portofolioService.sell(Integer.valueOf(p.getName()), or.getCode(), or.getQuantity());
			break;
		default:
		}

		return order;
	}
}
