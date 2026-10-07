package com.bookstation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bookstation.dto.UserBookRequestDTO;
import com.bookstation.dto.UserBookResponseDTO;
import com.bookstation.entity.Platform;
import com.bookstation.service.LibraryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LibraryController 
{
	private final LibraryService lService;
	
	
	@PostMapping("/api/library")
	public ResponseEntity<Map> book_post_library(@RequestBody UserBookRequestDTO dto)
	{
		Map map = new HashMap();
		
		try {
			Long id = lService.addUserBook(dto);
			map.put("id", id);
			
		} 
		catch (IllegalStateException ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		catch(NoSuchElementException ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("/api/library")
	public ResponseEntity<Map> book_get_library()
	{
		Map map = new HashMap();
		
		try {
			List<UserBookResponseDTO> list = lService.userBookList();
			
			map.put("list", list);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(map);
	}
	
	@PatchMapping("/api/library/{id}")
	public ResponseEntity<Void> book_patch_library(@PathVariable("id") Long id, @RequestBody UserBookRequestDTO dto)
	{
		try {
			lService.updateUserBook(dto, id);
			
		}
		catch(NoSuchElementException ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		catch (IllegalStateException ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/api/library/{id}")
	public ResponseEntity<Void> book_delete_library(@PathVariable("id") Long id)
	{
		try {
			lService.deleteUserBook(id);
			
		}
		catch(NoSuchElementException ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/api/platforms")
	public ResponseEntity<Map> platforms_get()
	{
		Map map = new HashMap();
		
		try {
			List<Platform> list = lService.platformsListData();
			map.put("list", list);
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(map);
	}
}
