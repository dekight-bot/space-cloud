package com.spacecloud.space.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spacecloud.space.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByLoginId(String loginId);

}
