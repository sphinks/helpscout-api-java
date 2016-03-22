package net.helpscout.api;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import net.helpscout.api.model.Mailbox;
import net.helpscout.api.model.customfield.CustomField;
import net.helpscout.api.model.customfield.CustomFieldType;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.collect.ImmutableList.of;
import static java.net.HttpURLConnection.HTTP_OK;
import static net.helpscout.api.model.customfield.CustomFieldType.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
    public void basicTestForCustomEventHeader() {

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        final String event_type = "customer.created";

        when(httpServletRequest.getHeader("x-helpscout-event".toUpperCase())).thenReturn(event_type);
        when(httpServletRequest.getHeader("x-helpscout-signature".toUpperCase())).thenReturn("qALfoJFZ/WVbevIxtFYKHJ86D8o=");

        Webhook webhook = new Webhook("SecretKey", httpServletRequest);

        assertThat(webhook.getEventType(), equalTo(event_type));
        assertTrue(webhook.isCustomerEvent());
    }
}
