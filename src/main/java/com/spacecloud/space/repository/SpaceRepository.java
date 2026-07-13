package com.spacecloud.space.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spacecloud.space.domain.Space;

public interface SpaceRepository extends JpaRepository<Space, Long>{
	
	List<Space> findByCategoryOrderByIdDesc(String category);

	List<Space> findByNameContainingOrDescriptionContaining(String nameKeyword, String descKeyword);
	
	List<Space> findAllByOrderByIdDesc();
	
	@Query("SELECT s FROM Space s LEFT JOIN FETCH s.images")
	List<Space> findAllWithImages();

	

	@Query("SELECT s FROM Space s WHERE s.category = :category")
    List<Space> findByCategory(@Param("category") String category);
	
}
