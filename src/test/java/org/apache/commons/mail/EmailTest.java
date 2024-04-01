package org.apache.commons.mail;

import org.junit.jupiter.api.*;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;

class EmailTest {
    private final Email testEmail = new Email() {
        @Override
        public Email setMsg(String msg) {
            return null;
        }
    };


    @Test
    void addCc() throws Exception {
        String testAddress = "ccemail@gmail.com";
        this.testEmail.addCc(testAddress);

        Assertions.assertEquals(testAddress, this.testEmail.getCcAddresses().get(0).getAddress());
    }

    @Test
    void getMailSessionEmpty() throws Exception {

        this.testEmail.setHostName("localhost");

        //test with no properties
        Assertions.assertNotNull(this.testEmail.getMailSession());

    }

    @Test
    void getMailSessionFull() throws Exception {

        this.testEmail.setHostName("localhost");

        //test with properties set
        this.testEmail.setAuthentication("authname", "authpass");
        this.testEmail.setSSLOnConnect(true);
        this.testEmail.setSSLCheckServerIdentity(true);
        this.testEmail.setBounceAddress("bounceemail@gmail.com");
        this.testEmail.setSocketConnectionTimeout(5);
        this.testEmail.setSocketTimeout(2);
        this.testEmail.setStartTLSEnabled(true);
        this.testEmail.setStartTLSRequired(true);
        this.testEmail.setSendPartial(true);

        Assertions.assertNotNull(this.testEmail.getMailSession());
    }

    @Test
    void getMailSessionNullHost(){

        Assertions.assertThrows(EmailException.class, this.testEmail::getMailSession);

    }

    @Test
    void getMailSessionNotNull() throws Exception {

        Properties testProperties = new Properties();

        this.testEmail.setMailSession(Session.getInstance(testProperties));

        Assertions.assertNotNull(this.testEmail.getMailSession());

    }


    @Test
    void setFrom() throws Exception {
        String testAddress = "fromemail@gmail.com";
        String testName = "Set From";

        this.testEmail.setCharset("UTF-16");
        this.testEmail.setFrom(testAddress, testName);

        Assertions.assertEquals(testAddress, this.testEmail.getFromAddress().getAddress());
        Assertions.assertEquals(testName, this.testEmail.getFromAddress().getPersonal());
    }

    @Test
    void addBcc() throws Exception {
        //test valid email list
        String[] testAddresses = {"bcc1email@gmail.com", "bcc2email@gmail.com", "bcc3email@gmail.com"};
        this.testEmail.addBcc(testAddresses);

        Assertions.assertEquals(Arrays.toString(testAddresses), Arrays.toString(this.testEmail.getBccAddresses().toArray()));
    }

    @Test
    void addBccNull(){
        //test with null list
        Assertions.assertThrows(EmailException.class, () -> this.testEmail.addBcc((String[]) null));
    }

    @Test
    void addBccEmpty(){
        //test with empty list
        String[] zeroAddresses = new String[0];
        Assertions.assertThrows(EmailException.class, () -> this.testEmail.addBcc(zeroAddresses));
    }



    @Test
    void addReplyTo() throws Exception {
        String testAddress = "replytoemail@gmail.com";
        String testName = "Reply To";

        this.testEmail.addReplyTo(testAddress, testName);

        Assertions.assertEquals(testAddress, this.testEmail.getReplyToAddresses().get(0).getAddress());
        Assertions.assertEquals(testName, this.testEmail.getReplyToAddresses().get(0).getPersonal());
    }

    @Test
    void addHeader() {
        //test valid options
        String testName = "JTestHeader";
        String testValue = "JTestValue";

        this.testEmail.addHeader(testName, testValue);

        Assertions.assertEquals(testValue, this.testEmail.headers.get(testName));

        //test invalid parameters
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.testEmail.addHeader(null, testValue));
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.testEmail.addHeader(testName, null));
    }

    @Test
    void buildMimeMessageEmpty() throws Exception {
        //required message properties
        this.testEmail.setHostName("localhost");
        this.testEmail.setFrom("testfrom@gmail.com");
        this.testEmail.addTo("testreciever@gmail.com");

        //set test properties
        this.testEmail.setSubject("Mime Message Test");

        this.testEmail.buildMimeMessage();

        Assertions.assertInstanceOf(MimeMessage.class, this.testEmail.getMimeMessage());

        //test building a second message illegally
        Assertions.assertThrows(IllegalStateException.class, this.testEmail::buildMimeMessage);
    }

    @Test
    void buildMimeMessageBody() throws Exception {
        //required message properties
        this.testEmail.setHostName("localhost");
        this.testEmail.setFrom("testfrom@gmail.com");
        this.testEmail.addTo("testreciever@gmail.com");

        this.testEmail.setContent(new MimeMultipart("text/plain"));

        this.testEmail.buildMimeMessage();

        Assertions.assertNotNull(this.testEmail.getMimeMessage());
    }

    @Test
    void buildMimeMessageFull() throws Exception {
        //required message properties
        this.testEmail.setHostName("localhost");
        this.testEmail.setFrom("testfrom@gmail.com");
        this.testEmail.addTo("testreciever@gmail.com");

        //set test properties
        this.testEmail.setCharset("UTF-16");
        this.testEmail.setSubject("Mime Message Test");
        this.testEmail.addCc(new String[] {"cc1email@gmail.com", "cc2email@gmail.com"}); //test multiple cc emails
        this.testEmail.addBcc("bccemail@gmail.com");
        this.testEmail.addReplyTo("replyemail@gmail.com");
        this.testEmail.addHeader("testHeader", "testHeaderValue");
        this.testEmail.setSentDate(new Date());
        this.testEmail.setContent("<b>test html</b>", "text/html");

        this.testEmail.buildMimeMessage();

        Assertions.assertInstanceOf(MimeMessage.class, this.testEmail.getMimeMessage());
    }

    @Test
    void buildMimeMessagePopError() throws Exception {
        //required message properties
        this.testEmail.setHostName("localhost");
        this.testEmail.setFrom("testfrom@gmail.com");
        this.testEmail.addTo("testreciever@gmail.com");

        this.testEmail.setPopBeforeSmtp(true, "pop-host", "pop-user", "pop-pass");

        //should error and convert messaging exception to email exception
        Assertions.assertThrows(EmailException.class, this.testEmail::buildMimeMessage);
    }

    @Test
    void buildMimeMessageNoFrom(){
        this.testEmail.setHostName("localhost");

        //test missing from address
        Assertions.assertThrows(EmailException.class, this.testEmail::buildMimeMessage);
    }

    @Test
    void buildMimeMessageNoReceiver() throws Exception {
        this.testEmail.setHostName("localhost");
        this.testEmail.setFrom("testfrom@gmail.com");

        //test no receiver address
        Assertions.assertThrows(EmailException.class, this.testEmail::buildMimeMessage);
    }

    @Test
    void getSentDate() {
        Date testSentDate = new Date();

        this.testEmail.setSentDate(null); //test null
        this.testEmail.setSentDate(testSentDate);

        Assertions.assertEquals(testSentDate.toString(), this.testEmail.getSentDate().toString());
    }

    @Test
    void getSentDateNull() {

        Assertions.assertInstanceOf(Date.class, this.testEmail.getSentDate());
    }

    @Test
    void getHostName() {
        String testHostName = "localhost";

        this.testEmail.setHostName(testHostName);

        Assertions.assertEquals(testHostName, this.testEmail.getHostName());
    }
    
    @Test
    void getHostNameWithSession() {
        Properties testProps = new Properties();
        this.testEmail.setMailSession(Session.getInstance(testProps));

        Assertions.assertNull(this.testEmail.getHostName());
    }

    @Test
    void getHostNameNull() {

        Assertions.assertNull(this.testEmail.getHostName());
    }

    @Test
    void getSocketConnectionTimeout() {
        int testTimeout = 5;

        this.testEmail.setSocketConnectionTimeout(testTimeout);

        Assertions.assertEquals(testTimeout, this.testEmail.getSocketConnectionTimeout());
    }
}