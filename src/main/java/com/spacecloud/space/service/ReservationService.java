package com.spacecloud.space.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacecloud.space.domain.Reservation;
import com.spacecloud.space.repository.ReservationRepository;

@Service
@Transactional(readOnly = true)
public class ReservationService {
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	/**
	 * 🛡️ [더블 부킹 원천 차단] 예약 데이터 검증 후 저장 기능
	 * (컨트롤러에서 saveReservation을 부르든 createReservation을 부르든 이쪽으로 관통됩니다)
	 */
	@Transactional
	public boolean saveReservation(Reservation reservation) {
		
		// 1. 내가 예약하려는 시작 시간 추출 (예: "12시" -> 12)
		String start = reservation.getStartTime();
		if (start == null || reservation.getUsageHours() == null) {
			return false;
		}
		
		int reqStart = Integer.parseInt(start.replaceAll("[^0-9]", ""));
		int reqHours = Integer.parseInt(reservation.getUsageHours().replaceAll("[^0-9]", ""));
		int reqEnd = reqStart + reqHours;
		
		// 2. DB에서 '해당 공간'과 '해당 날짜'에 잡힌 예약만 1차 필터링
		List<Reservation> existingList = reservationRepository.findBySpaceIdAndReserveDate(
				reservation.getSpaceId(),
				reservation.getReserveDate()
		);
		
		// 3. 자바 루프를 돌며 시간축 교집합 정밀 검문
		if (existingList != null) {
			for (Reservation exist : existingList) {
				// 이미 취소된 건들을 제외한 '예약완료', '이용 예정' 등의 살아있는 예약만 대조
				if (!"취소 완료".equals(exist.getStatus()) && !"예약취소".equals(exist.getStatus()) && !"CANCELLED".equals(exist.getStatus())) {
					
					int existStart = Integer.parseInt(exist.getStartTime().replaceAll("[^0-9]", ""));
					int existHours = Integer.parseInt(exist.getUsageHours().replaceAll("[^0-9]", ""));
					int existEnd = existStart + existHours; 
					
					// 🛑 핵심 시간대 교집합 판정 (내 시작 < 기존 종료 && 내 종료 > 기존 시작)
					if (reqStart < existEnd && reqEnd > existStart) {
						System.out.println("⚠️ [더블 부킹 백엔드 차단완료] 기존 예약 (" + existStart + "시 ~ " + existEnd + "시)와 겹침!");
						return false; // 방어벽 작동: 즉시 거부 및 리턴 false
					}
				}
			}
		}
		
		// 4. 모든 검문을 통과했다면 정상 저장 진행
		reservation.setStatus("예약완료"); // 스크린샷 규격에 맞게 '예약완료'로 고정
		reservationRepository.saveAndFlush(reservation);
		return true; // 예약 성공
	}
	
	// 💡 호환성을 위해 혹시 컨트롤러에서 createReservation으로 호출할 경우를 대비한 연결 파이프
	@Transactional
	public boolean createReservation(Reservation reservation) {
		return saveReservation(reservation);
	}

	// 예약 유저의 내역 조회
	public List<Reservation> getResservationByUser(Long user){
		return reservationRepository.findByUserIdOrderByIdDesc(user);
	}

	public List<Reservation> getReservationsByUserId(Long userId) {
		return reservationRepository.findByUserIdOrderByIdDesc(userId);
	}
	
	// 예약 취소(상태 업데이트 ) 로직
	@Transactional
	public void cacelReservation(Long id) {
		Reservation reservation = reservationRepository.findById(id).orElse(null);
		if(reservation != null) {
			reservation.setStatus("취소 완료");
			reservationRepository.save(reservation);
		}
	}

	public List<Reservation> getAllReservations() {
		return reservationRepository.findAll();
	}

	public Reservation getReservationById(Long id) {
		return reservationRepository.findById(id).orElse(null);
	}

	public List<Reservation> getAll() {
		return reservationRepository.findAll();
	}
}