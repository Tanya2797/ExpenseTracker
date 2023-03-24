package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.entities.Expense;
import com.entities.User;
import com.errors.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.UserService;
import com.services.UserServiceImp;
import com.utils.ValidationUtils;

@WebServlet("users")
public class UserController extends HttpServlet {
	
	private UserService userService = new UserServiceImp();
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		PrintWriter out = response.getWriter();
		String contentType = request.getContentType();
		if(!contentType.equals("application/json"))
		{
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ApiError error = new ApiError(new Date(),"Please provide data in the correct format",request.getRequestURI());
			objectMapper.writeValue(response.getWriter(), error);
			return;
		}
		
	    User user = objectMapper.readValue(request.getInputStream(), User.class);
	    
	    User existingUser = userService.userExists(user);
	    if(existingUser!=null)
	    {
	    	response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			objectMapper.writeValue(response.getWriter(), existingUser);
			return;
	    }
	    
	    User userAdded = userService.addUser(user);
	    if(userAdded!=null)
	    {
	    	response.setContentType("application/json");
	    	response.setStatus(HttpServletResponse.SC_CREATED);
	    	objectMapper.writeValue(response.getWriter(), userAdded);
	    }
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		List<User> allUsers = userService.getUsers();
		if(allUsers.size()==0)
		{
			out.println("No users exist in the database");
		}
		response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), allUsers);
	}

}
