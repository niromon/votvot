package org.deguet.service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

public class ServiceEmail {

	public void sendEmail(String email,String content) throws MessagingException{
		// A remplacer par votre login Gmail pour l'envoi de courriel
		final String gmailLogin = "deguetdupont@gmail.com";
		// A remplacer par le mot de passe du compte Gmail
		final String gmailPass  = "isaacdeguet";
		// A remplacer par votre adresse courriel
		final String destination = email;

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(gmailLogin,gmailPass);
			}
		});	
		Address sender = new InternetAddress(gmailLogin);
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(sender);
		msg.setRecipients(Message.RecipientType.TO,destination);
		msg.setSubject("Test de courriel de mon application ");
		msg.setSentDate(new Date());
		msg.setText(content);
		Transport.send(msg);
		System.out.println("Message should be sent " +msg);
	}

	//https://code.google.com/p/google-mail-oauth2-tools/source/browse/trunk/java/com/google/code/samples/oauth2/OAuth2Authenticator.java

	//	public static SMTPTransport connectToSmtp(String host,
	//			int port,
	//			String userEmail,
	//			String oauthToken,
	//			boolean debug) throws Exception {
	//		Properties props = new Properties();
	//		props.put("mail.smtp.starttls.enable", "true");
	//		props.put("mail.smtp.starttls.required", "true");
	//		props.put("mail.smtp.sasl.enable", "true");
	//		props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
	//		props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
	//		Session session = Session.getInstance(props);
	//		session.setDebug(debug);
	//
	//		final URLName unusedUrlName = null;
	//		SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
	//		// If the password is non-null, SMTP tries to do AUTH LOGIN.
	//		final String emptyPassword = "";
	//		transport.connect(host, port, userEmail, emptyPassword);
	//		//transport.sendMessage(arg0, arg1)
	//		return transport;
	//	}

}
