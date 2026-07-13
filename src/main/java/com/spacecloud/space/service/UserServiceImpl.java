package com.spacecloud.space.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacecloud.space.domain.User;
import com.spacecloud.space.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;

	@Override
	public User join(User user) {
		
		user.setCreateAt(LocalDateTime.now());
		
		
		if(user.getRole() == null || user.getRole().isEmpty()) {
			user.setRole("USER");
		}
		return userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public User login(String loginId, String password) {
		
		Optional<User> findUserOpt = userRepository.findByLoginId(loginId);
		
		if(findUserOpt.isPresent()) {
			User user = findUserOpt.get();
			if(user.getPassword().equals(password)) {
				return user;
			}
		}
		return null;
	}
	
	@Override
	public List<User> getAllUsers(){
		
		return userRepository.findAll();
	}

	@Override
	public Object findByloginId(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findById(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserById(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
