package com.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.entities.Expense;

public class ValidationUtils 
{
	private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private static Validator validator = factory.getValidator();
	
	 public static void validate(Expense expense) throws ConstraintViolationException
	 {
		 Set<ConstraintViolation<Expense>> violations = validator.validate(expense);
		 if (!violations.isEmpty()) {
	            throw new ConstraintViolationException(violations);
	        }
	 }

}
