package com.spacecloud.space.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "qna_reply")
public class QnaReply {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;
	
	private String writer;
	
	private LocalDateTime createAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "qna_id", nullable = false)
	private Qna qna;
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public Qna getQna() {
        return qna;
    }

    public void setQna(Qna qna) {
        this.qna = qna;
    }
}
