package fr.ymanvieu.trading.common.user;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.portofolio.PortofolioService;
import fr.ymanvieu.trading.common.symbol.Currency;
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
        ue.getAuthorities().add(new AuthorityEntity(ue, Role.USER));
        ue = userRepository.save(ue);

        createPortofolio(ue.getId());

        return getUser(ue.getId()).orElseThrow();
    }

    public User createSocialUser(String username, UserProvider socialProvider, String socialProviderUserId, String email) {
        if(userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        UserEntity ue = new UserEntity(username, true, socialProvider.getProviderType());
        ue.setProviderUserId(socialProviderUserId);
        ue.setEmail(email);
        ue.getAuthorities().add(new AuthorityEntity(ue, Role.USER));

        ue = userRepository.save(ue);

        createPortofolio(ue.getId());

        return getUser(ue.getId()).orElseThrow();
    }

    public User updateSocialUser(String email, String username) {
        UserEntity ue = userRepository.findByEmail(email);

        if (ue == null) {
            throw new IllegalArgumentException("User not found for email: " + email);
        }

        ue.setUsername(username);
        ue = userRepository.save(ue);

        return getUser(ue.getId()).orElseThrow();
    }

    private void createPortofolio(Integer userId) {
        portofolioService.createPortofolio(userId, Currency.EUR, 100_000);
    }

    //todo find solution to merge getUser()/getUsername()
    public Optional<User> getUser(Integer userId) {
        return userRepository.findById(userId).map(ue -> {
            var authorities = ue.getAuthorities()
                .stream().map(a -> new SimpleGrantedAuthority(a.getAuthority())).collect(Collectors.toList());

            return new User(String.valueOf(ue.getId()), "", authorities);
        });
    }

    public String getUsername(Integer userId) {
        return userRepository.findById(userId).orElseThrow().getUsername();
    }
}
