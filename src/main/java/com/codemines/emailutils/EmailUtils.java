package com.codemines.emailutils;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {

	@Autowired
	private JavaMailSender javaMailSender;
	
	public boolean sendEmail(String to,String subject,String body) {
		boolean isMailSent=false;
		try {
			
			MimeMessage mimeMsg = javaMailSender.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(mimeMsg);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);//2nd parameter is true means our body will contain some html code ..
		    javaMailSender.send(mimeMsg);
		    isMailSent=true;
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return isMailSent;
	}
	
	
}
