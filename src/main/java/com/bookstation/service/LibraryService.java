package com.bookstation.service;

import java.util.List;

import com.bookstation.dto.UserBookRequestDTO;
import com.bookstation.dto.UserBookResponseDTO;
import com.bookstation.entity.Platform;

public interface LibraryService 
{
	public Long addUserBook(UserBookRequestDTO dto);
	public List<UserBookResponseDTO> userBookList();
	public void updateUserBook(UserBookRequestDTO dto, Long id);
	public void deleteUserBook(Long id);
	
	public List<Platform> platformsListData();
}
