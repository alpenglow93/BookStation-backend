package com.bookstation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;
import com.bookstation.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	@Query(value = """
						with taste as (
				select avg(b.embedding) as taste
				from user_book ub
				join book b on b.id = ub.book_id
				cross join lateral generate_series(
					1,
					case
						when ub.rating = 5 then 3
						when ub.rating = 4 then 2
						when ub.rating <= 2 then 0
						else 1
					end
				) as g
				where ub.status in ('UNREAD', 'READING', 'COMPLETED')
					and b.embedding is not null
					and (CAST(:category AS text) IS NULL OR b.category LIKE :category || '%')
			)
			select b.id, b.title, b.author, b.genre, b.category, b.cover_url, b.synopsis , b.source, b.created_at
			from book b, taste t
			where b.embedding  is not null
			and b.id not in (
				select ub.book_id
				from user_book ub
			)
			and not exists (
				select 1
				from user_book ub2
				join book lb on lb.id = ub2.book_id
				where lb.embedding <=> b.embedding < 0.07
			)
			and t.taste is not null
			and (CAST(:category AS text) IS NULL OR b.category LIKE :category || '%')
			order by b.embedding <=> t.taste
			limit :limit
						""", nativeQuery = true)
	public List<Book> findRecommendBook(@Param("category") String category, @Param("limit") int limit);
	
	public Page<Book> findByTitleContainingAndCategoryStartingWith(String title, String category, Pageable pageable);
	
	// @Query는 기본적으로 조회용이기 때문에 update나 delete를 하려면 @Modifying을 붙여줘야한다
	@Modifying
	@Query(value = """
			update book set embedding = cast(:embedding as vector)
			where id = :id
			""", nativeQuery = true)
	int updateEmbedding(@Param("id") Long id, @Param("embedding") String embedding);
}
