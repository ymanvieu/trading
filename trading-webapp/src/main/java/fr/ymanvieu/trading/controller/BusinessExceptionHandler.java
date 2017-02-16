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
package fr.ymanvieu.trading.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import fr.ymanvieu.trading.exception.BusinessException;

public class BusinessExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler
	public RedirectView handleError(BusinessException e, Locale l, HttpServletRequest request) {
		Response response = new Response();

		response.setErrorMessage(messageSource.getMessage(e.getKey(), e.getArgs(), l));

		FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);

		outputFlashMap.put("response", response);

		return new RedirectView(request.getServletPath());
	}

}