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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class HttpClientTest {

	private static final Logger LOG = LoggerFactory.getLogger(HttpClientTest.class);

	private static final String URL = "http://localhost:8080/";

	private static final RestTemplate tp = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

	public static void main(String[] args) throws Exception {
		
		ResponseEntity<String> response = tp.getForEntity(URL + "/admin", String.class);

		LOG.info("{}", response.getBody());
		
		Pattern p = Pattern.compile(".*hidden\" value=\"([\\w-]*)\".*", Pattern.DOTALL);
		Matcher m = p.matcher(response.getBody());
		
		m.matches();
		String csrf = m.group(1);
		
		LOG.info("{}", csrf);
		
		MultiValueMap<String, String> v = new LinkedMultiValueMap<>();
		v.add("username", "admin");
		v.add("password", "password");
		v.add("_csrf", csrf);

		response = tp.postForEntity(URL + "/login", v, String.class);

		LOG.info("resp: {}", response);
		
		String responseStr = tp.getForObject(URL + "/admin", String.class);

		LOG.info("body: {}", responseStr);
		
		responseStr = tp.getForObject(URL + "/portofolio", String.class);

		LOG.info("body: {}", responseStr);
	}
}