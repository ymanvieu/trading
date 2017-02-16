/**
 *	Copyright (C) 2016	Yoann Manvieu
 *
 *	This software is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.trading.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.user.entity.UserEntity;
import fr.ymanvieu.trading.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public UserEntity createUser(String login, String password) throws UserException {
		
		UserEntity ue = userRepo.findByLogin(login);
		
		if(ue != null) {
			throw new UserException("User already exists: "+ login);
		}
		
		ue = new UserEntity(login, passwordEncoder.encode(password));
		
		return userRepo.saveAndFlush(ue);
	}
}
