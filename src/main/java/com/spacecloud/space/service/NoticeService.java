package com.spacecloud.space.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacecloud.space.domain.Notice;
import com.spacecloud.space.domain.Reservation;
import com.spacecloud.space.repository.NoticeRepository;

@Service
public class NoticeService {

	@Autowired
	private NoticeRepository noticeRepository;
	
	public List<Notice> getAllNotice(){
		
		return noticeRepository.findAllByOrderByIdDesc();
		
	}
	
	@Transactional
	public Notice getNoticeById(Long id) {
		
		Notice notice = noticeRepository.findById(id).orElse(null);
		
		if(notice != null) {
			notice.setViews(notice.getViews() + 1);
			noticeRepository.save(notice);
		}
		
		return notice;
	}

	public void saveNotice(Notice notice) {
		
		noticeRepository.save(notice);
		
		Notice noti = noticeRepository.findById(notice.getId()).orElse(null);
		
		noticeRepository.save(noti);
		
	}

	public void deleteNotice(Long id) {
	
		noticeRepository.deleteById(id);
		
	}

}
	
	
	