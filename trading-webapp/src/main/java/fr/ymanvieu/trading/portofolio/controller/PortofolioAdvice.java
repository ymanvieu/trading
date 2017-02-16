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
package fr.ymanvieu.trading.portofolio.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import fr.ymanvieu.trading.portofolio.PortofolioService;
import fr.ymanvieu.trading.portofolio.dto.AssetDTO;
import fr.ymanvieu.trading.portofolio.dto.PortofolioMapper;

@ControllerAdvice
public class PortofolioAdvice {

	@Autowired
	private PortofolioService portofolioService;

	@ModelAttribute("baseCurrency")
	public AssetDTO getPortofolio(Principal p) {
		return p != null ? PortofolioMapper.MAPPER.toAssetDto(portofolioService.getBaseCurrency(p.getName())) : null;
	}
}
