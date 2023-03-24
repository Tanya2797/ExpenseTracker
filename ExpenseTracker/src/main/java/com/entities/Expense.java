package com.entities;

import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class Expense {
	
	private Long id;
	
//    @Size(min = 2, max = 30, message = "Name should be between 2 to 30 characters")
//	@NotEmpty(message="Name cannot be empty")
//	@NotNull(message="Name cannot be null")
//	private String name;
	
    @NotNull(message="userId cannot be null")
    private Long userId;
    
	@Size(min = 2, max = 50, message = "Title should be between 2 to 50 characters")
	@NotEmpty(message="Title cannot be empty")
	@NotNull(message="Title cannot be null")
	private String title;
	
	@Max(value = (long) Float.MAX_VALUE, message = "Amount should be in the acceptable range")
	@Positive(message="amount has to be a positive value")
	@NotNull(message="amount cannot be null")
	private float amount;

	@Size(min = 2, max = 20, message = "Category should be between 2 to 20 characters")
	@NotEmpty(message="Category cannot be empty")
	@NotNull(message="category cannot be null")
	private String category;
	
	@PastOrPresent(message="Future dates not accepted")
	@NotNull(message="Date cannot be null")
	private Date date;
	
	private String notes;
	
	public Expense() {
		
	}
	
	public Expense(Long id, Long userId, String title, float amount, String category,  Date date, String notes) 
	{
		super();
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.category = category;
		this.amount = amount;
		this.date = date;
		this.notes = notes;
	}
	
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	

}
