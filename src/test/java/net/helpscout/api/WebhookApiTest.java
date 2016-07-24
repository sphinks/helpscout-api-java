package net.helpscout.api;

import lombok.SneakyThrows;
import net.helpscout.api.cbo.WebhookEventType;
import net.helpscout.api.model.Customer;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @Author: ivan
 * Date: 22.03.16
 * Time: 0:33
 */
public class WebhookApiTest {


    /**
     * Real webhook request contains such kind of headers:
     *
     * host: youcallback.domain.com
     * user-agent: Help Scout/Webhooks
     * accept-encoding: UTF-8
     * content-type: application/json
     * x-helpscout-event: customer.created
     * x-helpscout-signature: qALfoJFZ/WVbevIxtFYKHJ86D8o=
     * x-forwarded-proto: http
     * x-forwarded-port: 80
     * x-request-start: t=1458595138602884
     * x-client-ip: 000.000.000.000
     * content-length: 406
     * x-forwarded-host: youcallback.domain.com
     * x-forwarded-server: youcallback.domain.com
     * connection: Keep-Alive
     */
    @Test
    public void testForCustomEventHeader() {

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        when(httpServletRequest.getHeader("X-HELPSCOUT-EVENT")).thenReturn("customer.created");

        Webhook webhook = new Webhook("SecretKey", httpServletRequest);

        assertThat(webhook.getEventType(), equalTo(WebhookEventType.CustomerCreated));
        assertTrue(webhook.getEventType().isCustomerEvent());
    }

    @Test
    public void testForConversationEventHeader() {

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        when(httpServletRequest.getHeader("X-HELPSCOUT-EVENT")).thenReturn("convo.note.created");

        Webhook webhook = new Webhook("SecretKey", httpServletRequest);

        assertThat(webhook.getEventType(), equalTo(WebhookEventType.NoteCreated));
        assertTrue(webhook.getEventType().isConversationEvent());
    }

    /**
     * Calculating of signature performed based on JSON data and compared to
     * data in header 'x-helpscout-signature'.
     * At file 'webhook_customer' located example structure of json send to webhook.
     */
    @Test
    @SneakyThrows
    public void testForCheckIfRequestIsValid() {

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        BufferedReader bufferedReader = new BufferedReader(new StringReader(readJsonDataToString("webhook_customer")));

        when(httpServletRequest.getReader()).thenReturn(bufferedReader);
        when(httpServletRequest.getHeader("x-helpscout-signature".toUpperCase())).thenReturn("gV91IzHpvzCSLYW+/QGAxfm7KOM=");

        Webhook webhook = new Webhook("SecretKey", httpServletRequest);

        assertTrue(webhook.isValid());
    }

    @Test
    @SneakyThrows
    public void testForReadingJsonData() {

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        BufferedReader bufferedReader = new BufferedReader(new StringReader(readJsonDataToString("webhook_customer")));

        when(httpServletRequest.getReader()).thenReturn(bufferedReader);

        Webhook webhook = new Webhook("SecretKey", httpServletRequest);
        Customer customer = webhook.getCustomer();

        assertThat(customer.getFirstName(), equalTo("First_Name"));
        assertThat(customer.getLastName(), equalTo("Last_Name"));
        assertThat(customer.getEmails().get(0).getValue(), equalTo("some@mail.com"));
    }

    @SneakyThrows
    private static String readJsonDataToString(String jsonFileName) {
        Path pathToJsonData = Paths.get(ClassLoader.getSystemResource("responses/" + jsonFileName + ".json").toURI());
        return new String(Files.readAllBytes(pathToJsonData), "UTF-8");
    }
}
