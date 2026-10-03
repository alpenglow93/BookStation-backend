package com.bookstation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstation.entity.Platform;

public interface PlatformRepository extends JpaRepository<Platform, Long> 
{
	
}
