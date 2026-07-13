package com.spacecloud.space.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spacecloud.space.domain.Notice;
import com.spacecloud.space.domain.Qna;
import com.spacecloud.space.domain.QnaReply;
import com.spacecloud.space.domain.Reservation;
import com.spacecloud.space.domain.Space;
import com.spacecloud.space.domain.SpaceImage;
import com.spacecloud.space.domain.User;
import com.spacecloud.space.service.NoticeService;
import com.spacecloud.space.service.QnaService;
import com.spacecloud.space.service.ReservationService;
import com.spacecloud.space.service.SpaceService;
import com.spacecloud.space.service.UserService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private SpaceService spaceService;
	
	@Autowired
	private NoticeService noticeService;

	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private QnaService qnaService;
	
	@Autowired
	private UserService userService;
	
		// 관리자 메인 대시보드
	@GetMapping("/home")
	public String adminHome(HttpSession session, RedirectAttributes rttr, Model model) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		
		
		  if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
		 
		 rttr.addFlashAttribute("msg", "관리자만 접근 가능한 페이지입니다.");
		 return"redirect:/user/login"; 
		
		 }
		  
		  long totalSpaces = spaceService.getTotalSpaceCount();
		  
		  List<Reservation> allRes = reservationService.getAllReservations();
		  
		  long totalReservations = 0;
		  if(allRes != null) {
			   totalReservations = allRes.size();
		  }
		
		model.addAttribute("totalSpaces", totalSpaces);
		model.addAttribute("totalReservations", totalReservations);
		
		return "admin/home";
	}
	
	@GetMapping("/spaces")
	public String adminSpace(HttpSession session,
												Model model,
												RedirectAttributes rttr) {
		
			User loginUser = (User) session.getAttribute("loginUser");
			if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
				rttr.addFlashAttribute("msg", "최고 관리자 권한이 필요한 구역입니다.");
				
				return "redirect:/user/login";
			}
			
			List<Space> spaceList = spaceService.getAllSpace();
			model.addAttribute("spaceList", spaceList);
		
		return "admin/spaces";
	}
	
	@GetMapping("/spaces/add")
	public String adminSpaceAddForm(HttpSession session, RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
			rttr.addFlashAttribute("msg", "최고 관리자 권한이 필요한 구역입니다.");
			
			return "redirect:/user/login";
		}
		
		return "admin/space-add";
	}
	
	@PostMapping("/spaces/add")
	public String adminSpaceAddProcess(@RequestParam("name") String name,
																		@RequestParam("pricePerHour") int pricePerHour,
																		@RequestParam("description") String description,
																		@RequestParam("category") String category,
																		@RequestParam("address") String address,
																		@RequestParam("files") MultipartFile[] files,
																		HttpSession session, RedirectAttributes rttr) throws IOException {
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
			return "redirect:/user/login";
		}
		
		Space space = new Space();
		space.setName(name);
		space.setPricePerHour(pricePerHour);
		space.setDescription(description);
		space.setCategory(category);
		space.setAddress(address);
		
		String uploadDir = "/app/upload/";
		File folder = new File(uploadDir);
		
		if (!folder.exists()) folder.mkdirs();
		
		for (MultipartFile file : files) {
	        if (!file.isEmpty()) {
	            String savedFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	            File saveFile = new File(folder, savedFilename);
	            file.transferTo(saveFile);
	            
	            // 3. SpaceImage 객체 생성 및 리스트에 추가
	            SpaceImage spaceImage = new SpaceImage("/images/" + savedFilename, space);
	            space.getImages().add(spaceImage); // 엔티티에 추가
	        }
	    }
		
		spaceService.saveSpace(space);
		
		rttr.addFlashAttribute("successMsg", "새로운 공유 공간이 성공적으로 등록되었습니다.");
		
		return "redirect:/admin/spaces";
	}
	
	@GetMapping("/spaces/delete")
	public String adminSpaceDelete(@RequestParam("id") Long id, HttpSession session, Model model, RedirectAttributes rttr ) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		
		if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
			
			return "redirect:/user/login";
		}
		
		spaceService.deleteSpace(id);
		
		rttr.addFlashAttribute("successMsg", "공간이 성공적으로 삭제되었습니다.");
		
		return "redirect:/admin/spaces";
	}
	
	// 수정 폼 이동
	@GetMapping("/spaces/edit")
	public String adminSpaceEditForm(@RequestParam(value = "id", required = false) Long id, 
	                                 HttpSession session, 
	                                 org.springframework.ui.Model model, 
	                                 RedirectAttributes rttr) {
		// 🔒 보안 검문
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) { 
			return "redirect:/user/login";
		}

		// 만약 id가 주소창에 안 넘어왔거나 비어있으면 목록으로 튕겨냄
		if (id == null) {
			rttr.addFlashAttribute("msg", "잘못된 접근입니다. 공간 ID가 누락되었습니다.");
			return "redirect:/admin/spaces";
		}

		// 기존에 등록된 공간 정보를 DB에서 조회
		Space space = spaceService.getSpaceById(id);
		
		// 💡 [핵심 방어벽] 만약 DB에 해당 ID의 공간이 없으면 null 에러를 방지하고 리다이렉트
		if (space == null) {
			rttr.addFlashAttribute("msg", "존재하지 않거나 삭제된 공간입니다.");
			return "redirect:/admin/spaces";
		}
		
		model.addAttribute("space", space);

		return "admin/space-edit"; 
	}
	
	// 수정 처리
	@PostMapping("spaces/edit")
	public String adminSpaceEditForm(@RequestParam("id") Long id,
																 @RequestParam("name") String name,
																 @RequestParam("category") String category,
																 @RequestParam("pricePerHour") int pricePerHour,
																 @RequestParam("address") String addres,
																 @RequestParam("imageUrl") String imageUrl,
																 @RequestParam("description") String description,
																 HttpSession session, RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
			
			return "redirect:/user/login";
		}
		
		Space space = spaceService.getSpaceById(id);
		
		if(space != null) {
			space.setName(name);
			space.setCategory(category);
			space.setPricePerHour(pricePerHour);
			space.setDescription(description);
			space.setImageUrl(imageUrl);
			space.setAddress(addres);
			
			spaceService.saveSpace(space);
		}
		
		rttr.addFlashAttribute("successMsg", "공간정보가 성공적으로 수정되었습니다.");
		
		return "redirect:/admin/spaces";
	}
	
	
	@GetMapping("/notices")
	public String adminNotice(HttpSession session, Model model, RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		
		if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
			rttr.addFlashAttribute("msg", "최고 관리자권한이 필요한 구역입니다.");
			return "redirect:/user/login";
		}
		
		List<Notice> noticeList = noticeService.getAllNotice();
		model.addAttribute("noticeList", noticeList);
		
		return "admin/notices";
	}
	
	
	@GetMapping("/notices/add")
	public String adminNoticeAddForm(HttpSession session, RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		
		if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
			return "redirect:/user/login";
		}
		
		return "admin/notice-add";
	}
	
	@PostMapping("/notices/add")
	public String adminNoticeAddProcess(@RequestParam("title") String title,
																		@RequestParam("content") String content,
																		HttpSession session, RedirectAttributes rttr) {
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
			
			return "redirect:/user/login";
		}
		
		Notice notice = new Notice();
		notice.setTitle(title);
		notice.setContent(content);
		
		noticeService.saveNotice(notice);
		
		rttr.addFlashAttribute("successMsg", "새로운 공지사항이 등록되었습니다.");
		
		return "redirect:/admin/notices";
	}
	
	@GetMapping("/notices/delete")
	public String adminNoticeDelete(@RequestParam("id") Long id, HttpSession session, RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) { 
			return "redirect:/user/login";
		}
		
		noticeService.deleteNotice(id);
		
		rttr.addFlashAttribute("successMsg", "공지사항이 삭제되었습니다.");
		return "redirect:/admin/notices";
	}
	
	@GetMapping("/notices/edit")
	public String adminNoticeEditForm(@RequestParam("id") Long id, HttpSession session, org.springframework.ui.Model model, RedirectAttributes rttr) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) { 
			return "redirect:/user/login";
		}

		// 💡 기존 공지사항 단건 조회 (예: getNoticeById 또는 findById 등)
		Notice notice = noticeService.getNoticeById(id);
		model.addAttribute("notice", notice);

		return "admin/notice-edit"; // templates/admin/notice-edit.html 오픈
	}
	
	
	@PostMapping("/notices/edit")
	public String adminNoticeEditProcess(@RequestParam("id") Long id,
	                                     @RequestParam("title") String title,
	                                     @RequestParam("content") String content,
	                                     HttpSession session, RedirectAttributes rttr) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) { 
			return "redirect:/user/login";
		}

		Notice notice = noticeService.getNoticeById(id);
		if (notice != null) {
			notice.setTitle(title);
			notice.setContent(content); // 아까 추가한 Setter 작동!
			
			noticeService.saveNotice(notice); // JPA가 ID를 보고 알아서 Update 쿼리를 날려줍니다.
		}

		rttr.addFlashAttribute("successMsg", "공지사항이 성공적으로 수정되었습니다.");
		return "redirect:/admin/notices";
	}
	
	
	@GetMapping("/reservations")
	public String adminReservations(HttpSession session, Model model, RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) { 
            rttr.addFlashAttribute("msg", "최고 관리자 권한이 필요한 구역입니다.");
            return "redirect:/user/login";
        }
        
        List<Reservation> reservationList = reservationService.getAllReservations();
        
        if(reservationList == null) {
        		reservationList = new java.util.ArrayList<>();
        }
        
        
		model.addAttribute("reservationList", reservationList);
        
		return "admin/reservations";
	}
	
	@GetMapping("/reservations/cancel")
	public String adminReservationCancel(@RequestParam("id") Long id, HttpSession session, RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) { 
			return "redirect:/user/login";
		}
		
		Reservation res = reservationService.getReservationById(id);
		
		if(res != null) {
			res.setStatus("예약 취소");
			reservationService.saveReservation(res);
			rttr.addFlashAttribute("successMsg", "해당 예약이 성공적으로 강제 취소 처리되었습니다.");
		}else {
			rttr.addFlashAttribute("errorMsg", "존재하지 않는 예약 번호입니다.");
		}
		
		return "redirect:/admin/reservations";
	}
	
	@GetMapping("/qna")
	public String adminQnaList(HttpSession session, Model model, RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) { 
            return "redirect:/user/login";
        }
        
        List<Qna> qnaList = qnaService.getAll();
		if(qnaList == null) {
			qnaList = new java.util.ArrayList<>();
		}
        model.addAttribute("qnaList", qnaList);
		
		return "admin/qna";
	}
	
	@PostMapping("/qna/reply")
	public String adminQnaReplyProcess(@RequestParam("id") Long id,
	                                   @RequestParam("content") String content,
	                                   HttpSession session, RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) { 
			return "redirect:/user/login";
		}
		
		Qna qna = qnaService.getQnaById(id);
		
		if (qna != null) {
			// 새로운 QnaReply 자식 객체 조립
			QnaReply reply = new QnaReply();
			reply.setContent(content);
			reply.setWriter("최고관리자"); 
			reply.setCreateAt(java.time.LocalDateTime.now());
			reply.setQna(qna); // 양방향 매핑 연동
			
			// 부모 객체의 자식 리스트에 바인딩
			qna.getReplies().add(reply);
			
			// cascade 옵션에 의해 자동으로 자식 테이블까지 인서트 쿼리가 전파됩니다.
			qnaService.saveQna(qna); 
			
			rttr.addFlashAttribute("successMsg", "답변이 성공적으로 등록되었습니다.");
		}
		
		return "redirect:/admin/qna";
	}
	
	@GetMapping("/users")
	public String adminUser(HttpSession session, RedirectAttributes rttr, Model model) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
			
			return "redirect:/user/login";
		}
		
		List<User> userList = userService.getAllUsers();
		
		if(userList == null) {
				userList = new java.util.ArrayList<User>();
		}
		
		model.addAttribute("userList", userList);
		
		return "admin/users";
	}
}

