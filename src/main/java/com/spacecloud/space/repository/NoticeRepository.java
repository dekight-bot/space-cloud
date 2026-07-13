package com.spacecloud.space.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spacecloud.space.domain.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>{
	

	List<Notice> findAllByOrderByIdDesc();
	
	
}
