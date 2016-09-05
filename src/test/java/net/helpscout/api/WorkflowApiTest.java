package net.helpscout.api;

import lombok.SneakyThrows;
import net.helpscout.api.model.Workflow;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @Author: ivan
 * Date: 05.09.16
 * Time: 0:00
 */
public class WorkflowApiTest extends AbstractApiClientTest {

    @Test
    @SneakyThrows
    public void shouldReturnEmptyResultOnWorkflow() {
        givenThat(get(urlEqualTo("/v1/mailboxes/6/workflows.json"))
                .willReturn(aResponse().withStatus(HTTP_OK)
                        .withBody(getResponse("empty_pages"))));

        Page<Workflow> workflows = client.getWorkflows(6L);
        assertNotNull(workflows);
        assertEquals(0, workflows.getItems().size());
    }

    @Test
    @SneakyThrows
    public void shouldReturnCorrectWorkflow() {
        givenThat(get(urlEqualTo("/v1/mailboxes/6/workflows.json"))
                .willReturn(aResponse().withStatus(HTTP_OK)
                        .withBody(getResponse("workflow"))));

        Page<Workflow> workflows = client.getWorkflows(6L);
        assertNotNull(workflows);
        assertEquals(1, workflows.getItems().size());
    }
}
