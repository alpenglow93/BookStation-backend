package com.bookstation.controller;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstation.dto.RecommendResponseDTO;
import com.bookstation.service.RecommendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RecommendController 
{
	private final RecommendService rService; 
	
	@GetMapping("/api/recommendations")
	public ResponseEntity<Map> get_recommend(@RequestParam(value = "category", required = false) String category, @RequestParam(value = "limit", defaultValue = "10") int limit)
	{
		Map map = new HashMap<>();
		List<RecommendResponseDTO> list = new ArrayList<>();
		
		try {
			
			list = rService.recommendWithReason(category, limit);
			map.put("list", list);
			
		} catch (Exception ex) 
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(map);
	}
	

}
