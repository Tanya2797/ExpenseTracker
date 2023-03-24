package com.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.entities.Expense;
import com.google.appengine.api.datastore.EntityNotFoundException;

public interface ExpenseService {
	
	public Expense addExpense(Expense expense);
	public Expense updateExpense(Expense expense);
	public boolean deleteExpense(long id);
	public Expense getExpenseById(String idString) throws ParseException, NumberFormatException, EntityNotFoundException;
	public ArrayList<Expense> getAllExpenses() throws ParseException;
	public Long customRangeExpense(String startDateString, String endDateString) throws ParseException;
	public Long CalDailyExpense() throws ParseException;
	public List<Expense> getAllExpensesForUser(String userId) throws ParseException;
	public List<Expense> getExpensesInRange(String startDate, String endDate) throws ParseException;
	public Expense expenseExists(Expense expense) throws ParseException;
	

}
