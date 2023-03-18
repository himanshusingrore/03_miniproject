package com.codemines.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.codemines.bindings.ActivateAccount;
import com.codemines.bindings.Login;
import com.codemines.bindings.User;
import com.codemines.services.UserMgmtService;

@RestController
public class UserRestController {
	
	@Autowired
	private UserMgmtService userMgmtService;
	
	@PostMapping("/user")
	public ResponseEntity<String> userReg(@RequestBody User user)
	{
		String msg="";
		
		boolean saveUser = userMgmtService.saveUser(user);
		if(saveUser)
		{
			msg="User Saved Succesfully !! ";
			return new ResponseEntity<String>(msg,HttpStatus.OK);
		}else
		{
			msg="Something went wrong ";
			return new ResponseEntity<String>(msg,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/activate")
	public ResponseEntity<String> activateAccount(@RequestBody ActivateAccount activateAccount)
	{
		String msg="";
		boolean activateAccount2 = userMgmtService.activateAccount(activateAccount);
		if(activateAccount2)
		{
			msg="Account Activated Succesfully ";
			return new ResponseEntity<String>(msg,HttpStatus.OK);
		}
		else
		{
			msg="Failed TO activate Account ";
			return new ResponseEntity<String>(msg,HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers()
	{
		List<User> allUsers = userMgmtService.getAllUsers();
			return new ResponseEntity<List<User>>(allUsers,HttpStatus.OK);
	}
	
	//to edit the user we need that user 
	@GetMapping("/user/{userID}")
	public ResponseEntity<User> getAllUserById(@PathVariable Integer userID)
	{
		User user = userMgmtService.getUserById(userID);
		
		return new ResponseEntity<>(user,HttpStatus.OK);
		
	}
	
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<String> deleteUserById(@PathVariable Integer userID)
	{
		String msg="";
		boolean isDeleted = userMgmtService.deleteUser(userID);
		if(isDeleted)
		{
			msg="User Deleted Succesfully !!";
			return new ResponseEntity<String>(msg,HttpStatus.OK);
		}else
		{
			msg="Failed TO delete user!!";
			return new ResponseEntity<String>(msg,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/status/{userId}/{status}")
	public ResponseEntity<String> changeStatus(@PathVariable Integer userID,@PathVariable String status)
	{
		String msg="";
		boolean isStatusChanged = userMgmtService.changeAccountstatus(userID, status);
		if(isStatusChanged)
		{
			msg="Status Changed Succesfully !! ";
			return new ResponseEntity<String>(msg,HttpStatus.OK);
		}else
		{
			msg="Unable To Changed Status ";
			return new ResponseEntity<String>(msg,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Login login)
	{
		String islogin = userMgmtService.login(login);
		if(islogin!=null)
		{
			return new ResponseEntity<String>(islogin,HttpStatus.OK);	
		}else
		{
			return new ResponseEntity<String>(islogin,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/forgot/{emailId}")
	public ResponseEntity<String> forgotPasword(@PathVariable String email)
	{
		String forgotPwd = userMgmtService.forgotPwd(email);
		if(forgotPwd!=null)
		{
			return new ResponseEntity<String>(forgotPwd,HttpStatus.OK);	
		}else
		{
			return new ResponseEntity<String>(forgotPwd,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}//end
