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
package fr.ymanvieu.trading.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.trading.TradingApplication;
import fr.ymanvieu.trading.user.UserService;
import fr.ymanvieu.trading.user.entity.UserEntity;
import fr.ymanvieu.trading.user.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TradingApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {

	@Autowired
	private UserRepository repo;

	@Autowired
	private UserService service;

	@Autowired
	private PasswordEncoder pwEncoder;

	@Test
	public void testCreateUser() throws Exception {
		String login = "titi";
		String password = "tutu";

		UserEntity ue = service.createUser(login, password);

		assertThat(ue.getLogin()).isEqualTo(login);
		assertThat(pwEncoder.matches(password, ue.getPassword())).isTrue();

		assertThat(repo.findByLogin(login)).isNotNull();
	}
}
