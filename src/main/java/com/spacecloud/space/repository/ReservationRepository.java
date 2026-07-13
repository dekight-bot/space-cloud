package com.spacecloud.space.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spacecloud.space.domain.Reservation;
import com.spacecloud.space.domain.User;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>{
	
	@Modifying
	@Query("DELETE FROM Reservation r WHERE (r.status = '취소완료' OR r.status = '이용 완료') AND r.reserveDate <= :targetDate")
	void deleteOldReservations(@Param("targetDate") String targetDate);
	
	/**
     * 🛡️ 중복 예약 방어용 쿼리
     * 조건: 동일한 공간(spaceId) + 동일한 날짜(reserveDate) + 예약취소가 안 된 상태 중
     *       사용자가 요청한 시작시간~종료시간이 기존 예약 시간대와 교집합이 발생하는지 검사
     */
//    @Query("SELECT r FROM Reservation r WHERE r.spaceId = :spaceId " +
//           "AND r.reserveDate = :reserveDate " +
//           "AND r.status <> '예약취소' AND r.status <> 'CANCELLED' " +
//           "AND (:startTime < r.endTime AND :endTime > r.startTime)")
//    List<Reservation> findOverlappingReservations(
//            @Param("spaceId") Long spaceId,
//            @Param("reserveDate") String reserveDate,
//            @Param("startTime") String startTime,
//            @Param("endTime") String endTime
//    );
	
    List<Reservation> findBySpaceIdAndReserveDate(Long spaceId, String reserveDate);
	
	List<Reservation> findByUserIdOrderByIdDesc(Long userId);

	
	
}
