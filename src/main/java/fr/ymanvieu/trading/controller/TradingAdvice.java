package fr.ymanvieu.trading.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import fr.ymanvieu.trading.portofolio.Portofolio;
import fr.ymanvieu.trading.portofolio.PortofolioService;

@ControllerAdvice
public class TradingAdvice {

	@Autowired
	private PortofolioService portofolioService;

	@ModelAttribute("portofolio")
	public Portofolio getPortofolio(Principal p) {
		return p != null ? portofolioService.getPortofolio(p.getName()) : null;
	}
}
