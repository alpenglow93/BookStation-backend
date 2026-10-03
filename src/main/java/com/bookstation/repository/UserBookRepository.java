package com.bookstation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstation.entity.UserBook;

public interface UserBookRepository extends JpaRepository<UserBook, Long>
{
	public boolean existsByBookIdAndPlatformId(Long bookId, Long platformId);
	List<UserBook> findByStatusInAndRatingGreaterThanEqual(List<String> statuses, Integer rating);
}
