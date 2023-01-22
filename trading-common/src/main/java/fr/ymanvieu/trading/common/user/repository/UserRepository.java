package fr.ymanvieu.trading.common.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.ymanvieu.trading.common.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByUsernameAndProviderEquals(String username, String provider);

	UserEntity findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByProviderUserIdAndProvider(String providerUserId, String provider);
}
