package com.spacecloud.space.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spacecloud.space.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findBySpaceIdOrderByIdDesc(Long spaceId);

	List<Review> findByUserId(String userId);
	
	List<Review> findAllByOrderByIdDesc();
	
	
}
