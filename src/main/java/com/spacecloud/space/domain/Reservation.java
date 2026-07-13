package com.spacecloud.space.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reservation")
@Getter
@Setter
public class Reservation {
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
		
		
		private Long userId;
		private Long spaceId;
		
		private String spaceName;
		private String imgUrl;
		private String spaceContent;
		private String reserveDate;
		private String startTime;
		private String status;
		private String price;
		private String usageHours;
		
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getSpaceName() {
			return spaceName;
		}

		public void setSpaceName(String spaceName) {
			this.spaceName = spaceName;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public String getSpaceContent() {
			return spaceContent;
		}

		public void setSpaceContent(String spaceContent) {
			this.spaceContent = spaceContent;
		}

		public String getReserveDate() {
			return reserveDate;
		}

		public void setReserveDate(String reserveDate) {
			this.reserveDate = reserveDate;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Long getSpaceId() {
			return spaceId;
		}

		public void setSpaceId(Long spaceId) {
			this.spaceId = spaceId;
		}

		public String getUsageHours() {
			return usageHours;
		}

		public void setUsageHours(String usageHours) {
			this.usageHours = usageHours;
		}
		
		public String getFormattedUsageTime() {
			if (this.startTime == null || this.usageHours == null || this.startTime.isEmpty() || this.usageHours.isEmpty()) {
				return "시간 정보 미기입";
			}
			
			try {
				// 숫자만 정교하게 추출 시도
				String startStr = this.startTime.replaceAll("[^0-9]", "");
				String hoursStr = this.usageHours.replaceAll("[^0-9]", "");
				
				if(!startStr.isEmpty() && !hoursStr.isEmpty()) {
					int start = Integer.parseInt(startStr);
					int hours = Integer.parseInt(hoursStr);
					int end = start + hours;
					return start + "시 ~ " + end + "시 (" + this.usageHours + ")";
				}
				
				// 숫자가 없는 특이한 데이터 포맷일 경우 원본 결합 리턴
				return this.startTime + " ~ (" + this.usageHours + ")";
			} catch (Exception e) {
				// 🛡️ 예외 발생 시 빈값을 내뿜지 않고 무조건 들어있는 문자열 그대로 살려서 화면에 방어 출력!!
				return this.startTime + " / " + this.usageHours;
			}
		}
		
		
		
		
		
		
}