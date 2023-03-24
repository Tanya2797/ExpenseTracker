package com.controller.userExpenseCal;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;


@WebServlet("/customRangeExpenseServlet")
public class CustomRangeExpenseServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		PrintWriter out = res.getWriter();
		try {
			
		String startDateString = req.getParameter("startdate");
		String endDateString = req.getParameter("enddate");
		
		
		Date todayDate = new Date();
		String todaydateString = new SimpleDateFormat("yyyy-MM-dd").format(todayDate);
		todayDate = new SimpleDateFormat("yyyy-MM-dd").parse(todaydateString);
		
		Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateString);
		Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateString);
		
		if(startDate.compareTo(endDate)>0)
		{
			out.println("Please provide a valid range! Start date should be less than end date");
			return;
		}
		if(startDate.compareTo(todayDate)>0||endDate.compareTo(todayDate)>0)
		{
			out.println("Please Provide valid date inputs! Do not provide future dates!");
			return;
		}
		
		Iterable<Entity> list = UserExpenseOperations.customRangeExpense(startDateString, endDateString);
		
		if(list==null)
		{
			out.println("No expenses made in this time period");
			return;
		}
		
		float sum = 0;
		for(Entity e : list)
		{
			String amountString = e.getProperty("amount").toString();
			sum = sum + Float.parseFloat(amountString);
		}
		if(sum!=0)
		{
			out.println("Total expense for the range:"+sum);
		}
		else
	    {
	    	out.print("No expenses were made in this time period!");
	    }
		}catch(ParseException e)
		{
			out.println("Error occured while parsing the date!");
		}
	}
	
	
}
