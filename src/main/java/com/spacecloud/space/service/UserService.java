package com.spacecloud.space.service;

import java.util.List;

import com.spacecloud.space.domain.User;

public interface UserService {
	
	
	// 회원가입 기능!(가입시 유저 객체를 반환)
	User join(User user);
	
	// 실질적인 로그인 기능
	User login(String loginId, String password);
	
	List<User> getAllUsers();

	Object findByloginId(Long userId);

	User findById(Long userId);

	User getUserById(Long userId);

}