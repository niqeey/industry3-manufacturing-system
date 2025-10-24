package com.industry3.manufacturing.repository;

import com.industry3.manufacturing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(User.Role role);
    
    List<User> findByIsActiveTrue();
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role IN :roles")
    List<User> findActiveUsersByRoles(List<User.Role> roles);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
