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
package fr.ymanvieu.trading.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Stopwatch;

@Component
public class ConnectionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectionHandler.class);

	@Autowired
	private RestTemplate template;

	public String sendGet(String request) {
		LOG.trace("request: {}", request);

		Stopwatch gsw = Stopwatch.createStarted();

		String response = template.getForObject(request, String.class);

		LOG.debug("Data fetched in {}", gsw);

		return response;
	}
}