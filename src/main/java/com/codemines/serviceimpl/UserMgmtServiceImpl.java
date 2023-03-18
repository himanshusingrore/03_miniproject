package com.codemines.serviceimpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.codemines.bindings.ActivateAccount;
import com.codemines.bindings.Login;
import com.codemines.bindings.User;
import com.codemines.emailutils.EmailUtils;
import com.codemines.entites.UserMaster;
import com.codemines.repo.UserMasterRepo;
import com.codemines.services.UserMgmtService;

@Service
public class UserMgmtServiceImpl implements UserMgmtService {

	@Autowired
	private UserMasterRepo userMasterRepo;
	
	@Autowired
	private EmailUtils emailUtils;
	

	// random text generater
	private String randomPassword() {
		String pwd = "";
		// create a string of uppercase and lowercase characters and numbers
		String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";

		// combine all strings
		String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

		// create random string builder
		StringBuilder sb = new StringBuilder();

		// create an object of Random class
		Random random = new Random();

		// specify length of random string
		int length = 6;

		for (int i = 0; i < length; i++) {

			// generate random index number
			int index = random.nextInt(alphaNumeric.length());

			// get character specified by index
			// from the string
			char randomChar = alphaNumeric.charAt(index);

			// append the character to string builder
			sb.append(randomChar);
		}
		pwd = sb.toString();
		return pwd;
	}
	
	
	//read regestration email body
	private String readEmailBody(String fullname,String pwd,String filename)
	{
		String url="";
		String mailBody="";
		try {
			FileReader fr=new FileReader(filename);//reader read character by character and create character streams 
			BufferedReader br=new BufferedReader(fr);//buffer reader read character stream and next line also 
			
			StringBuffer buffer=new StringBuffer();
			
			String line =br.readLine();//reads 1st line 
			while(line!=null)
			{
				buffer.append(line);
				line=br.readLine();//after reading 1st line inside loop again reading next line
			}
			br.close();
			mailBody = buffer.toString();
			mailBody=mailBody.replace("{fullname}", fullname);
			mailBody=mailBody.replace("{temppwd}", pwd);
			mailBody=mailBody.replace("{url}", url);
			mailBody=mailBody.replace("{recpwd}", pwd);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
return mailBody;		
	}

	@Override
	public boolean saveUser(User user) {
		// jpa repo method take entity object na ki binding object so jo user object hai
		// vo binding object hai
		// usko entity object mai copy krdenge fir save method ko denge

		UserMaster userentity = new UserMaster();
		BeanUtils.copyProperties(user, userentity);
		// our requrmnt is ki user save krte time ek random password bhi save ho jaaye
		// and after registeration
		// ye random password user k email pr jaaye jisse vo baad mai account activate
		// kr ske ..so we need to write code for random password
		// genration jo ki net se miljyega

		String password = randomPassword();// random password generateor
		userentity.setPassword(password);
        userentity.setAccStatus("In-Active");//abhi in active rhega jb vo real password daaldega tb active kehlaayega
		
        
        String subject="your registration succ !!";
        //sending temppassword in mail
        String filename="regMailBody.txt";
        String body=readEmailBody(user.getFullname(), userentity.getPassword(),filename);
        emailUtils.sendEmail(user.getEmail(), subject, body);
        
        
		UserMaster usersaved = userMasterRepo.save(userentity);
		if (usersaved.getUserID() != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean activateAccount(ActivateAccount accActivateAccount) {
		//user k new password daalte time means account check kre ki active hai ki in active 
		//usse pehle check krenge ki email and temp paasword jo user ne dala hai vo sahi hai ki nahi
		//so we db se check krlenge becoz user save krte time hi apan ne temp password save krvaliya tha
		UserMaster entity = new UserMaster();
		entity.setEmail(accActivateAccount.getEmail());
		entity.setPassword(accActivateAccount.getTempPwd());

		Example<UserMaster> example = Example.of(entity);
		List<UserMaster> user = userMasterRepo.findAll(example);
		
		if(user.isEmpty())
		{
			return false;
		}
		else
		{
			UserMaster userMaster = user.get(0);//list maid eke hi user to aayga na 
			userMaster.setPassword(accActivateAccount.getNewPwd());
			userMaster.setAccStatus("Active");
			userMasterRepo.save(userMaster);
			return true;
		}
		
	}

	@Override
	public User getUserById(Integer userID) {
Optional<UserMaster> optionalcontainer = userMasterRepo.findById(userID);
User user=new User();
if(optionalcontainer.isPresent())
{
	UserMaster userMaster=optionalcontainer.get();//getting usemaster object from optional container
	BeanUtils.copyProperties(userMaster, user);
}
		
		return user;
	}
	
//is method ka use apan ne nahi kiya b soch rhe the 
	@Override
	public User getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAllUsers() {
		List<UserMaster> allusers = userMasterRepo.findAll();
		List<User> userlist=new ArrayList<>();
		Iterator<UserMaster> iterator = allusers.iterator();
		while(iterator.hasNext())
		{
			UserMaster userentity=iterator.next();
			User user=new User();
			BeanUtils.copyProperties(userentity, user);
			userlist.add(user);
		}
		
		
return userlist;
		
	}

	@Override
	public boolean deleteUser(Integer userID) {
		
		try
		{
			userMasterRepo.deleteById(userID);
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean changeAccountstatus(Integer userID, String accStatus) {
Optional<UserMaster> optcontainer = userMasterRepo.findById(userID);
if(optcontainer.isPresent())
{
	UserMaster userMaster = optcontainer.get();
	userMaster.setAccStatus(accStatus);
	return true;
}
		
		return false;
	}

	@Override
	public String login(Login login) {
String msg="";
		//first we need to check user is laredy exist ki ni
		UserMaster userMaster=new UserMaster();
		userMaster.setEmail(login.getEmail());
		userMaster.setPassword(login.getPassword());
		Example<UserMaster> example = Example.of(userMaster);
		List<UserMaster> exisitinguser = userMasterRepo.findAll(example);
		if(exisitinguser.isEmpty())
		{
			msg="Invalid Credentials !!";
			return msg;
		}else
		{
			UserMaster user = exisitinguser.get(0);
			if(user.getAccStatus().equalsIgnoreCase("Active"))
			{
				msg="Login in Succesfully !!";
				return msg;
			}else
			{
				msg="Account Not Activated";
				return msg;
			}
		}
		
	}

	@Override
	public String forgotPwd(String email) {
		UserMaster user = userMasterRepo.findByEmail(email);
		if(user==null)
		{
			return "Invalid Email !!";
		}
		//send pwd to user email id
		String subject="recover password mail";
	    String filename="recoverpwd.txt";
        String body=readEmailBody(user.getFullname(), user.getPassword(),filename);
        boolean sendEmail = emailUtils.sendEmail(user.getEmail(), subject, body);
        if(sendEmail)
        {
        	return "password send to your register email";
        }
		return null;
	}

}// end
