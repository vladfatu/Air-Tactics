package com.users;

public class User {
	
	private static User instance;
	private String username, password, name, email;
	private Boolean loggedIn;

	public User()
	{
		if (instance == null)
			instance = this;
	}
	
	public void setUser(String pUsername, String pPassword)
	{
			username = pUsername;
			password = pPassword;
			email = username + "@tacticsgames.tk";
			loggedIn = false;
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static User getInstance()
	{
		return instance;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public Boolean getLoggedIn() {
		return loggedIn;
	}

}
