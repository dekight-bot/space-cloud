package com.spacecloud.space.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacecloud.space.domain.Reservation;
import com.spacecloud.space.domain.Review;
import com.spacecloud.space.repository.ReservationRepository;
import com.spacecloud.space.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	public Reservation getReservationById(Long resId) {
		
		return reservationRepository.findById(resId).orElse(null);
	}
	
	@Transactional
	public void saveReivew(Review review) {
		
		reviewRepository.save(review);
		
		Reservation res = reservationRepository.findById(review.getReservationId()).orElse(null);
		
			if(res != null) {
				res.setStatus("후기 작성 완료");
				reservationRepository.save(res);
		}
	}
	
	public List<Review> getReviewSpaceId(Long spaceId){
		return reviewRepository.findBySpaceIdOrderByIdDesc(spaceId);
		
	}

	public List<Review> getReviewSpaceId(String loginId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Review> getReviewById(String loginId){
		
		
		return reviewRepository.findByUserId(loginId);
	}

	public List<Review> getAllReview() {
		
		return reviewRepository.findAll();
	}
	
	public Review getReviewById(Long id) {
		
		return reviewRepository.findById(id).orElse(null);
	}

	public List<Review> getAllReviewDESC() {
		
		return reviewRepository.findAllByOrderByIdDesc();
	
	}

	public List<Review> findBySpaceId(Long spaceId) {
	
		return reviewRepository.findBySpaceIdOrderByIdDesc(spaceId);
	}

	

	
}
