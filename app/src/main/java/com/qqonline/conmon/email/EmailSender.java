package com.qqonline.conmon.email;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender {
	
	private final static String EMAILHOST="smtp.163.com";
	private final static String EMAILPORT="25";
	private final static String EMAILUSERNAME="MPF_Error@163.com";
	private final static String EMAILPASSWORD="topiserv";
	private final static String EMAILSENDFROM="MPF_Error@163.com";
	private final static String EMAILSENDTO="MPF_Error@163.com";
	/**
	 * ���ʼ��������������ʼ���������
	 */
	/**
	 * ���ı���ʽ�����ʼ�
	 * 
	 * @param mailInfo
	 *            �����͵��ʼ�����Ϣ
	 */
	public boolean sendTextMail(MailSenderInfo mailInfo) {
		// �ж��Ƿ���Ҫ������֤
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// �����Ҫ������֤���򴴽�һ��������֤��
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// �����ʼ��Ự���Ժ�������֤������һ�������ʼ���session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// ����session����һ���ʼ���Ϣ
			Message mailMessage = new MimeMessage(sendMailSession);
			// �����ʼ������ߵ�ַ
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// �����ʼ���Ϣ�ķ�����
			mailMessage.setFrom(from);
			// �����ʼ��Ľ����ߵ�ַ�������õ��ʼ���Ϣ��
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// �����ʼ���Ϣ������
			mailMessage.setSubject(mailInfo.getSubject());
			// �����ʼ���Ϣ���͵�ʱ��
			mailMessage.setSentDate(new Date());
			// �����ʼ���Ϣ����Ҫ����
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// �����ʼ�
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * ��HTML��ʽ�����ʼ�
	 * 
	 * @param mailInfo
	 *            �����͵��ʼ���Ϣ
	 */
//	public boolean sendHtmlMail(MailSenderInfo mailInfo) {
//		// �ж��Ƿ���Ҫ������֤
//		MyAuthenticator authenticator = null;
//		Properties pro = mailInfo.getProperties();
//		// �����Ҫ������֤���򴴽�һ��������֤��
//		if (mailInfo.isValidate()) {
//			authenticator = new MyAuthenticator(mailInfo.getUserName(),
//					mailInfo.getPassword());
//		}
//		// �����ʼ��Ự���Ժ�������֤������һ�������ʼ���session
//		Session sendMailSession = Session
//				.getDefaultInstance(pro, authenticator);
//		try {
//			// ����session����һ���ʼ���Ϣ
//			Message mailMessage = new MimeMessage(sendMailSession);
//			// �����ʼ������ߵ�ַ
//			Address from = new InternetAddress(mailInfo.getFromAddress());
//			// �����ʼ���Ϣ�ķ�����
//			mailMessage.setFrom(from);
//			// �����ʼ��Ľ����ߵ�ַ�������õ��ʼ���Ϣ��
//			Address to = new InternetAddress(mailInfo.getToAddress());
//			// Message.RecipientType.TO���Ա�ʾ�����ߵ�����ΪTO
//			mailMessage.setRecipient(Message.RecipientType.TO, to);
//			// �����ʼ���Ϣ������
//			mailMessage.setSubject(mailInfo.getSubject());
//			// �����ʼ���Ϣ���͵�ʱ��
//			mailMessage.setSentDate(new Date());
//
//			// MiniMultipart����һ�������࣬����MimeBodyPart���͵Ķ���
//			Multipart mainPart = new MimeMultipart();
//			// ����һ������HTML���ݵ�MimeBodyPart
//			BodyPart html = new MimeBodyPart();
//			// ����HTML����
//			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
//			mainPart.addBodyPart(html);
//
//			// �����ż��ĸ���(�ñ����ϵ��ļ���Ϊ����)
//			html = new MimeBodyPart();
//			FileDataSource fds = new FileDataSource("D:\\...javamail.doc");
//			DataHandler dh = new DataHandler(fds);
//			html.setFileName("javamail.doc");
//			html.setDataHandler(dh);
//			mainPart.addBodyPart(html);
//
//			// ��MiniMultipart��������Ϊ�ʼ�����
//			mailMessage.setContent(mainPart);
//			mailMessage.saveChanges();
//
//			// �����ʼ�
//			Transport.send(mailMessage);
//			return true;
//		} catch (MessagingException ex) {
//			ex.printStackTrace();
//		}
//		return false;
//	}
	public void sendTo163(String title,String content)
	{
		// �������Ҫ�������ʼ�  
        MailSenderInfo mailInfo = new MailSenderInfo();  
        mailInfo.setMailServerHost(EMAILHOST);  
        mailInfo.setMailServerPort(EMAILPORT);  
        mailInfo.setValidate(true);  
        mailInfo.setUserName(EMAILUSERNAME); // ʵ�ʷ�����  
        mailInfo.setPassword(EMAILPASSWORD);// ������������  
        mailInfo.setFromAddress(EMAILSENDFROM); // ���÷����������ַ  
        mailInfo.setToAddress(EMAILSENDTO); // ���ý����������ַ  
        mailInfo.setSubject(title);  
        mailInfo.setContent(content);  
        // �������Ҫ�������ʼ�  
        EmailSender sms = new EmailSender();  
        sms.sendTextMail(mailInfo); // ���������ʽ  
//        sms.sendHtmlMail(mailInfo); // ����html��ʽ  
	}
}