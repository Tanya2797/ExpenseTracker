//package com.controller.userExpenseCal;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.text.ParseException;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.google.appengine.api.datastore.Entity;
//
//
//
//@WebServlet("/weekly")
//public class weeklyExpenseServlet extends HttpServlet {
//	
//	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
//	{
//		PrintWriter out = res.getWriter();
//		
//		try {
//			Iterable<Entity> list = UserExpenseOperations.calWeeklyExpense();
//			
//			if(list==null)
//			{
//				out.println("No expenses made in this week");
//				return;
//			}
//			
//			float sum = 0;
//			for(Entity e : list)
//			{
//				String amountString = e.getProperty("amount").toString();
//				sum = sum + Float.parseFloat(amountString);
//			}
//			if(sum!=0)
//			{
//				out.println("Total expense for this week: "+sum);
//			}
//			else
//		    {
//		    	out.print("No expenses were made in this week");
//		    }
//			
//			
//		} catch (ParseException e) {
//			
//			e.printStackTrace();
//	
//		}
//		
//	}
//	
//
//}
