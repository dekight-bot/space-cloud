package com.spacecloud.space.dto;

import lombok.Data;

@Data
public class ReservationDTO {
		
		private Long spaceId; // 공간 명
		private String reserveDate;
		private int startTime; // 이용 시작 시간
		private int usageHours; // 이용시간
		private int price;
		
		
		public Long getSpaceId() {
			return spaceId;
		}
		public void setSpaceId(Long spaceId) {
			this.spaceId = spaceId;
		}
		public String getReserveDate() {
			return reserveDate;
		}
		public void setReserveDate(String reserveDate) {
			this.reserveDate = reserveDate;
		}
		public int getStartTime() {
			return startTime;
		}
		public void setStartTime(int startTime) {
			this.startTime = startTime;
		}
		public int getUsageHours() {
			return usageHours;
		}
		public void setUsageHours(int usageHours) {
			this.usageHours = usageHours;
		}
		public int getPrice() {
			return price;
		}
		public void setPrice(int price) {
			this.price = price;
		}
	
	
}
