package com.services;

import java.util.List;

import com.entities.User;

public interface UserService {
	
	public User addUser(User user);
	public List<User> getUsers();
	public User userExists(User user);
}
