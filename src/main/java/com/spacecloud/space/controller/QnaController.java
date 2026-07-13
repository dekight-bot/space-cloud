package com.spacecloud.space.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spacecloud.space.domain.Qna;
import com.spacecloud.space.domain.QnaReply;
import com.spacecloud.space.domain.User;
import com.spacecloud.space.service.QnaReplyService;
import com.spacecloud.space.service.QnaService;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/qna") // 🚨 상단에 /qna가 고정되어 있으므로 아래 메서드 주소들 앞에는 /qna를 빼야 합니다!
public class QnaController {
	
	@Value("${file.upload-dir}")
	private String uploadDir;

	private final QnaService qnaService;
    private final QnaReplyService qnaReplyService;
    
    @Autowired
    public QnaController(QnaService qnaService, QnaReplyService qnaReplyService) {
        this.qnaService = qnaService;
        this.qnaReplyService = qnaReplyService;
    }
    
    // 메인이자 목록 (최신순)
    @GetMapping
    public String qnaPage(Model model) {
        List<Qna> list = qnaService.getQnaListDesc();
        model.addAttribute("list", list);
        return "qna"; 
    }
    
    // qna 글쓰기 화면 이동 
    @GetMapping("/write")
    public String qnaWritePage() {
        return "qnawrite";
    }
    
    // 작성 처리
    @PostMapping("/write")
    public String qnaRegister(@ModelAttribute Qna qna, HttpSession session,
                              @RequestParam(value = "qnaImage", required = false) MultipartFile file) {
        User loginUser = (User) session.getAttribute("loginUser");
        
        if(loginUser == null) {
            return "redirect:/user/login";
        }
        
        qna.setUserLoginId(loginUser.getLoginId());
        
        qna.setCreateAt(LocalDateTime.now()); // 🟢 작성날짜 강제 주입 성공!
        
        if (file != null && !file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                String savedFilename = UUID.randomUUID().toString() + ext;
                
                File saveFile = new File(uploadDir + savedFilename);
                file.transferTo(saveFile);
                
               
                qna.setImageUrl("/images/" + savedFilename); 
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            qna.setImageUrl("");
        }
        
        if(qna.getSpaceId() == null) {
            qna.setSpaceId(1L);
        }
        
        qnaService.register(qna);
        return "redirect:/user/reservation-list";
    }
    
    // 🟢 [버그 수리] 깔끔한 주소로 연동 완료 (/qna/detail/7 주소와 완벽하게 대칭)
    @GetMapping("/detail/{id}")
    public String qnaDetailPage(@PathVariable("id") Long id, Model model) {
        
    	Qna qna = qnaService.getById(id);
        model.addAttribute("qna", qna);
        
        List<QnaReply> replies = qnaReplyService.getRepliesQnaId(id);
        model.addAttribute("replies", replies);
        
        return "qnadetail";
    }
    
    // 삭제 처리
    @GetMapping("/delete/{id}")
    public String deleteQna(@PathVariable("id") Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null) {
            return "redirect:/user/login";
        }
        Qna qna = qnaService.getById(id);
        if(qna != null && qna.getUserLoginId().equals(loginUser.getLoginId())) {
            qnaService.delete(id);
        }
        return "redirect:/user/reservation-list";
    }
    
    // 수정 처리
    @PostMapping("/modify-myqna")
    public String modifyQna(@ModelAttribute Qna qna, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null) {
            return "redirect:/user/login";
        }
        Qna originQna = qnaService.getById(qna.getId());
        if(originQna != null && originQna.getUserLoginId().equals(loginUser.getLoginId())) {
            qnaService.modify(qna);
        }
        return "redirect:/user/reservation-list";
    }
    
    // 답변 처리
    @PostMapping("/reply/write")
    public String qnaReplyWrite(@RequestParam("qnaId") Long qnaId, @ModelAttribute QnaReply qnaReply, Model model) {
        qnaReplyService.saveReply(qnaId, qnaReply);
        List<QnaReply> replies = qnaReplyService.getRepliesQnaId(qnaId);
        model.addAttribute("replies", replies);
        return "redirect:/qna/detail/" + qnaId; // 🟢 주소 규칙 통일
    }
    
    // 답변 삭제
    @GetMapping("/reply/delete")
    public String qnaReplyDelete(@RequestParam("qnaId") Long qnaId, @RequestParam("replyId") Long replyId) {
        qnaReplyService.delete(replyId);
        return "redirect:/qna/detail/" + qnaId; // 🟢 주소 규칙 통일
    }
    
    @GetMapping("/delete")
    public String qnaDelete(@RequestParam("id") Long id) {
        qnaService.delete(id);
        return "redirect:/qna";
    }
    
    @GetMapping("/modify")
    public String qnaModifyPage(@RequestParam("id") Long id, Model model) {
         Qna qna = qnaService.getById(id);
         model.addAttribute("qna", qna);
         return "qnamodify";
    }
    
    @PostMapping("/modify")
    public String qnaModify(@ModelAttribute Qna qna) {
        qnaService.modify(qna);
        return "redirect:/qna/detail/" + qna.getId(); // 🟢 주소 규칙 통일
    }
    
    // 비밀글
    @GetMapping("/password")
    public String qnaPasswordPage(@RequestParam("id") Long id, Model model) {
        model.addAttribute("id", id);
        return "qnapassword";
    }
    
    @PostMapping("/password")
    public String qnaPasswordCheck(@RequestParam("id") Long id, @RequestParam("inputPassword") String inputPassword, Model model) {
        Qna qna = qnaService.getById(id);
        if(qna != null && qna.getPassword().equals(inputPassword)) {
            return "redirect:/qna/detail/" + id; // 🟢 주소 규칙 통일
        } else {
            model.addAttribute("id" , id);
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "qnapassword";
        }
    }
}