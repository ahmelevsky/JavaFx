package idlast;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender {
	
	public static boolean createEmailAndSend(List<String> text ){
		StringBuffer body = new StringBuffer("<html>Напоминание от IDLast Remote:<br>");
	      body.append("<br>");
		  for (String s:text) body.append(s+"<br>");
		  body.append("</html>");
      try {
    	  sendEmail(body.toString());
    	  return true;
      } catch (MessagingException | GeneralSecurityException ex) {
         return false;
      }
	}
		
	private static void sendEmail(String htmlBody) throws MessagingException, GeneralSecurityException{
		           
		
		final String username = "idlastreminder@gmail.com";
		final String password = "idlast123";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("IDLast Reminder Remote <idlastreminder@gmail.com>"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("glaesarius@mail.ru"));
			message.setSubject("Напоминание!");
			message.setHeader("Content-Type", "text/plain; charset=UTF-8"); 
			
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(htmlBody, "text/html; charset=\"UTF-8\"");

			
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			
			Transport.send(message);
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}