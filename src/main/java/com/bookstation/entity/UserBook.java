package com.bookstation.entity;

import java.time.LocalDate;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="user_book")
@Getter
@Setter
public class UserBook {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="book_id")
	private Book book;
	
	@ManyToOne
	@JoinColumn(name="platform_id")
	private Platform platform;
	
	private String status;
	private Integer rating;
	private String memo;
	
	private LocalDate purchased_at;	
	
	@CreationTimestamp
	private Date created_at;
	
	
	@PrePersist
	public void prePersist()
	{
		if(this.status == null)
		{
			this.status = "UNREAD";
		}
		
		if(this.purchased_at == null && !this.status.equals("WISHLIST"))
		{
			this.purchased_at = LocalDate.now();
		}
	}
}
