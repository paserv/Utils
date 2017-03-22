package it.osg.utils;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MailUtils {
	
	public static String SENDER_MAIL = "donpablooooo@gmail.com";
	public static String SENDER_NAME = "DONPABLOWATCH";
	
	public static void sendMail(String mail, String subject, String bodyMail, String attachFileName, String attachFile) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress(SENDER_MAIL, SENDER_NAME));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail));
			msg.setSubject(subject);
			//msg.setText(bodyMail);

			Multipart mp = new MimeMultipart();
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(bodyMail, "text/plain");
			mp.addBodyPart(htmlPart);

			MimeBodyPart attachment = new MimeBodyPart();
			ByteArrayDataSource src = new ByteArrayDataSource(attachFile.getBytes(), "text/plain"); 
			attachment.setFileName(attachFileName);
			attachment.setDataHandler(new DataHandler (src));
			mp.addBodyPart(attachment);

			msg.setContent(mp);
			msg.saveChanges();
			Transport.send(msg);

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	

	}
	
	
} 
