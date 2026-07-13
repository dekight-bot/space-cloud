package com.spacecloud.space.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spacecloud.space.domain.Space;
import com.spacecloud.space.repository.SpaceRepository;

@Service
public class SpaceService {

	@Autowired
	private SpaceRepository spaceRepository;
	
	public List<Space> getAllSpace(){
		
		return spaceRepository.findAllByOrderByIdDesc();
	}
	
	public Space getSpaceById(Long id) {
		
		return spaceRepository.findById(id).orElse(null);
	}
	
	public List<Space> getSpaceByCategory(String category){
		
		return spaceRepository.findByCategoryOrderByIdDesc(category);
	}

	public void saveSpace(Space space) {
		
		spaceRepository.save(space);
		
	}

	public void deleteSpace(Long id) {
	
		spaceRepository.deleteById(id);
		
	}
	
	
	public long getTotalSpaceCount() {
		
		return spaceRepository.count();
	}
	
	public List<Space> searchSpaces(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return spaceRepository.findAll();
        }
        // 바뀐 레포지토리 메서드명인 findByNameContainingOrDescriptionContaining 호출!
        return spaceRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
    }

	public Space findById(Long spaceId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Space> findAll() {
	
		return spaceRepository.findAll();
	}

	public List<Space> findByCategory(String category) {
		
		return spaceRepository.findByCategory(category);
		
	}
}
