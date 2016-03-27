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

import java.security.Principal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@ConditionalOnWebApplication
@Controller
@RequestMapping("/admin")
@PreAuthorize("isAuthenticated()")
public class AdminController {

	@Autowired
	private SymbolController symController;

	@Autowired
	private SimpUserRegistry reg;

	@RequestMapping(method = RequestMethod.GET)
	public String symbols(Model model) {
		model.addAttribute("symbols", symController.get());
		return "symbols";
	}

	@ResponseBody
	@RequestMapping(path = "/user", method = RequestMethod.GET)
	public Set<SimpSession> getUser(Principal p) {
		SimpUser u = reg.getUser(p.getName());
		// FIXME infinite loop
		return u.getSessions();
	}
}