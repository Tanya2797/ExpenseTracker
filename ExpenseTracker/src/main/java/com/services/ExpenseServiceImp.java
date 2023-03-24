package com.services;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.entities.Expense;
import com.entities.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class ExpenseServiceImp implements ExpenseService {
	 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Long> allKeys = new ArrayList<>();
	
	public Expense addExpense(Expense expense) 
	{
		
        Entity entity = new Entity("expense");
        entity.setProperty("userId", expense.getUserId());
		entity.setProperty("title", expense.getTitle());
		entity.setProperty("amount", expense.getAmount());
		entity.setProperty("category", expense.getCategory());
		entity.setProperty("date", expense.getDate());
        
        if(expense.getNotes()==null||expense.getNotes().length()==0)
        {
        	expense.setNotes("NA");
        	entity.setProperty("notes", "NA");
        }
        else
        {
        	entity.setProperty("notes",expense.getNotes());
        }
        
        Key key;
        key = datastore.put(entity);
        System.out.println(entity);
        
        //checking if the entity is successfully added to the datastore
        if(key.isComplete())
        {
        	expense.setId(key.getId());
        	return expense;
        }
        else 
        {
        	return null;
        }
        
	}
	
	public Expense updateExpense(Expense expense) 
	{
		if(allKeys.size()==0)
		{

			Query query = new Query("expense").setKeysOnly();
			PreparedQuery pq = datastore.prepare(query);
			Iterable<Entity> iter = pq.asIterable();
			for(Entity e : iter)
			{
				allKeys.add(e.getKey().getId());
			}
			
		}
		if(!allKeys.contains(expense.getId()))
		{
			return null;
		}
		Entity entity = new Entity("expense", expense.getId());
		entity.setProperty("userId", expense.getUserId());
		entity.setProperty("title", expense.getTitle());
		entity.setProperty("amount", expense.getAmount());
		entity.setProperty("category", expense.getCategory());
		entity.setProperty("date", expense.getDate());
		if(expense.getNotes()==null||expense.getNotes().length()==0)
        {
        	expense.setNotes("NA");
        	entity.setProperty("notes", "NA");
        }
        else
        {
        	entity.setProperty("notes",expense.getNotes());
        }
        
            
        Key key = datastore.put(entity);
        if(key.isComplete())
        {
        	return expense;
        }
        else 
        {
        	return null;
        }
	    
	}
	
	public boolean deleteExpense(long id) 
	{
		//System.out.println("Inside the deleteExpense Method");
		if(allKeys.size()==0)
		{
			//System.out.println("allKeys size is 0");

			Query query = new Query("expense").setKeysOnly();
			PreparedQuery pq = datastore.prepare(query);
			Iterable<Entity> iter = pq.asIterable();
			for(Entity e : iter)
			{
				allKeys.add(e.getKey().getId());
				System.out.println("KEY ID = "+e.getKey().getId());
			}
			
		}
		
		if(allKeys.contains(id))
		{
			Key key = KeyFactory.createKey("expense", id);
			datastore.delete(key);
			allKeys.remove(id);
			return true;
		}
		else
		{
			return false;
		}
	
			
	}
	
	public Expense getExpenseById(String idString) throws ParseException, NumberFormatException, EntityNotFoundException 
	{
		Long id = Long.parseLong(idString);
		boolean expenseExists = checkExpenseId(id);
		//Filter keyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, id);
		if(!expenseExists)
		{
			return null;
		}
		Key key = KeyFactory.createKey("expense", id);
		Entity e = datastore.get(key);
		Expense expense = new Expense();
		Long userId = Long.parseLong(e.getProperty("userId").toString());
		String title = e.getProperty("title").toString();
		Float amount = Float.parseFloat(e.getProperty("amount").toString());
		String category = e.getProperty("category").toString();
		Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").parse(e.getProperty("date").toString()); 
		String notes = e.getProperty("notes").toString();
		expense = new Expense(id,userId,title,amount,category,date,notes);
		return expense;
	}
	
	public ArrayList<Expense> getAllExpenses() throws ParseException
	{
		Query query = new Query("expense");
		PreparedQuery pq = datastore.prepare(query);
		Iterable<Entity> iter = pq.asIterable(FetchOptions.Builder.withLimit(10));
		Expense expense;
		ArrayList<Expense> results = new ArrayList<>();
		for(Entity e : iter)
		{
			Long id = e.getKey().getId();
			Long userId = Long.parseLong(e.getProperty("userId").toString());
			String title = e.getProperty("title").toString();
			Float amount = Float.parseFloat(e.getProperty("amount").toString());
			String category = e.getProperty("category").toString();
			Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").parse(e.getProperty("date").toString()); 
			String notes = e.getProperty("notes").toString();
			expense = new Expense(id,userId,title,amount,category,date,notes);
			results.add(expense);
		}
		return results;
		
	}
	
	public Long customRangeExpense(String startDateString, String endDateString) throws ParseException
	{
		Iterable<Entity> list = null;
	    Date startDate = df.parse(startDateString);
		Date endDate = df.parse(endDateString);
		//System.out.println("startdate"+startDate);
		Filter startDateFilter = new FilterPredicate("date",FilterOperator.GREATER_THAN_OR_EQUAL,startDate);
		Filter endDateFilter = new FilterPredicate("date",FilterOperator.LESS_THAN_OR_EQUAL,endDate);
		CompositeFilter dateRangeFilter = CompositeFilterOperator.and(startDateFilter,endDateFilter);
		Query query = new Query("expense").setFilter(dateRangeFilter);
		list = datastore.prepare(query).asIterable();
		Long sum = 0L;
		for(Entity e : list)
		{
			sum = sum + Long.parseLong(e.getProperty("amount").toString());
		}
		return sum;
	}
	
	public Long CalDailyExpense() throws ParseException
	{
		Date date = new Date();
		String currentDateString = df.format(date);
		Iterable<Entity> list = null ;
		date = df.parse(currentDateString);
		Filter filter = new FilterPredicate("date", FilterOperator.EQUAL,date);
		Query query = new Query("expense").setFilter(filter);
		list = datastore.prepare(query).asIterable();
		Long sum = 0L;
		for(Entity e : list)
		{
			sum = sum + Long.parseLong(e.getProperty("amount").toString());
		}
		return sum;	
		
	}
	
	public List<Expense> getAllExpensesForUser(String userIdString) throws NumberFormatException, ParseException
	{
		Long userId = Long.parseLong(userIdString);
		Filter userIdFilter = new FilterPredicate("userId",FilterOperator.EQUAL,userId);
		Query query = new Query("expense").setFilter(userIdFilter);
		PreparedQuery pq = datastore.prepare(query);
		Iterable<Entity> iter = pq.asIterable(FetchOptions.Builder.withLimit(10));
		Expense expense;
		ArrayList<Expense> results = new ArrayList<>();
		for(Entity e : iter)
		{
			Long id = e.getKey().getId();
			String title = e.getProperty("title").toString();
			Float amount = Float.parseFloat(e.getProperty("amount").toString());
			String category = e.getProperty("category").toString();
			Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").parse(e.getProperty("date").toString()); 
			String notes = e.getProperty("notes").toString();
			expense = new Expense(id,userId,title,amount,category,date,notes);
			results.add(expense);
		}
		return results;
		
		
	}
	
	public List<Expense> getExpensesInRange(String startDateString, String endDateString) throws ParseException
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = df.parse(startDateString);
		Date endDate = df.parse(endDateString);
		Filter startDateFilter = new FilterPredicate("date",FilterOperator.GREATER_THAN_OR_EQUAL,startDate);
		Filter endDateFilter = new FilterPredicate("date",FilterOperator.LESS_THAN_OR_EQUAL,endDate);
		CompositeFilter dateRangeFilter = CompositeFilterOperator.and(startDateFilter,endDateFilter);
		Query query = new Query("expense").setFilter(dateRangeFilter);
		PreparedQuery pq = datastore.prepare(query);
		Iterable<Entity> iter = pq.asIterable(FetchOptions.Builder.withLimit(10));
		
		Expense expense;
		ArrayList<Expense> results = new ArrayList<>();
		for(Entity e : iter)
		{
			Long id = e.getKey().getId();
			Long userId = Long.parseLong(e.getProperty("userId").toString());
			String title = e.getProperty("title").toString();
			Float amount = Float.parseFloat(e.getProperty("amount").toString());
			String category = e.getProperty("category").toString();
			Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").parse(e.getProperty("date").toString()); 
			String notes = e.getProperty("notes").toString();
			expense = new Expense(id,userId,title,amount,category,date,notes);
			results.add(expense);
		}
		return results;
	}
	
	public boolean checkExpenseId(Long id)
	{
		if(allKeys.size()==0)
		{

			Query query = new Query("expense").setKeysOnly();
			PreparedQuery pq = datastore.prepare(query);
			Iterable<Entity> iter = pq.asIterable();
			for(Entity e : iter)
			{
				allKeys.add(e.getKey().getId());
			}
			
		}
		if(!allKeys.contains(id))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Expense expenseExists(Expense expense) throws ParseException
	{
		Long userId = expense.getUserId();
		String title = expense.getTitle();
		Float amount = expense.getAmount();
		String category = expense.getCategory();
		Date date = expense.getDate();
		String notes = expense.getNotes();
		
		Filter userIdFilter = new FilterPredicate("userId",FilterOperator.EQUAL,userId);
		Filter titleFilter = new FilterPredicate("title",FilterOperator.EQUAL,title);
		Filter amountFilter = new FilterPredicate("amount",FilterOperator.EQUAL,amount);
		Filter categoryFilter = new FilterPredicate("category",FilterOperator.EQUAL,category);
		Filter dateFilter = new FilterPredicate("date",FilterOperator.EQUAL,date);
		Filter notesFilter = new FilterPredicate("notes",FilterOperator.EQUAL, notes);
		CompositeFilter filter = CompositeFilterOperator.and(userIdFilter, titleFilter, amountFilter, categoryFilter, dateFilter, notesFilter);
		Query query = new Query("user").setFilter(filter);
		PreparedQuery pq = datastore.prepare(query);
		int entityCount = pq.countEntities();
		if(entityCount!=0)
		{
			Entity e = pq.asSingleEntity();
			Long id = e.getKey().getId();
			userId = Long.parseLong(e.getProperty("userId").toString());
			title = e.getProperty("title").toString();
			amount = Float.parseFloat(e.getProperty("amount").toString());
			category = e.getProperty("category").toString();
			date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").parse(e.getProperty("date").toString()); 
			notes = e.getProperty("notes").toString();
			return new Expense(id,userId,title,amount,category,date,notes);
		}
		return null;
	}



	
	
	

}
