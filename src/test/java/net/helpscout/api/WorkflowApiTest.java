package net.helpscout.api;

import lombok.SneakyThrows;
import net.helpscout.api.model.Workflow;
import org.junit.Test;

import java.util.HashMap;

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
        stubGET("/v1/mailboxes/6/workflows.json", "empty_pages");

        Page<Workflow> workflows = client.getWorkflows(6L);
        assertNotNull(workflows);
        assertEquals(0, workflows.getItems().size());
    }

    @Test
    @SneakyThrows
    public void shouldReturnWorkflows() {
        stubGET("/v1/mailboxes/6/workflows.json", "workflow");

        Page<Workflow> workflows = client.getWorkflows(6L);
        assertNotNull(workflows);
        assertEquals(1, workflows.getItems().size());
    }

    @Test
    @SneakyThrows
    public void shouldReturnWorkflowsWithQuery() {
        stubGETWithLikeUrl("/v1/mailboxes/6/workflows.json?.*", "workflow");

        HashMap<String, String> query = new HashMap<>();
        query.put("id", "1234");

        Page<Workflow> workflows = client.getWorkflows(6L, query);
        assertNotNull(workflows);
        assertEquals(1, workflows.getItems().size());
    }
}
