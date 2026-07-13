package com.spacecloud.space.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spacecloud.space.domain.Reservation;
import com.spacecloud.space.domain.Space;
import com.spacecloud.space.domain.User;
import com.spacecloud.space.service.ReservationService;
import com.spacecloud.space.service.SpaceService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
	
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private SpaceService spaceService;

	/**
	 * 📝 사용자의 실시간 예약 신청 제어부 (중복 예약 완벽 원천 차단)
	 */
	@PostMapping("/submit")
	public String submitReservation(@RequestParam("spaceId") Long spaceId, 
									@RequestParam("reserveDate") String reserveDate,
									@RequestParam("startTime") String startTime,
									@RequestParam("usageHours") String usageHours,
									HttpSession session, Model model, RedirectAttributes rttr, HttpServletResponse response) throws Exception {
		
		// 1. 로그인 인증 방어벽
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser == null) {
			rttr.addFlashAttribute("msg", "예약은 로그인 후 이용 가능합니다.");
			return "redirect:/user/login";
		}
		
		// 2. 예약 도메인 객체 데이터 셋업
		Reservation reservation = new Reservation();
		reservation.setUserId(loginUser.getId());
		reservation.setSpaceId(spaceId);
		reservation.setReserveDate(reserveDate);
		reservation.setStartTime(startTime);
		reservation.setUsageHours(usageHours);
		
		// 3. 공간 정보 조회 및 가격 연산 바인딩
		Space space = spaceService.getSpaceById(spaceId);
		if(space != null) {
			int hours = Integer.parseInt(usageHours.replace("시간", "").trim());
			int totalPrice = space.getPricePerHour() * hours;
			reservation.setPrice(String.valueOf(totalPrice) + "원");
			
			reservation.setSpaceName(space.getName());
			reservation.setSpaceContent(space.getDescription());
			reservation.setImgUrl(space.getImageUrl());
		}
		
		// 🛡️ 4. 서비스단 검문소에 통과 요청
boolean isApproved = reservationService.saveReservation(reservation);
		
		if (!isApproved) {
			// 🟢 [최종 정치 치트키] 리다이렉트 주소선 충돌을 완전히 피하기 위해 
			// 브라우저 경고창을 띄우고 바로 직전 상세페이지 화면으로 자연스럽게 빽(Back) 시킵니다!
			response.setContentType("text/html; charset=UTF-8");
			java.io.PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('죄송합니다. 선택하신 시간대는 이미 다른 유저가 선점한 시간대입니다. 다른 시간을 선택해주세요.');");
			out.println("history.back();");
			out.println("</script>");
			out.flush();
			return null; // 스크립트를 직접 실행했으므로 뷰 리턴은 null 처리!
		}
		
		// 성공 시 마이페이지 이동
		return "redirect:/user/reservation-list";
	}
	
	/**
	 * ❌ 예약 취소 처리 핸들러
	 */
	@PostMapping("/cancel")
	public String cancelReservation(@RequestParam("id") Long id, RedirectAttributes rttr) {
		reservationService.cacelReservation(id);
		rttr.addFlashAttribute("successMsg", "예약 취소가 완료되었습니다.");
		return "redirect:/user/reservation-list";
	}
	
	
	@GetMapping("/booked-times")
	@ResponseBody
	public List<String> getBookedTimes(@RequestParam("spaceId") Long spaceId,
																	@RequestParam("reserveDate") String reserveDate){
		
		List<String> bookedTimes = new java.util.ArrayList<String>();
		
		java.util.List<Reservation> existingList = reservationService.getAllReservations();
		
		if(existingList != null) {
			for(Reservation exist : existingList) {
					
				if (exist.getSpaceId().equals(spaceId) && reserveDate.equals(exist.getReserveDate()) && "예약완료".equals(exist.getStatus())) {
					
					int startHour = Integer.parseInt(exist.getStartTime().replaceAll("[^0-9]", ""));
					int hours = Integer.parseInt(exist.getUsageHours().replaceAll("[^0-9]", ""));
					
					for (int i = 0; i < hours; i++) {
						bookedTimes.add((startHour + i) + "시");
					}
				}
			}
		}
		
		return bookedTimes;
	}
	
	
	
	
	
	
	
	
	
	
}