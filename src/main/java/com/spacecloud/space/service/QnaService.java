package com.spacecloud.space.service;

import java.util.List;
import com.spacecloud.space.domain.Qna;

public interface QnaService {
    
    // 🟢 관리자 대시보드용 기본 명칭 매칭
    List<Qna> getAllQuestions();
    Qna getQnaById(Long id);
    void saveQna(Qna qna);

    // 기존에 선언해 두셨던 기본 규격들
    List<Qna> getAll();
    Qna getById(Long id);
    void register(Qna qna);
    void delete(Long id);
    void modify(Qna qna);
    
    List<Qna> getQnaListByLoginId(String loginId);
    List<Qna> getQnaListBySpaceId(Long spaceId);
    
    List<Qna> getQnaListDesc();
    
    
}