package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.entities.Expense;
import com.entities.User;
import com.errors.ApiError;
import com.errors.ValidationError;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.services.ExpenseService;
import com.services.ExpenseServiceImp;
import com.utils.ValidationUtils;

// api/v1/expense
@WebServlet("expenses/*")
public class ExpenseController extends HttpServlet {
	
	private ExpenseService expenseService = new ExpenseServiceImp();
	
    
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		
		PrintWriter out = response.getWriter();
		String contentType = request.getContentType();
		ObjectMapper mapper = new ObjectMapper();
		if(!contentType.equals("application/json"))
		{
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ApiError error = new ApiError(new Date(),"Please provide data in the correct format",request.getRequestURI());
			mapper.writeValue(response.getWriter(), error);
			return;
		}
		
		
	    if(request.getPathInfo()==null)
		{
			try {
			    
			    Expense expense = mapper.readValue(request.getInputStream(), Expense.class);

			    // Validating all the parameters received by the user
			    ValidationUtils.validate(expense);

			    //Checking if this entry already exists
			    Expense existingExpense = expenseService.expenseExists(expense);
			    if(existingExpense!=null)
			    {
			    	response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					mapper.writeValue(response.getWriter(), existingExpense);
					return;
			    }
			    
			    // Calling addExpense method to save user expense details
			    Expense expenseAdded = expenseService.addExpense(expense);
			    if(expenseAdded!=null)
			    {
			    	response.setContentType("application/json");
			    	response.setStatus(HttpServletResponse.SC_CREATED);
			    	mapper.writeValue(response.getWriter(), expenseAdded);
			    }


			} catch (ConstraintViolationException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ArrayList<String> errorMessages = new ArrayList<>();
				// List<String> errors = e.getBindingResult().getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).collect(Collectors.toList());
				for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			        errorMessages.add(violation.getPropertyPath()+":"+violation.getMessage());
			     
			    }
				ValidationError error = new ValidationError(new Date(),"Invalid Input! Validation Failed!", errorMessages);
				mapper.writeValue(response.getWriter(), error);

			} catch(ParseException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ApiError error = new ApiError(new Date(),"Please provide date in the correct format",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
			}
		}
		
		
		 
	
	}
	
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		ObjectMapper mapper = new ObjectMapper();
		if(request.getPathInfo()!=null)
		{
			String idString = request.getPathInfo().substring(1);
			if(idString.length()>16)
			{
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ApiError error = new ApiError(new Date(),"Invalid Path",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
				
			}
			else
			{
				try 
				{
					long id = Long.parseLong(idString);
					boolean userDeleted = expenseService.deleteExpense(id);
					if(userDeleted == true)
					{
						response.setStatus(HttpServletResponse.SC_NO_CONTENT);
						out.println("Expense Deleted Successfully!");
					}
					else
					{
						response.setContentType("application/json");
						response.setStatus(HttpServletResponse.SC_NOT_FOUND);
						ApiError error = new ApiError(new Date(),"No such entry exists in the database",request.getRequestURI());
						mapper.writeValue(response.getWriter(), error);
					}
	
					
				}catch(NumberFormatException e)
				{
					response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					ApiError error = new ApiError(new Date(),"Please provide the numeric ID of the expense",request.getRequestURI());
					mapper.writeValue(response.getWriter(), error);
					
				}
			}
		}
		else
		{
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ApiError error = new ApiError(new Date(),"Please provide the expense Id",request.getRequestURI());
			mapper.writeValue(response.getWriter(), error);
		}
		
		

		
	}
	
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String pathInfo = request.getPathInfo();
		ObjectMapper mapper = new ObjectMapper();
		
		if(pathInfo==null)
		{
			PrintWriter out = response.getWriter();
			String contentType = request.getContentType();
			if(!contentType.equals("application/json"))
			{
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ApiError error = new ApiError(new Date(),"Please provide data in the correct format",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
				return;
			}
			try {
			    

			    // To ignore unknown parameters in the request body
			    //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			    Expense expense = mapper.readValue(request.getReader(), Expense.class);

			    // Validating all the parameters received by the user
			    ValidationUtils.validate(expense);
			    
			    Expense expenseUpdated = expenseService.updateExpense(expense);

			    if(expenseUpdated!=null)
			    {
			    	response.setContentType("application/json");
			    	response.setStatus(HttpServletResponse.SC_OK);
			    	mapper.writeValue(response.getWriter(), expenseUpdated);
			    }
			    else
			    {
			    	response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					ApiError error = new ApiError(new Date(),"No such entry exists in the database",request.getRequestURI());
					mapper.writeValue(response.getWriter(), error);
			    }

			} catch (ConstraintViolationException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ArrayList<String> errorMessages = new ArrayList<>();
				for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			        errorMessages.add(violation.getMessage());
			    }
				ValidationError error = new ValidationError(new Date(),"Invalid Input! Validation Failed!", errorMessages);
				mapper.writeValue(response.getWriter(), error);

			} 
			
		}
		else
		{
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ApiError error = new ApiError(new Date(),"Please provide the correct URL",request.getRequestURI());
			mapper.writeValue(response.getWriter(), error);
		}
		
	
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		
		String pathInfo = request.getPathInfo();
		
		if (pathInfo != null && pathInfo.startsWith("/")) {
			//get by expenseID
	        String expenseId = pathInfo.substring(1); // Get the expense ID from the path
	        Expense expense = null;
			try {
				expense = expenseService.getExpenseById(expenseId);
				
				// Serialize the expense to JSON and send it as the response
		        response.setContentType("application/json");
		        response.setStatus(HttpServletResponse.SC_OK);
		        mapper.writeValue(response.getWriter(), expense);
			} catch (NumberFormatException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ApiError error = new ApiError(new Date(),"Please provide the numeric ID of the expense",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
				e.printStackTrace();
			} catch (ParseException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ApiError error = new ApiError(new Date(),"Please provide date in the correct format",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
			} catch (EntityNotFoundException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				ApiError error = new ApiError(new Date(),"No such entry exists in the database",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
			}

	        // Check if the expense exists
	        if (expense == null) {
	        	response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				ApiError error = new ApiError(new Date(),"No such entry exists in the database",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
	            return;
	        }

	       
	    } 
		else if(pathInfo == null && request.getParameter("startDate") != null && request.getParameter("endDate") != null)
		{
			//Get by date custom range
			
			String startDate = request.getParameter("startDate");
	        String endDate = request.getParameter("endDate");
	        List<Expense> expensesInRange;
			try {
				expensesInRange = expenseService.getExpensesInRange(startDate, endDate);
				
				// Serialize the expenses in the date range to JSON and send it as the response
		        response.setContentType("application/json");
		        response.setStatus(HttpServletResponse.SC_OK);
		        mapper.writeValue(response.getWriter(), expensesInRange);
			} catch (ParseException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ApiError error = new ApiError(new Date(),"Please provide date in the correct format",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
				
			}

	        
		}
		else if(pathInfo == null && request.getParameter("userId") != null)
		{
			//Get by userId
			
			String userId = request.getParameter("userId");
			List<Expense> userExpenses;
			try {
				userExpenses = expenseService.getAllExpensesForUser(userId);
				
				 response.setContentType("application/json");
			     response.setStatus(HttpServletResponse.SC_OK);
			     mapper.writeValue(response.getWriter(), userExpenses);
			} catch (ParseException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ApiError error = new ApiError(new Date(),"Please provide date in the correct format",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
			} catch(NumberFormatException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ApiError error = new ApiError(new Date(),"Please provid numeric value for user Id",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
			}
		
		}
		else if(pathInfo == null)
		{
			//get all expenses
			
			List<Expense> allExpenses;
			try {
				allExpenses = expenseService.getAllExpenses();
				 response.setContentType("application/json");
			     response.setStatus(HttpServletResponse.SC_OK);
			     mapper.writeValue(response.getWriter(), allExpenses);
			} catch (ParseException e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ApiError error = new ApiError(new Date(),"Please provide date in the correct format",request.getRequestURI());
				mapper.writeValue(response.getWriter(), error);
			}
			
			
	     }
		else
		{
			//Invalid URL
			
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ApiError error = new ApiError(new Date(),"Please provide the correct URL",request.getRequestURI());
			mapper.writeValue(response.getWriter(), error);
		}
		
		
		
	}
	
	private void writeErrorResponse(HttpServletResponse response, int statusCode, String errorMessage, String requestURI) throws IOException {
	    response.setContentType("application/json");
	    response.setStatus(statusCode);
	    ApiError error = new ApiError(new Date(), errorMessage, requestURI);
	    new ObjectMapper().writeValue(response.getWriter(), error);
	}
}
