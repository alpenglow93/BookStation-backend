package com.bookstation.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstation.dto.ManualBookRequestDTO;
import com.bookstation.entity.Book;
import com.bookstation.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class BookController {
	private final BookService bService;
	
	@GetMapping("/api/books")
	public ResponseEntity<Map> book_list(
				@RequestParam("page") int page, 
				@RequestParam(value = "keyword", required =  false) String keyword, 
				@RequestParam(value = "category", required = false) String category
			)
	{
		Map map = new HashMap();
		
		try 
		{
			Page<Book> result = bService.bookListData(page, keyword, category);
			List<Book> list = result.getContent();
			int totalpage = result.getTotalPages();
			
			int[] datas = bService.getPageData(page, totalpage);
			
			map.put("list", list);
			map.put("curpage", datas[0]);
			map.put("totalpage", datas[1]);
			map.put("startPage", datas[2]);
			map.put("endPage", datas[3]);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("/api/books/{id}")
	public ResponseEntity<Book> book_detail(@PathVariable("id") Long id)
	{
		Book vo = null;
		
		try 
		{
			vo = bService.bookDetailData(id);
			
			if(vo==null)
			{
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(vo);
	}
	
	@PostMapping("/api/books")
	public ResponseEntity<Map> add_book_manual(@Valid @RequestBody ManualBookRequestDTO dto)
	{
		Map map = new HashMap();
		
		try 
		{
			Long id = bService.addManualBook(dto);
			map.put("id", id);
			
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
		return ResponseEntity.ok(map);
	}
	
}
