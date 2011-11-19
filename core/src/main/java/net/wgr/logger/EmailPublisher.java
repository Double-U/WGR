/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.logger;

import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import net.wgr.settings.Settings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * 
 * @created Sep 11, 2011
 * @author double-u
 */
public class EmailPublisher extends LogPublisher {

    protected final PatternLayout pl = new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN);

    @Override
    public void publish(List<LoggingEvent> records, Period p) {
        String timeRange = DateTime.now().minus(p).toString() + " - " + DateTime.now().toString();
        String log = "";

        for (LoggingEvent le : records) {
            log += pl.format(le) + "\n";
            if (le.getThrowableInformation() != null) {
                log += le.getThrowableInformation().getThrowable().getMessage() + "\n";
                StackTraceElement[] elements = le.getThrowableInformation().getThrowable().getStackTrace();
                for (StackTraceElement ste : elements) {
                    if (ste.getClassName().startsWith("net.wgr") || ste.getClassName().startsWith("net.secretpanda")) {
                        log += "\t" + ste.toString() + "\n";
                    }
                }
            }

        }
        sendMail(log, timeRange);
    }

    protected void sendMail(String log, String period) {
        try {
            Settings s = Settings.getInstance();
            String host = s.getString("Integration.Mail.Server");
            String from = "backend@secretpanda.net";
            System.setProperty("javax.net.ssl.keyStore", s.getString("SSLKeystore.Path"));
            System.setProperty("javax.net.ssl.keyStorePassword", s.getString("SSLKeystore.Password"));
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "26");
            props.put("mail.smtp.auth", "false");

            String[] to = {s.getString("Logging.Mail.Recipient")};

            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
            message.setSubject("Log output for " + period);
            message.setText(log);
            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException ex) {
            Logger.getLogger(getClass()).log(Level.ERROR, "Sending log mail failed", ex);
            ex.printStackTrace();
        }
    }
}
