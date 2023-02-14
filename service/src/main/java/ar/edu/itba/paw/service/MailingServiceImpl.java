package ar.edu.itba.paw.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.util.Locale;


@Service
public class MailingServiceImpl implements MailingService{

    private final Dotenv env = Dotenv.load();

    private final static String MAIL_USERNAME_PARAMETER = "MAIL_USERNAME";

    private static final String EMAIL_ENCODING = "UTF-8";
    private static final String IMG_PNG_FORMAT = "image/png";

    private static final String EXPLORE_URL = "http://pawserver.it.itba.edu.ar/paw-2022a-09/explore";

    private static final String PRODUCT_BASE_URL = "http://pawserver.it.itba.edu.ar/paw-2022a-09/product?id=";

    private static final String PROFILE_BASE_URL = "http://pawserver.it.itba.edu.ar/paw-2022a-09/profile/";

    private static final String REVIEW_BASE_URL = "http://pawserver.it.itba.edu.ar/paw-2022a-09/review/";

    private final static String CREATE_REVIEW_SUFFIX = "/create";


    private static final Logger LOGGER = LoggerFactory.getLogger(MailingServiceImpl.class);

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    private final MessageSource messageSource;

    @Autowired
    public MailingServiceImpl(SpringTemplateEngine templateEngine, JavaMailSender emailSender, MessageSource messageSource) {
        this.templateEngine = templateEngine;
        this.emailSender = emailSender;
        this.messageSource = messageSource;
    }

    /**
     *Sends a Welcome mail to the user
     *@param userEmail String that contains the email of the addressee
     *@param username String that contains the user's name.
     *@param locale Locale of the addressee in order to send the email in that language.
     *@throws MessagingException if email could not be sent
     */
    @Override
    public void sendRegisterMail(String userEmail, String username, Locale locale) /*throws MessagingException*/{
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);

            helper.setFrom(env.get(MAIL_USERNAME_PARAMETER));
            helper.setTo(userEmail);

            helper.setSubject(messageSource.getMessage("email.register.welcomeSubject", null, locale));

            Context context = new Context(locale);

            context.setVariable("logoResourceName", "logo");
            context.setVariable("exploreResourceName", "explore");
            context.setVariable("buyResourceName", "buy");
            context.setVariable("sellResourceName", "sell");

            context.setVariable("username", username);
            context.setVariable("exploreUrl", EXPLORE_URL);
            String html = templateEngine.process("register", context);
            helper.setText(html, true);

            helper.addInline("logo", new ClassPathResource("/mails/images/logo.png"), IMG_PNG_FORMAT);
            helper.addInline("explore", new ClassPathResource("/mails/images/explore.png"), IMG_PNG_FORMAT);
            helper.addInline("buy", new ClassPathResource("/mails/images/buy.png"), IMG_PNG_FORMAT);
            helper.addInline("sell", new ClassPathResource("/mails/images/sell.png"), IMG_PNG_FORMAT);

            emailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Unexpected error sending email", e);
        }
    }

    @Override
    public void sendOfferMail(String bidderMail, String sellerMail, String nftName, int nftId, String nftAddress, BigDecimal nftPrice, byte[] imageBytes, Locale locale, int productId){
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
            helper.setFrom(env.get(MAIL_USERNAME_PARAMETER));
            helper.setTo(sellerMail);
            helper.setSubject(messageSource.getMessage("email.bid.title", null, locale));

            Context context = new Context(locale);
            context.setVariable("sellerMail", sellerMail);
            context.setVariable("bidderMail", bidderMail);
            context.setVariable("nftName", nftName);
            context.setVariable("nftId", nftId);
            context.setVariable("nftAddress", nftAddress);
            context.setVariable("nftBidEthPrice", nftPrice.toPlainString());
            context.setVariable("productUrl", PRODUCT_BASE_URL + productId);
            context.setVariable("logoResourceName", "logo");
            context.setVariable("nftImageResourceName", "nftImage");
            context.setVariable("userImageResourceName", "userImage");
            context.setVariable("nftNameImageResourceName", "nftNameImage");
            context.setVariable("nftAddressImageResourceName", "nftAddressImage");
            context.setVariable("priceImageResourceName", "priceImage");

            String html = templateEngine.process("bid", context);
            helper.setText(html, true);

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(imageBytes));
            InputStreamSource imageSource = new ByteArrayResource(imageBytes);

            helper.addInline("logo", new ClassPathResource("/mails/images/logo.png"), IMG_PNG_FORMAT);
            helper.addInline("userImage", new ClassPathResource("/mails/images/user.png"), IMG_PNG_FORMAT);
            helper.addInline("priceImage", new ClassPathResource("/mails/images/sell.png"), IMG_PNG_FORMAT);
            helper.addInline("nftNameImage", new ClassPathResource("/mails/images/nft.png"), IMG_PNG_FORMAT);
            helper.addInline("nftAddressImage", new ClassPathResource("/mails/images/chain.png"), IMG_PNG_FORMAT);
            helper.addInline("nftImage", imageSource, URLConnection.guessContentTypeFromStream(is));

            emailSender.send(message);
        } catch (MessagingException | IOException e) {
            LOGGER.error("Unexpected error sending email", e);
        }
    }

    @Override
    public void sendNftCreatedMail(String userEmail, String username, int nftId, String nftName, String nftAddress, byte[] imageBytes, Locale locale, int productId) {
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
            helper.setFrom(env.get(MAIL_USERNAME_PARAMETER));
            helper.setTo(userEmail);
            helper.setSubject(messageSource.getMessage("email.nftCreated.subject", null, locale));

            Context context = new Context(locale);
            context.setVariable("userEmail", userEmail);
            context.setVariable("username", username);
            context.setVariable("nftName", nftName);
            context.setVariable("nftId", nftId);
            context.setVariable("nftAddress", nftAddress);
            context.setVariable("productUrl", PRODUCT_BASE_URL + productId);
            context.setVariable("logoResourceName", "logo");
            context.setVariable("nftImageResourceName", "nftImage");
            context.setVariable("userImageResourceName", "userImage");
            context.setVariable("nftNameImageResourceName", "nftNameImage");
            context.setVariable("nftAddressImageResourceName", "nftAddressImage");

            String html = templateEngine.process("nftCreated", context);
            helper.setText(html, true);

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(imageBytes));
            InputStreamSource imageSource = new ByteArrayResource(imageBytes);

            helper.addInline("logo", new ClassPathResource("/mails/images/logo.png"), IMG_PNG_FORMAT);
            helper.addInline("userImage", new ClassPathResource("/mails/images/user.png"), IMG_PNG_FORMAT);
            helper.addInline("priceImage", new ClassPathResource("/mails/images/sell.png"), IMG_PNG_FORMAT);
            helper.addInline("nftNameImage", new ClassPathResource("/mails/images/nft.png"), IMG_PNG_FORMAT);
            helper.addInline("nftAddressImage", new ClassPathResource("/mails/images/chain.png"), IMG_PNG_FORMAT);
            helper.addInline("nftImage", imageSource, URLConnection.guessContentTypeFromStream(is));

            emailSender.send(message);
        } catch (MessagingException | IOException e) {
            LOGGER.error("Unexpected error sending email", e);
        }
    }

    @Override
    public void sendNftSellOrderCreatedMail(String userEmail, String username, int nftId, String nftName, String nftAddress, BigDecimal nftPrice, byte[] imageBytes, Locale locale, int productId) {
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
            helper.setFrom(env.get(MAIL_USERNAME_PARAMETER));
            helper.setTo(userEmail);
            helper.setSubject(messageSource.getMessage("email.sellOrderCreated.subject", null, locale));

            Context context = new Context(locale);
            context.setVariable("userEmail", userEmail);
            context.setVariable("username", username);
            context.setVariable("nftName", nftName);
            context.setVariable("nftId", nftId);
            context.setVariable("nftAddress", nftAddress);
            context.setVariable("nftPrice", nftPrice.toPlainString());
            context.setVariable("productUrl", PRODUCT_BASE_URL + productId);
            context.setVariable("logoResourceName", "logo");
            context.setVariable("nftImageResourceName", "nftImage");
            context.setVariable("userImageResourceName", "userImage");
            context.setVariable("nftNameImageResourceName", "nftNameImage");
            context.setVariable("nftAddressImageResourceName", "nftAddressImage");
            context.setVariable("priceImageResourceName", "priceImage");

            String html = templateEngine.process("sellOrderCreated", context);
            helper.setText(html, true);

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(imageBytes));
            InputStreamSource imageSource = new ByteArrayResource(imageBytes);

            helper.addInline("logo", new ClassPathResource("/mails/images/logo.png"), IMG_PNG_FORMAT);
            helper.addInline("userImage", new ClassPathResource("/mails/images/user.png"), IMG_PNG_FORMAT);
            helper.addInline("priceImage", new ClassPathResource("/mails/images/sell.png"), IMG_PNG_FORMAT);
            helper.addInline("nftNameImage", new ClassPathResource("/mails/images/nft.png"), IMG_PNG_FORMAT);
            helper.addInline("nftAddressImage", new ClassPathResource("/mails/images/chain.png"), IMG_PNG_FORMAT);
            helper.addInline("nftImage", imageSource, URLConnection.guessContentTypeFromStream(is));

            emailSender.send(message);
        } catch (MessagingException | IOException e) {
            LOGGER.error("Unexpected error sending email", e);
        }
    }

    @Override
    public void sendOfferAcceptedMail(String bidderMail, String sellerMail, int sellerId, String username, String nftName, int nftId, String nftAddress, BigDecimal offerPrice, byte[] imageBytes, Locale locale, int productId) {
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
            helper.setFrom(env.get(MAIL_USERNAME_PARAMETER));
            helper.setTo(bidderMail);
            helper.setSubject(messageSource.getMessage("email.accepted.title", null, locale));

            Context context = new Context(locale);
            context.setVariable("sellerMail", sellerMail);
            context.setVariable("bidderMail", bidderMail);
            context.setVariable("nftName", nftName);
            context.setVariable("nftId", nftId);
            context.setVariable("nftAddress", nftAddress);
            context.setVariable("offerPrice", offerPrice.toPlainString());
            context.setVariable("productUrl", PRODUCT_BASE_URL + productId);
            context.setVariable("reviewUrl", REVIEW_BASE_URL + sellerId + CREATE_REVIEW_SUFFIX);
            context.setVariable("logoResourceName", "logo");
            context.setVariable("nftImageResourceName", "nftImage");
            context.setVariable("userImageResourceName", "userImage");
            context.setVariable("nftNameImageResourceName", "nftNameImage");
            context.setVariable("nftAddressImageResourceName", "nftAddressImage");
            context.setVariable("priceImageResourceName", "priceImage");

            String html = templateEngine.process("offerAccepted", context);
            helper.setText(html, true);

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(imageBytes));
            InputStreamSource imageSource = new ByteArrayResource(imageBytes);

            helper.addInline("logo", new ClassPathResource("/mails/images/logo.png"), IMG_PNG_FORMAT);
            helper.addInline("userImage", new ClassPathResource("/mails/images/user.png"), IMG_PNG_FORMAT);
            helper.addInline("priceImage", new ClassPathResource("/mails/images/sell.png"), IMG_PNG_FORMAT);
            helper.addInline("nftNameImage", new ClassPathResource("/mails/images/nft.png"), IMG_PNG_FORMAT);
            helper.addInline("nftAddressImage", new ClassPathResource("/mails/images/chain.png"), IMG_PNG_FORMAT);
            helper.addInline("nftImage", imageSource, URLConnection.guessContentTypeFromStream(is));

            emailSender.send(message);
        } catch (MessagingException | IOException e) {
            LOGGER.error("Unexpected error sending email", e);
        }
    }

    @Override
    public void sendOfferRejectedMail(String bidderMail, String sellerMail, String bidderUsername, String nftName, int nftId, String nftAddress, BigDecimal offerPrice, byte[] imageBytes, Locale locale, int productId) {
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
            helper.setFrom(env.get(MAIL_USERNAME_PARAMETER));
            helper.setTo(bidderMail);
            helper.setSubject(messageSource.getMessage("email.rejected.title", null, locale));

            Context context = new Context(locale);
            context.setVariable("sellerMail", sellerMail);
            context.setVariable("bidderMail", bidderMail);
            context.setVariable("nftName", nftName);
            context.setVariable("nftId", nftId);
            context.setVariable("nftAddress", nftAddress);
            context.setVariable("offerPrice", offerPrice.toPlainString());
            context.setVariable("productUrl", PRODUCT_BASE_URL + productId);
            context.setVariable("logoResourceName", "logo");
            context.setVariable("nftImageResourceName", "nftImage");
            context.setVariable("userImageResourceName", "userImage");
            context.setVariable("nftNameImageResourceName", "nftNameImage");
            context.setVariable("nftAddressImageResourceName", "nftAddressImage");
            context.setVariable("priceImageResourceName", "priceImage");

            String html = templateEngine.process("offerRejected", context);
            helper.setText(html, true);

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(imageBytes));
            InputStreamSource imageSource = new ByteArrayResource(imageBytes);

            helper.addInline("logo", new ClassPathResource("/mails/images/logo.png"), IMG_PNG_FORMAT);
            helper.addInline("userImage", new ClassPathResource("/mails/images/user.png"), IMG_PNG_FORMAT);
            helper.addInline("priceImage", new ClassPathResource("/mails/images/sell.png"), IMG_PNG_FORMAT);
            helper.addInline("nftNameImage", new ClassPathResource("/mails/images/nft.png"), IMG_PNG_FORMAT);
            helper.addInline("nftAddressImage", new ClassPathResource("/mails/images/chain.png"), IMG_PNG_FORMAT);
            helper.addInline("nftImage", imageSource, URLConnection.guessContentTypeFromStream(is));

            emailSender.send(message);
        } catch (MessagingException | IOException e) {
            LOGGER.error("Unexpected error sending email", e);
        }
    }



    @Override
    public void sendNftDeletedMail(String userEmail, String username, int nftId, String nftName, String nftAddress, byte[] imageBytes, Locale locale) {
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
            helper.setFrom(env.get(MAIL_USERNAME_PARAMETER));
            helper.setTo(userEmail);
            helper.setSubject(messageSource.getMessage("email.nftDeleted.title", null, locale));

            Context context = new Context(locale);
            context.setVariable("userEmail", userEmail);
            context.setVariable("username", username);
            context.setVariable("nftName", nftName);
            context.setVariable("nftId", nftId);
            context.setVariable("nftAddress", nftAddress);
            context.setVariable("logoResourceName", "logo");
            context.setVariable("nftImageResourceName", "nftImage");
            context.setVariable("userImageResourceName", "userImage");
            context.setVariable("nftNameImageResourceName", "nftNameImage");
            context.setVariable("nftAddressImageResourceName", "nftAddressImage");

            String html = templateEngine.process("nftDeleted", context);
            helper.setText(html, true);

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(imageBytes));
            InputStreamSource imageSource = new ByteArrayResource(imageBytes);

            helper.addInline("logo", new ClassPathResource("/mails/images/logo.png"), IMG_PNG_FORMAT);
            helper.addInline("userImage", new ClassPathResource("/mails/images/user.png"), IMG_PNG_FORMAT);
            helper.addInline("priceImage", new ClassPathResource("/mails/images/sell.png"), IMG_PNG_FORMAT);
            helper.addInline("nftNameImage", new ClassPathResource("/mails/images/nft.png"), IMG_PNG_FORMAT);
            helper.addInline("nftAddressImage", new ClassPathResource("/mails/images/chain.png"), IMG_PNG_FORMAT);
            helper.addInline("nftImage", imageSource, URLConnection.guessContentTypeFromStream(is));

            emailSender.send(message);
        } catch (MessagingException | IOException e) {
            LOGGER.error("Unexpected error sending email", e);
        }
    }

    @Override
    public void sendSellOrderDeletedMail(String userEmail, String username, int nftId, String nftName, String nftAddress, byte[] imageBytes, BigDecimal nftPrice, Locale locale) {
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
            helper.setFrom(env.get(MAIL_USERNAME_PARAMETER));
            helper.setTo(userEmail);
            helper.setSubject(messageSource.getMessage("email.sellOrderDeleted.title", null, locale));

            Context context = new Context(locale);
            context.setVariable("userEmail", userEmail);
            context.setVariable("username", username);
            context.setVariable("nftName", nftName);
            context.setVariable("nftId", nftId);
            context.setVariable("nftAddress", nftAddress);
            context.setVariable("nftPrice", nftPrice.toPlainString());
            context.setVariable("productUrl", PRODUCT_BASE_URL + nftId);
            context.setVariable("logoResourceName", "logo");
            context.setVariable("nftImageResourceName", "nftImage");
            context.setVariable("userImageResourceName", "userImage");
            context.setVariable("nftNameImageResourceName", "nftNameImage");
            context.setVariable("nftAddressImageResourceName", "nftAddressImage");
            context.setVariable("priceImageResourceName", "priceImage");

            String html = templateEngine.process("sellOrderDeleted", context);
            helper.setText(html, true);

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(imageBytes));
            InputStreamSource imageSource = new ByteArrayResource(imageBytes);

            helper.addInline("logo", new ClassPathResource("/mails/images/logo.png"), IMG_PNG_FORMAT);
            helper.addInline("userImage", new ClassPathResource("/mails/images/user.png"), IMG_PNG_FORMAT);
            helper.addInline("priceImage", new ClassPathResource("/mails/images/sell.png"), IMG_PNG_FORMAT);
            helper.addInline("nftNameImage", new ClassPathResource("/mails/images/nft.png"), IMG_PNG_FORMAT);
            helper.addInline("nftAddressImage", new ClassPathResource("/mails/images/chain.png"), IMG_PNG_FORMAT);
            helper.addInline("nftImage", imageSource, URLConnection.guessContentTypeFromStream(is));

            emailSender.send(message);
        } catch (MessagingException | IOException e) {
            LOGGER.error("Unexpected error sending email", e);
        }
    }


    @Override
    public void sendNewReviewMail(String revieweeName, String revieweeEmail, int revieweeUserId, String reviewerName, String reviewerEmail, int score, String reviewTitle, String reviewComment, Locale locale) {
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);

            helper.setFrom(env.get(MAIL_USERNAME_PARAMETER));
            helper.setTo(revieweeEmail);
            helper.setSubject(messageSource.getMessage("email.newReview.title", null, locale));

            Context context = new Context(locale);

            context.setVariable("revieweeName", revieweeName);
            context.setVariable("reviewerName", reviewerName);
            context.setVariable("reviewerEmail", reviewerEmail);
            context.setVariable("profileUrl", PROFILE_BASE_URL + revieweeUserId);
            context.setVariable("score", score);
            context.setVariable("reviewTitle", reviewTitle);
            context.setVariable("reviewComment", reviewComment);
            context.setVariable("logoResourceName", "logo");
            context.setVariable("userImageResourceName", "userImage");
            context.setVariable("reviewImageResourceName", "reviewImage");
            context.setVariable("reviewScoreImageResourceName", "scoreImage");

            String html = templateEngine.process("newReview", context);
            helper.setText(html, true);

            helper.addInline("logo", new ClassPathResource("/mails/images/logo.png"), IMG_PNG_FORMAT);
            helper.addInline("userImage", new ClassPathResource("/mails/images/user.png"), IMG_PNG_FORMAT);
            helper.addInline("reviewImage", new ClassPathResource("/mails/images/review.png"), IMG_PNG_FORMAT);
            helper.addInline("scoreImage", new ClassPathResource("/mails/images/score.png"), IMG_PNG_FORMAT);

            emailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Unexpected error sending email", e);
        }
    }
}
