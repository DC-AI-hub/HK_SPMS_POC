package com.spms.backend.repository.idm;


import com.spms.backend.repository.BaseRepository;
import com.spms.backend.repository.entities.idm.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findByUsername(String username);
    List<User> findByUsernameContainingOrEmailContaining(String username, String email);
    Optional<User> findByProviderAndProviderId(String provider,String providerId);
}
