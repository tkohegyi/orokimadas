package website.magyar.adoration.helper;

import com.sun.mail.smtp.SMTPTransport; //NOSONAR
import website.magyar.adoration.configuration.EmailConfigurationAccess;
import website.magyar.adoration.configuration.PropertyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * EmailSender is the Component that is used to send mails to administrator
 * or to a user logged in with his/her social account for the first time.
 */
@Component
public class EmailSender {
    private final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private EmailConfigurationAccess emailConfigurationAccess;

    private void sendProperMail(final String subject, final String text, final String to, final String cc, final String bcc, final String typeText) {
        PropertyDto propertyDto = emailConfigurationAccess.getProperties();
        Properties prop = new Properties();
        prop.put("mail.smtp.starttls.enable","true");
        prop.put("mail.smtp.ssl.trust", "*");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        prop.put("mail.smtp.host", propertyDto.getSmtpServer()); //optional, defined in SMTPTransport
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", propertyDto.getSmtpPort()); // default port 25

        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);

        String response = "failed";
        try {
            // from
            msg.setFrom(new InternetAddress(propertyDto.getEmailFrom()));
            // to
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            // cc
            if (cc != null) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
            }
            // bcc
            if (bcc != null) {
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
            }
            // subject
            msg.setSubject(subject);
            // content
            msg.setContent(text, "text/plain; charset=UTF-8");
            msg.setSentDate(new Date());
            // Get SMTPTransport
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            // connect
            t.connect(propertyDto.getSmtpServer(), propertyDto.getSmtpUserName(), propertyDto.getSmtpPassword());
            // send
            t.sendMessage(msg, msg.getAllRecipients());
            response = t.getLastServerResponse();
            logger.info("Send Email: {}, Response: {}", typeText, response);
            t.close();
        } catch (MessagingException e) {
            logger.warn("Send Email: {} FAILED, Response: {}", typeText, response, e);
        }
    }

    /**
     * Send an e-mail to the Administrator.
     *
     * @param subject is the subject of the mail
     * @param text    is the body of the mail - which is a plain text mail
     */
    public void sendMailToAdministrator(final String subject, final String text) {
        PropertyDto propertyDto = emailConfigurationAccess.getProperties();
        sendProperMail(subject, text, propertyDto.getEmailTo(), "", "","to Administrator");
    }

    /**
     * Send a mail to a person + to Administrator in BCC.
     *
     * @param providedEmail is the email of the adorator (or any other user)
     * @param subject       is the subject of the mail
     * @param text          is the body of the mail - which is a plain text mail
     */
    public void sendMailFromSocialLogin(String providedEmail, String subject, String text) {
        PropertyDto propertyDto = emailConfigurationAccess.getProperties();
        sendProperMail(subject, text, providedEmail, "", propertyDto.getEmailTo(), "to person logged-in first time");
    }
}
