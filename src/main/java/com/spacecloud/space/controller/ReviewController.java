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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spacecloud.space.domain.Reservation;
import com.spacecloud.space.domain.Review;
import com.spacecloud.space.domain.Space;
import com.spacecloud.space.domain.User;
import com.spacecloud.space.service.ReviewService;
import com.spacecloud.space.service.SpaceService;
import com.spacecloud.space.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/review")
public class ReviewController {
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SpaceService spaceService;
	
	private String uploadDir;
	
	@GetMapping("/write")
	public String reviewWriteForm(@RequestParam("resId") Long resId,
														  @RequestParam("spaceId") Long spaceId,
															HttpSession session, Model model) {
		
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null) {
			return "redirect:/user/login";
		}
		
	//	Reservation reservation  = reviewService.getReservationById(resId);
		
		model.addAttribute("reservationId", resId);
		model.addAttribute("spaceId", spaceId);
		
		return "review-write";
	}
	
	@PostMapping("/submit")
	public String submitReview(
			@RequestParam("reservationId") Long reservationId,
			@RequestParam("rating") int rating,
			@RequestParam("content") String content,
			// 🟢 1. 프론트 HTML의 file input name과 대칭되는 파일 매개변수 추가!
			@RequestParam(value = "reviewImage", required = false) MultipartFile file,
			HttpSession session) {
				
		User loginUser = (User) session.getAttribute("loginUser");
		if(loginUser == null) {
			return "redirect:/user/login";
		}
		
		Reservation reservation = reviewService.getReservationById(reservationId);
		
		Review review = new Review();
		
		review.setReservationId(reservationId);
		review.setUserId(loginUser.getId());
		review.setSpaceId(reservation.getSpaceId());
		review.setRating(rating);
		review.setContent(content);
		
		// 🟢 2. [리뷰 파일 업로드 처리 엔진 장착]
		if (file != null && !file.isEmpty()) {
		    try {
		        String originalFilename = file.getOriginalFilename();
		        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
		        String savedFilename = UUID.randomUUID().toString() + ext; // 고유 ID 생성
		        
		        // 🟢 [핵심 해결] 널 포인터를 방지하기 위해 강제로 물리 경로 지정 및 폴더 생성 디펜스 추가
		        String fixedUploadDir = "C:/upload"; 
		        File folder = new File(fixedUploadDir);
		        if (!folder.exists()) {
		            folder.mkdirs(); // C:/upload 폴더가 없으면 에러 안 나게 자동으로 생성
		        }
		        
		        // 지정된 C:/upload/ 폴더에 실제 저장
		        File saveFile = new File(folder, savedFilename);
		        file.transferTo(saveFile);
		        
		        // 🟢 [중요] 이미 DB 저장 경로에 "/images/"를 붙여서 넣고 계셨네요!
		        review.setImageUrl("/images/" + savedFilename);
		        
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		} else {
		    review.setImageUrl(""); // 이미지를 첨부하지 않았을 때 빈 값 디펜스
		}
		
		// 오타 수정 반영 (saveReivew ➡️ saveReview 자바 메서드 명칭 유지)
		reviewService.saveReivew(review);
		
		return "redirect:/user/reservation-list";
	}
	
	@GetMapping("/detail")
	public String reviewDetailPage(@RequestParam("id") Long id, Model model) {
		
		Review review = reviewService.getReviewById(id);
		
		model.addAttribute("review", review);
		
		return "review-detail";
	}
	
	@GetMapping("/list")
	public String reviewListPage(Model model) {
		
		List<Review> reviewList = reviewService.getAllReviewDESC();
		
		for (Review review : reviewList) {
            // 유저 번호로 로그인 ID 찾아서 주입
			if (review.getUserId() != null) {
			    // 🟢 findById 대신 기존에 구현되어 있던 유저 조회 메서드 호출
			    User user = userService.getUserById(review.getUserId()); 
			    if (user != null) {
			        review.setUserLoginId(user.getLoginId());
			    }
			}
            
            // 공간 번호로 공간 이름 찾아서 주입
            if (review.getSpaceId() != null) {
                Space space = spaceService.findById(review.getSpaceId()); // 공간 서비스 단건조회 호출
                if (space != null) {
                    review.setSpaceName(space.getName());
                }
            }
        }
		
		model.addAttribute("reviewList", reviewList);
		
		return "reviewList";
	}
	

	
}
