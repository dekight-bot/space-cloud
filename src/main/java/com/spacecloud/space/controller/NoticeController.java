package com.spacecloud.space.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spacecloud.space.domain.Notice;
import com.spacecloud.space.service.NoticeService;

@Controller
@RequestMapping("/notice")
public class NoticeController {

	@Autowired
	private NoticeService noticeService;
	
	@GetMapping("/list")
	public String noticeList(Model model) {
		
		List<Notice> list = noticeService.getAllNotice();
		model.addAttribute("noticeList", list);
		
		return "notice-list";
	}
	
	@GetMapping("/detail/{id}")
	public String noticeDetail(@PathVariable("id") Long id, Model model) {
	    // 🚨 [여기를 보세요!] 콘솔창에 이 글자가 찍히는지 봐야 합니다.
	    System.out.println("====== 공지사항 상세조회 콘솔 로그 ======");
	    System.out.println("요청받은 공지사항 ID 번호: " + id);
	    
	    Notice notice = noticeService.getNoticeById(id);
	   
	    if (notice == null) {
	        System.out.println("❌ 경고: DB에서 " + id + "번 공지사항을 찾지 못했습니다! 목록으로 튕겨냅니다.");
	        return "redirect:/notice/list";
	    }
	    
	    System.out.println("⭕ 성공: DB에서 공지를 찾았습니다. 제목 -> " + notice.getTitle());
	    
	    model.addAttribute("notice", notice);
	    return "notice-detail";
	}


	
	
}
