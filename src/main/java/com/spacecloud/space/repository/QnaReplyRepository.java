package com.spacecloud.space.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spacecloud.space.domain.QnaReply;

public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {
	
	List<QnaReply> findByQnaIdOrderByCreateAtAsc(Long qnaId);
}
