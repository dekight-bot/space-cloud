package com.spacecloud.space.scheduler;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.spacecloud.space.repository.ReservationRepository;

import jakarta.transaction.Transactional;

@Component
@EnableScheduling
public class ReservationScheduler {
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	
	@Scheduled(cron = "0 0 3 * * ?")
	@Transactional
	public void cleanUpOldReservation() {
		
		LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);
		String targetDateStr = ninetyDaysAgo.toString();
		
		reservationRepository.deleteOldReservations(targetDateStr);
		
		System.out.println("[자동 시스템 로그] 90일이 지난 예약 내역 삭제가 완료되었습니다." + targetDateStr);
	}
}
