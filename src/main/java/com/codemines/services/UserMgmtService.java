package com.codemines.services;

import java.util.List;

import com.codemines.bindings.ActivateAccount;
import com.codemines.bindings.Login;
import com.codemines.bindings.User;

public interface UserMgmtService {
	//when user register
	public boolean saveUser(User user);
	
	//when user receive temp pwd in email and again register with that pwd mean activating acount
	public boolean activateAccount(ActivateAccount accActivateAccount);

	//when user want to edit then first user need that particular user data tb hi toh usme changes krega
	public User getUserById(Integer userID);
	
	//to check wether user email is already present or not
	public User getUserByEmail(String email);
	
	//to get all list of users
	public List<User> getAllUsers();
	
	//to delete user
	public boolean deleteUser(Integer userID);
	
	//for soft delete like active or de active
	public boolean changeAccountstatus(Integer userID,String accStatus);
	
	//when user do login here we returning string instead of boolean becoz if login fail hua toh kis wajah se hua 
	//like credentials wrong hai ki kuch or wo msg apan return krvaadenge
	public String login(Login login);
	
	//for forgot password 
	public String forgotPwd(String email);
	
}
