package com.spacecloud.space.service;

import java.util.List;

import com.spacecloud.space.domain.QnaReply;

public interface QnaReplyService {
	
	// 답변 등록기능
	void saveReply(Long qnaId, QnaReply reply);

	// 특정 문의글의 답변 목록 조회
	List<QnaReply> getRepliesQnaId(Long Id);

	void delete(Long qnaId);
}
