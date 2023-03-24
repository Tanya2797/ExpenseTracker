package com.services;

import java.util.ArrayList;
import java.util.List;

import com.entities.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class UserServiceImp implements UserService{
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public User addUser(User user)
	{
		Entity entity = new Entity("user");
		entity.setProperty("name", user.getName());
		
		 Key key;
	     key = datastore.put(entity);
	     
	     if(key.isComplete())
	     {
	    	 user.setId(key.getId());
	    	 return user;
	     }
	     return null;
	}
	
	public List<User> getUsers()
	{
		Query query = new Query("user");
		PreparedQuery pq = datastore.prepare(query);
		Iterable<Entity> iter = pq.asIterable(FetchOptions.Builder.withLimit(10));
		User user;
		List<User> results = new ArrayList<>();
		for(Entity e : iter)
		{
			Long id = e.getKey().getId();
			String name = e.getProperty("name").toString();
			user = new User(id,name);
			results.add(user);
		}
		return results;
	}
	
	public User userExists(User user)
	{
		String userName = user.getName();
		Filter filter = new FilterPredicate("name",FilterOperator.EQUAL,userName);
		Query query = new Query("user").setFilter(filter);
		PreparedQuery pq = datastore.prepare(query);
		int entityCount = pq.countEntities();
		if(entityCount!=0)
		{
			Entity entity = pq.asSingleEntity();
			Long id = entity.getKey().getId();
			String name = entity.getProperty("name").toString();
			User userExisting = new User(id,name);
			return userExisting;
		}
		return null;
	}

}
