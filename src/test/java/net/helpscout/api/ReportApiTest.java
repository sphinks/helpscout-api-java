package net.helpscout.api;

import lombok.SneakyThrows;
import net.helpscout.api.model.Workflow;
import net.helpscout.api.model.report.common.DatesAndCounts;
import net.helpscout.api.model.report.conversations.Conversation;
import net.helpscout.api.model.report.conversations.ConversationsReport;
import net.helpscout.api.model.report.conversations.DayStats;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @Author: ivan
 * Date: 07.09.16
 * Time: 0:01
 */
public class ReportApiTest extends AbstractApiClientTest {

    private Map<String, String> reportParameters;

    @Before
    public void init() {
        DateTime startDate = new DateTime(2015, 12, 25, 12, 0, 0, 0);
        DateTime endDate = new DateTime();
        reportParameters = new HashMap<>();
        reportParameters.put("start", startDate.toString());
        reportParameters.put("end", endDate.toString());
    }

    @Test
    @SneakyThrows
    public void shouldReturnConversationReport() {
        givenThat(get(urlMatching("/v1/reports/conversations.json?.*"))
                .willReturn(aResponse().withStatus(HTTP_OK)
                        .withBody(getResponse("conversation_report"))));

        ConversationsReport report = client.getConversationsReport(reportParameters);

        assertNotNull(report);
        assertEquals(2, report.getFilterTags().size());
    }

    @Test
    @SneakyThrows
    public void shouldReturnBusiestTimeOfDayReport() {
        givenThat(get(urlMatching("/v1/reports/conversations/busy-times.json?.*"))
                .willReturn(aResponse().withStatus(HTTP_OK)
                        .withBody(getResponse("busiest_time_report"))));

        List<DayStats> report = client.getBusiestTimeOfDayReport(reportParameters);

        assertNotNull(report);
        assertEquals(4, report.size());
    }

    @Test
    @SneakyThrows
    public void shouldReturnNewConversationReport() {
        givenThat(get(urlMatching("/v1/reports/conversations/new.json?.*"))
                .willReturn(aResponse().withStatus(HTTP_OK)
                        .withBody(getResponse("new_conversation_report"))));

        DatesAndCounts report = client.getNewConversationsReport(reportParameters);

        assertNotNull(report);
        assertEquals(5, report.getCurrent().size());
        assertEquals(5, report.getPrevious().size());
    }

    @Test
    @SneakyThrows
    public void shouldReturnConversationDrillDownReport() {
        givenThat(get(urlMatching("/v1/reports/conversations/drilldown.json?.*"))
                .willReturn(aResponse().withStatus(HTTP_OK)
                        .withBody(getResponse("conversation_drill_down_report"))));

        Page<Conversation> report = client.getConversationsDrillDown(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getItems().size());
        assertEquals("Sample subject", report.getItems().get(0).getSubject());
    }
}
