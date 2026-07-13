package com.spacecloud.space.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "space_image")
public class SpaceImage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    private String imageUrl; // 파일 경로 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id") // 공간과 연결
    private Space space;

    // 생성자, Getter, Setter
    public SpaceImage() {}

    public SpaceImage(String imageUrl, Space space) {
        this.imageUrl = imageUrl;
        this.space = space;
    }

    public Long getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setSpace(Space space) { this.space = space; }
}
