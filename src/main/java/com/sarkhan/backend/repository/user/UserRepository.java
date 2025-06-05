package com.sarkhan.backend.repository.user;

import com.sarkhan.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // findByUsername -> findByEmail

    @Query("SELECT  u.fullName  FROM User u WHERE u.id= :userId")
    String findUserNameByUserId(String userId);

}
