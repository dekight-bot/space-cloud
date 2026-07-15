package com.spacecloud.space.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.spacecloud.space.domain.Qna;
import com.spacecloud.space.domain.QnaReply;
import com.spacecloud.space.domain.User;
import com.spacecloud.space.service.QnaReplyService;
import com.spacecloud.space.service.QnaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/qna")
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
    
    @GetMapping
    public String qnaPage(Model model) {
        model.addAttribute("list", qnaService.getQnaListDesc());
        return "qna"; 
    }
    
    @GetMapping("/write")
    public String qnaWritePage() {
        return "qnawrite";
    }
    
    @PostMapping("/write")
    public String qnaRegister(@ModelAttribute Qna qna, HttpSession session,
                              @RequestParam(value = "qnaImage", required = false) MultipartFile file) {
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null) return "redirect:/user/login";
        
        qna.setUserLoginId(loginUser.getLoginId());
        qna.setCreateAt(LocalDateTime.now());
        
        if (file != null && !file.isEmpty()) {
            try {
                String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String savedFilename = UUID.randomUUID().toString() + ext;
                file.transferTo(new File(uploadDir + savedFilename));
                qna.setImageUrl("/images/" + savedFilename); 
            } catch (IOException e) { e.printStackTrace(); }
        } else {
            qna.setImageUrl("");
        }
        
        if(qna.getSpaceId() == null) qna.setSpaceId(1L);
        qnaService.register(qna);
        return "redirect:/user/reservation-list";
    }
    
    @GetMapping("/detail/{id}")
    public String qnaDetailPage(@PathVariable("id") Long id, Model model, HttpSession session) {
    	
        Qna qna = qnaService.getById(id);
        User loginUser = (User) session.getAttribute("loginUser");
        
        if("Y".equals(qna.getPassword())) {
        	
        	boolean isAdmin = (loginUser  != null && "ADMIN".equals(loginUser.getRole()));
        	
        	boolean isOwner = (loginUser != null && loginUser.getLoginId().equals(qna.getUserLoginId())); 
        	
        	if(!isAdmin && !isOwner) {
        		return "redirect:/qna/password?id=" + id;
        	}
        }
        
        model.addAttribute("qna", qna);
        model.addAttribute("replies", qnaReplyService.getRepliesQnaId(id));
        return "qnadetail";
    }
    
    @PostMapping("/reply/write")
    public String qnaReplyWrite(@RequestParam("qnaId") Long qnaId, @ModelAttribute QnaReply qnaReply, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) return "redirect:/access-denied";
        
        qnaReplyService.saveReply(qnaId, qnaReply);
        return "redirect:/qna/detail/" + qnaId; // 클래스 레벨 /qna 가 있으므로 절대경로 /qna/... 사용
    }
    
    @GetMapping("/reply/delete")
    public String qnaReplyDelete(@RequestParam("qnaId") Long qnaId, @RequestParam("replyId") Long replyId) {
        qnaReplyService.delete(replyId);
        return "redirect:/qna/detail/" + qnaId;
    }
    
    @GetMapping("/modify")
    public String qnaModifyPage(@RequestParam("id") Long id, Model model) {
    	
    	Qna qna = qnaService.getById(id);
    	model.addAttribute("qna", qna);
    	
    	return "qnamodify";
    }
    
    @PostMapping("/modify")
    public String qnaModify(@ModelAttribute Qna qna, HttpSession session) {
    	User loginUser = (User) session.getAttribute("loginUser");
    	
    	if(loginUser == null) {
    		return "redirect:/user/login";
    	}
    	
    	Qna originQna = qnaService.getById(qna.getId());
        if (originQna != null && originQna.getUserLoginId().equals(loginUser.getLoginId())) {
            qnaService.modify(qna);
        } else {
            return "redirect:/access-denied"; // 권한 없음 페이지 또는 목록으로
        }
        
        return "redirect:/qna/detail/" + qna.getId();
    }
    
    @GetMapping("/password")
    public String qnaPasswordPage(@RequestParam("id") Long id, Model model) {
    	
    	model.addAttribute("id", id);
    	    	
    	return "qnapassword";
    }
    
    @PostMapping("/password")
    public String qnaPasswordCheck(@RequestParam("id") Long id, @RequestParam("inputPassword") String inputPassword, Model model) {
        Qna qna = qnaService.getById(id);
        if(qna != null && qna.getPassword().equals(inputPassword)) {
            return "redirect:/qna/detail/" + id;
        } else {
            model.addAttribute("id", id);
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "qnapassword";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteQna(@PathVariable("id") Long id, HttpSession session) {
    	
    	User loginUser = (User) session.getAttribute("loginUser");
    	
    	Qna qna = qnaService.getById(id);
    	if(loginUser != null && qna != null && qna.getUserLoginId().equals(loginUser.getLoginId())) {
    			qnaService.delete(id);
    	}
    	
    	return "redirect:/qna";
    }
    
}