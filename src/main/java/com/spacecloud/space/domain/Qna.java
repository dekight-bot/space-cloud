package com.spacecloud.space.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qna {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Long id;
	private Long spaceId;
	
	private String userLoginId;
	private String title;
	private String content;
	private  String category;
	private String password;
	private String imageUrl;
	private LocalDateTime createAt;
	@OneToMany(mappedBy = "qna", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<QnaReply> replies = new ArrayList<>();
	
	 public Long getId() { return id; }
	 
	 public void setId(Long id) {
		 
		 this.id = id;
	 }

	 public String getTitle() {
		return title;
	 }

	 public void setTitle(String title) {
		this.title = title;
	 }

	 public String getContent() {
		return content;
	 }

	 public void setContent(String content) {
		this.content = content;
	 }

	 public String getCategory() {
		return category;
	 }

	 public void setCategory(String category) {
		this.category = category;
	 }

	 public String getPassword() {
		return password;
	 }

	 public void setPassword(String password) {
		this.password = password;
	 }

	 public LocalDateTime getCreateAt() {
		return createAt;
	 }

	 public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	 }
	 
	 
	 public List<QnaReply> getReplies() {
		    return this.replies;
		}

		public void setReplies(List<QnaReply> replies) {
		    this.replies = replies;
		}

		public String getUserLoginId() {
			return userLoginId;
		}

		public void setUserLoginId(String userLoginId) {
			this.userLoginId = userLoginId;
		}

		public Long getSpaceId() {
			return spaceId;
		}

		public void setSpaceId(Long spaceId) {
			this.spaceId = spaceId;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

	

	
}
	