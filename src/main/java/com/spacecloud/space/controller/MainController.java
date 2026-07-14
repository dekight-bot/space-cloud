package com.spacecloud.space.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spacecloud.space.domain.Review;
import com.spacecloud.space.domain.Space;
import com.spacecloud.space.service.ReviewService;
import com.spacecloud.space.service.SpaceService;

@Controller
public class MainController {
	
	@Autowired
	private SpaceService spaceService;
	
	@Autowired
	private ReviewService reviewService;
	
	// 🟢 메인 홈('/')과 '/index'로 들어왔을 때 둘 다 이 메서드가 실행되도록 합칩니다!
	@GetMapping({"/", "/index"})
	public String mainHome(
			@RequestParam(value = "category", required = false, defaultValue = "all") String category, 
	        Model model) {
		
		// DB에서 진짜 공간 데이터 긁어오기
		List<Space> spaceList;
		
		if("all".equals(category) || category == null) {
			spaceList = spaceService.getAllSpace();
		}else {
			spaceList = spaceService.getSpaceByCategory(category);
		}
		
		// "spaceList"라는 이름으로 상자에 포장해서 index.html로 배달!
		model.addAttribute("spaceList", spaceList);
		
		List<Review> mainReviewList = reviewService.getAllReview();
		model.addAttribute("mainReviewList", mainReviewList);
		
		return "index";
	}
	
	@GetMapping("/space/list")
	public String spaceList(@RequestParam(value = "category", required = false, defaultValue = "all") String category, Model model) {
	    
	    // 1. 카테고리에 따른 리스트 조회
	    List<Space> spaceList;
	    if("all".equals(category)) {
	        spaceList = spaceService.findAll();
	    } else {
	        spaceList = spaceService.findByCategory(category);
	    }
	    
	   
	    model.addAttribute("spaces", spaceList); 
	    model.addAttribute("selectedCategory", category);
	    
	    return "space-list";
	}
	
	
	@GetMapping("/space/search")
    public String searchSpaces(@RequestParam(value = "keyword", required = false) String keyword,
    												@RequestParam(value = "category", required = false) String category, Model model) {
        
		List<Space> list;
		
		System.out.println("전달받은 검색어: " + keyword);
	    
	    List<Space> spaceList = spaceService.searchSpaces(keyword);
	    
	    // 결과 확인!
	    System.out.println("검색된 공간 개수: " + (spaceList != null ? spaceList.size() : "null"));
		
		if(keyword != null && !keyword.isEmpty()) {
			
			list = spaceService.searchSpaces(keyword);
			model.addAttribute("selectedCategory", "검색어 : " + keyword);
		}else if(category != null && !category.isEmpty()){
			list = spaceService.findByCategory(category);
			model.addAttribute("selectedCategory", category);
		}else {
			list = spaceService.getAllSpace();
			model.addAttribute("selectedCategory", "all");
		}
	
        model.addAttribute("spaceList", list);
        
        // 리뷰 리스트도 그대로 유지
        List<Review> mainReviewList = reviewService.getAllReview(); 
        model.addAttribute("mainReviewList", mainReviewList);
        
        return "space-list";
    }
}