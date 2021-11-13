/**
 * Copyright (C) 2020 Yoann Manvieu
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
package fr.ymanvieu.trading.common.user;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.portofolio.PortofolioService;
import fr.ymanvieu.trading.common.symbol.util.CurrencyUtils;
import fr.ymanvieu.trading.common.user.entity.AuthorityEntity;
import fr.ymanvieu.trading.common.user.entity.UserEntity;
import fr.ymanvieu.trading.common.user.repository.UserRepository;

@Transactional
@Service
public class UserService {

    @Autowired
    private PortofolioService portofolioService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    //TODO add tests
    public User createLocalUser(String username, String plaintextPassword) {
        if(userRepository.existsByUsernameAndProviderEquals(username, UserProvider.LOCAL.getProviderType())) {
            throw new UserAlreadyExistsException(username);
        }

        UserEntity ue = new UserEntity(username, true, UserProvider.LOCAL.getProviderType());
        ue.setPassword(passwordEncoder.encode(plaintextPassword));
        ue.getAuthorities().add(new AuthorityEntity(ue, Role.USER.name()));
        ue = userRepository.save(ue);

        createPortofolio(ue.getId());

        return getUser(ue.getId());
    }

    public User createSocialUser(String username, UserProvider socialProvider, String socialProviderUserId, String email) {
        if(userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        UserEntity ue = new UserEntity(username, true, socialProvider.getProviderType());
        ue.setProviderUserId(socialProviderUserId);
        ue.setEmail(email);
        ue.getAuthorities().add(new AuthorityEntity(ue, Role.USER.name()));

        ue = userRepository.save(ue);

        createPortofolio(ue.getId());

        return getUser(ue.getId());
    }

    public User updateSocialUser(String email, String username) {
        UserEntity ue = userRepository.findByEmail(email);

        if (ue == null) {
            throw new IllegalArgumentException("User not found for email: " + email);
        }

        ue.setUsername(username);
        ue = userRepository.save(ue);

        return getUser(ue.getId());
    }

    private void createPortofolio(Integer userId) {
        portofolioService.createPortofolio(userId, CurrencyUtils.EUR, 100_000);
    }

    //todo find solution to merge getUser()/getUsername()
    public User getUser(Integer userId) {
        UserEntity ue = userRepository.findById(userId).orElseThrow();
        var authorities = ue.getAuthorities()
            .stream().map(a -> new SimpleGrantedAuthority(a.getAuthority())).collect(Collectors.toList());

        return new User(String.valueOf(ue.getId()), "", authorities);
    }

    public String getUsername(Integer userId) {
        return userRepository.findById(userId).orElseThrow().getUsername();
    }
}
