package com.spacecloud.space.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spacecloud.space.domain.Qna;
import com.spacecloud.space.domain.Space;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {

	List<Qna> findByUserLoginId(String userLoginId);
	
	List<Qna> findBySpaceId(Long spaceId);
	
	List<Qna> findAllByOrderByIdDesc();
	
	
}
