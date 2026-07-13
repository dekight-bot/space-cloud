package com.spacecloud.space.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.spacecloud.space.domain.Qna;
import com.spacecloud.space.repository.QnaRepository;

@Service
public class QnaServiceImpl implements QnaService {

    private final QnaRepository qnaRepository;
    
    // 🟢 [수동 생성자 추가] 롬복이 안 되면 우리가 직접 자바 정석대로 연결해 주면 됩니다!
    public QnaServiceImpl(QnaRepository qnaRepository) {
        this.qnaRepository = qnaRepository;
    }

    // 1. 전체 목록
    @Override
    public List<Qna> getAll() {
        return qnaRepository.findAll();
    }

    // 2. 상세 조회
    @Override
    public Qna getById(Long id) {
        return qnaRepository.findById(id).orElse(null);
    }

    // 3. 글 작성
    @Override
    public void register(Qna qna) {
        qnaRepository.save(qna);
    }

    // 4. 삭제
    @Override
    public void delete(Long id) {
        qnaRepository.deleteById(id);
    }

    // 5. 수정
    @Override
    public void modify(Qna qna) {
        Qna originQna = qnaRepository.findById(qna.getId()).orElse(qna);
        
        if(originQna != null) {
            originQna.setTitle(qna.getTitle());
            originQna.setContent(qna.getContent());
            originQna.setCategory(qna.getCategory());
            originQna.setPassword(qna.getPassword());
            
            qnaRepository.save(originQna);
        }
    }

    // 6. 마이페이지용 로그인 ID 기준 조회
    @Override
    public List<Qna> getQnaListByLoginId(String loginId) {
        return qnaRepository.findByUserLoginId(loginId);
    }
    
    @Override
    public List<Qna> getQnaListBySpaceId(Long spaceId) {
        // (※ 만약 레포지토리에 findBySpaceId가 없다면 qnaRepository.findAll() 등으로 임시 리턴하거나 레포지토리에 선언해 주시면 됩니다!)
        return qnaRepository.findBySpaceId(spaceId); 
    }
    
    @Override
    public List<Qna> getAllQuestions() {
        return qnaRepository.findAll();
    }

    @Override
    public Qna getQnaById(Long id) {
        return qnaRepository.findById(id).orElse(null);
    }

    @Override
    public void saveQna(Qna qna) {
        qnaRepository.save(qna);
    }

    @Override
    public List<Qna> getQnaListDesc() {
        return qnaRepository.findAllByOrderByIdDesc();
    }
}