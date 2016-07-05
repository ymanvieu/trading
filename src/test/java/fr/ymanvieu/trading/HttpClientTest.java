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
package fr.ymanvieu.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class HttpClientTest {

	private static final Logger LOG = LoggerFactory.getLogger(HttpClientTest.class);

	private static final String URL = "http://localhost:8080/";

	private static final RestTemplate tp = new RestTemplate();

	public static void main(String[] args) throws Exception {

		//CSRF must be disabled
		
		MultiValueMap<String, String> v = new LinkedMultiValueMap<>();
		v.add("username", "admin");
		v.add("password", "password");

		ResponseEntity<String> response = tp.postForEntity(URL + "/login", v, String.class);

		LOG.info("resp: {}", response);
		LOG.info("Cookie: {}", response.getHeaders().get("Set-Cookie").get(0));

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", response.getHeaders().get("Set-Cookie").get(0));
		HttpEntity<String> entity = new HttpEntity<>(headers);

		response = tp.exchange(URL + "/admin/symbol/", HttpMethod.GET, entity, String.class);

		LOG.info("body: {}", response.getBody());
	}
}