package com.spacecloud.space.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spacecloud.space.domain.Qna;
import com.spacecloud.space.domain.Reservation;
import com.spacecloud.space.domain.Review;
import com.spacecloud.space.domain.Space;
import com.spacecloud.space.domain.User;
import com.spacecloud.space.repository.UserRepository;
import com.spacecloud.space.service.QnaService;
import com.spacecloud.space.service.ReservationService;
import com.spacecloud.space.service.ReviewService;
import com.spacecloud.space.service.SpaceService;
import com.spacecloud.space.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class Usercontroller {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private com.spacecloud.space.service.ReservationService reservationService;
	
	@Autowired
	private SpaceService spaceService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private QnaService qnaService;
	
	// 회원가입
	@GetMapping("/join")
	public String joinPage() {

		return "user/join";
	}

	// 회원가입 데이터 처리
	@PostMapping("/join")
	public String joinProcess(@ModelAttribute User user) {

		userService.join(user);

		return "redirect:/user/login";

	}

	@GetMapping("/login")
	public String loginPage() {

		return "user/login";
	}

	@PostMapping("/login")
	public String loginProcess(@RequestParam("loginId") String loginId, @RequestParam("password") String password,
			HttpServletRequest request, Model model) {

		User loginUser = userService.login(loginId, password);

		if (loginUser == null) {
			model.addAttribute("loginError", "아이디 또는 비밀번호가 일치하지 않습니다.");

			return "user/login";
		}

		HttpSession session = request.getServerName() != null ? request.getSession() : null;
		session = request.getSession();

		session.setAttribute("loginUser", loginUser);
		
		if("ADMIN".equals(loginUser.getRole())) {
				return "redirect:/admin/home";
		}

		return "redirect:/";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
			if(session != null) {
				session.invalidate();
			}
		return "redirect:/";
	}

	@GetMapping("/check-id")
	@ResponseBody
	public Map<String, Boolean> checkId(@RequestParam("loginId") String loginId) {

		boolean isDuplicate = userRepository.findByLoginId(loginId).isPresent();

		Map<String, Boolean> response = new HashMap<>();
		response.put("isDuplicate", isDuplicate);

		return response;
	}

	@GetMapping("/mypage")
	public String mypage() {

		return "user/mypage";
	}

	@GetMapping("/reservation-list")
	public String reservation(HttpSession session, Model model) {
		
		User loginUser = (User) session.getAttribute("loginUser"); 
		if(loginUser == null) { 
			return "redirect:/user/login"; 
		}
		
		// 1. 예약 내역 조회 및 모델 전송
		List<Reservation> dbList = reservationService.getReservationsByUserId(loginUser.getId());
		model.addAttribute("reservationList", dbList); // 👈 템플릿 th:each="res : ${reservationList}"과 매칭!
		
		Map<Long, String> spaceNameMap = new HashMap<Long, String>();
		for(Reservation res : dbList) {
			if(res.getSpaceId() != null) {
				Space space = spaceService.getSpaceById(res.getSpaceId());
					if(space != null) {
							spaceNameMap.put(res.getSpaceId(), space.getName());
					}
			}
		}
		
		model.addAttribute("spaceNameMap", spaceNameMap);
		
		// 2. 내가 남긴 후기 조회 및 모델 전송
		List<Review> myReviewList = reviewService.getReviewSpaceId(loginUser.getLoginId());
		model.addAttribute("myReviewList", myReviewList);
		
		// 3. 내가 남긴 Q&A 문의 내역 조회 및 모델 전송
		List<Qna> myQnaList = qnaService.getQnaListByLoginId(loginUser.getLoginId());
		model.addAttribute("myQnaList", myQnaList);
		
		return "user/reservation-list";
	}
	
	@PostMapping("/update-profile")
	public String updateProfile(@RequestParam(value="newPassword", required=false) String newPassword,
													@RequestParam("name") String name,
													@RequestParam("email") String email,
													@RequestParam("address") String address,
													HttpSession session, 
													RedirectAttributes rttr) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null) {
			return "redirect:/user/login";
		}
		
		loginUser.setName(name);
		loginUser.setEmail(email);
		loginUser.setAddress(address);
		
		if(newPassword != null && !newPassword.trim().isEmpty()) {
				loginUser.setPassword(newPassword);
		}
		
		userService.join(loginUser);
		session.setAttribute("loginUser", loginUser);
		
		rttr.addFlashAttribute("successMsg", "개인정보 수정이 완료되었습니다.");
		
		return "redirect:/user/reservation-list";
	}
	
	
	@GetMapping("/space-detail")
	public String spaceDetail(HttpServletRequest request, Model model) {
		
		String spaceIdParam = request.getParameter("spaceId");
		Long spaceId = 1L;
		
		if(spaceIdParam != null && !spaceIdParam.isEmpty()) {
			try {
				spaceId = Long.parseLong(spaceIdParam);
			}catch(NumberFormatException e){
				spaceId = 1L;
			}
		}
		
		model.addAttribute("currentSpaceId", spaceId);
		
		return "space-detail";
	}
	
	@GetMapping("/space/detail/{id}")
	public String spaceDetaiPage(@PathVariable("id") Long id, Model model) {
		
		Space space = spaceService.getSpaceById(id);
		model.addAttribute("space", space);
		
		List<Review> reviewList = reviewService.getReviewSpaceId(id);
		model.addAttribute("reviewList", reviewList);
		
		List<Qna> qnaList = qnaService.getQnaListBySpaceId(id);
		model.addAttribute("qnaList", qnaList);
		
		List<Review> reviews = reviewService.findBySpaceId(id);
		model.addAttribute("reviews", reviews);
		
		return "space-detail";
	}
	
	
}
