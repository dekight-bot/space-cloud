package com.spacecloud.space.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacecloud.space.domain.Qna;
import com.spacecloud.space.domain.QnaReply;
import com.spacecloud.space.repository.QnaReplyRepository;
import com.spacecloud.space.repository.QnaRepository;

@Service
@Transactional
public class QnaReplyServiceImpl implements QnaReplyService{

	@Autowired
	private QnaReplyRepository qnaReplyRepository;
	
	// 원본을 글을 찾기 위해 기존 페포지토리도 주입.
	@Autowired
	private QnaRepository qnaRepository;
	
	@Override
	public void saveReply(Long qnaId, QnaReply reply) {
		
		Qna qna = qnaRepository.findById(qnaId).orElse(null);
		
		if(qna != null) {
			reply.setQna(qna);
			reply.setCreateAt(LocalDateTime.now());
			if(reply.getWriter() == null || reply.getWriter().isEmpty()) {
				reply.setWriter("관리자");
			}
			
			qnaReplyRepository.save(reply);
		}
		
	}

	@Override
	@Transactional(readOnly = true)
	public List<QnaReply> getRepliesQnaId(Long qnaId) {
		
		return qnaReplyRepository.findByQnaIdOrderByCreateAtAsc(qnaId);
	}

	@Override
	public void delete(Long replyId) {
		
		qnaReplyRepository.deleteById(replyId);
	}

}
