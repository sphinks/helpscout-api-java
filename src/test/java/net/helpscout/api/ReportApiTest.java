package net.helpscout.api;

import lombok.SneakyThrows;
import net.helpscout.api.model.report.common.DatesAndCounts;
import net.helpscout.api.model.report.common.DatesAndElapsedTimes;
import net.helpscout.api.model.report.common.Rating;
import net.helpscout.api.model.report.conversations.Conversation;
import net.helpscout.api.model.report.conversations.ConversationsReport;
import net.helpscout.api.model.report.conversations.DayStats;
import net.helpscout.api.model.report.docs.DocsReport;
import net.helpscout.api.model.report.happiness.HappinessReport;
import net.helpscout.api.model.report.productivity.ProductivityReport;
import net.helpscout.api.model.report.user.UserReport;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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
        stubGETWithLikeUrl("/v1/reports/conversations.json?.*", "conversation_report");

        ConversationsReport report = client.getConversationsReport(reportParameters);

        assertNotNull(report);
        assertEquals(2, report.getFilterTags().size());
    }

    @Test
    @SneakyThrows
    public void shouldReturnBusiestTimeOfDayReport() {
        stubGETWithLikeUrl("/v1/reports/conversations/busy-times.json?.*", "busiest_time_report");

        List<DayStats> report = client.getBusiestTimeOfDayReport(reportParameters);

        assertNotNull(report);
        assertEquals(4, report.size());
    }

    @Test
    @SneakyThrows
    public void shouldReturnNewConversationReport() {
        stubGETWithLikeUrl("/v1/reports/conversations/new.json?.*", "dates_and_counts_report");

        DatesAndCounts report = client.getNewConversationsReport(reportParameters);

        assertNotNull(report);
        assertEquals(5, report.getCurrent().size());
        assertEquals(new Integer(577), report.getPrevious().get(2).getCount());
    }

    @Test
    @SneakyThrows
    public void shouldReturnConversationDrillDownReport() {
        stubGETWithLikeUrl("/v1/reports/conversations/drilldown.json?.*", "conversation_drill_down_report");

        Page<Conversation> report = client.getConversationsDrillDown(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getItems().size());
        assertEquals("Sample subject", report.getItems().get(0).getSubject());
    }

    @Test
    @SneakyThrows
    public void shouldReturnConversationDrillDownReportByField() {
        stubGETWithLikeUrl("/v1/reports/conversations/fields-drilldown.json?.*", "conversation_drill_down_report");

        reportParameters.put("field", "tagid");
        reportParameters.put("fieldid", "123");
        Page<Conversation> report = client.getConversationsDrillDownByField(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getItems().size());
        assertEquals("Sample subject", report.getItems().get(0).getSubject());
    }

    @Test
    @SneakyThrows
    public void shouldReturnNewConversationDrillDownReport() {
        stubGETWithLikeUrl("/v1/reports/conversations/new-drilldown.json?.*", "conversation_drill_down_report");

        Page<Conversation> report = client.getNewConversationsDrillDown(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getItems().size());
        assertEquals("Sample subject", report.getItems().get(0).getSubject());
    }

    @Test
    @SneakyThrows
    public void shouldReturnDocsReport() {
        stubGETWithLikeUrl("/v1/reports/docs.json?.*", "docs_report");

        DocsReport report = client.getDocsReport(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getPopularSearches().size());
        assertEquals("api", report.getPopularSearches().get(0).getId());
    }

    @Test
    @SneakyThrows
    public void shouldReturnHappinessReport() {
        stubGETWithLikeUrl("/v1/reports/happiness.json?.*", "happiness_report");

        HappinessReport report = client.getHappinessReport(reportParameters);

        assertNotNull(report);
        assertEquals(-5.58, report.getDeltas().getOkay(), 0.1);
    }

    @Test
    @SneakyThrows
    public void shouldReturnHappinessRatingReport() {
        stubGETWithLikeUrl("/v1/reports/happiness/ratings.json?.*", "happiness_rating_report");

        Page<Rating> report = client.getHappinessRatings(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getItems().size());
        assertEquals("john@example.com", report.getItems().get(0).getRatingCustomerName());
    }

    @Test
    @SneakyThrows
    public void shouldReturnProductivityReport() {
        stubGETWithLikeUrl("/v1/reports/productivity.json?.*", "productivity_report");

        ProductivityReport report = client.getProductivityReport(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getFilterTags().size());
        assertEquals("sample-tag", report.getFilterTags().get(0).getName());
    }

    @Test
    @SneakyThrows
    public void shouldReturnProductivityFirstResponseTimeReport() {
        stubGETWithLikeUrl("/v1/reports/productivity/first-response-time.json?.*", "productivity_elapsed_times_report");

        DatesAndElapsedTimes report = client.getFirstResponseTimes(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getCurrent().size());
        assertEquals(94825.7, report.getPrevious().get(0).getTime(), 0.1);
    }

    @Test
    @SneakyThrows
    public void shouldReturnProductivityResolutionTimeReport() {
        stubGETWithLikeUrl("/v1/reports/productivity/resolution-time.json?.*", "productivity_elapsed_times_report");

        DatesAndElapsedTimes report = client.getResolutionTimes(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getCurrent().size());
        assertEquals(94825.7, report.getPrevious().get(0).getTime(), 0.1);
    }

    @Test
    @SneakyThrows
    public void shouldReturnProductivityResponseTimeReport() {
        stubGETWithLikeUrl("/v1/reports/productivity/response-time.json?.*", "productivity_elapsed_times_report");

        DatesAndElapsedTimes report = client.getResponseTime(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getCurrent().size());
        assertEquals(94825.7, report.getPrevious().get(0).getTime(), 0.1);
    }

    @Test
    @SneakyThrows
    public void shouldReturnProductivityDrillDownReport() {
        stubGETWithLikeUrl("/v1/reports/productivity/drilldown.json?.*", "conversation_drill_down_report");

        Page<Conversation> report = client.getProductivityDrillDown(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getItems().size());
        assertEquals("Sample subject", report.getItems().get(0).getSubject());
    }

    @Test
    @SneakyThrows
    public void shouldReturnRepliesSentReport() {
        stubGETWithLikeUrl("/v1/reports/productivity/replies-sent.json?.*", "dates_and_counts_report");

        DatesAndCounts report = client.getRepliesSent(reportParameters);

        assertNotNull(report);
        assertEquals(5, report.getCurrent().size());
        assertEquals(new Integer(577), report.getPrevious().get(2).getCount());
    }

    @Test
    @SneakyThrows
    public void shouldReturnResolvedReport() {
        stubGETWithLikeUrl("/v1/reports/productivity/resolved.json?.*", "dates_and_counts_report");

        DatesAndCounts report = client.getResolved(reportParameters);

        assertNotNull(report);
        assertEquals(5, report.getCurrent().size());
        assertEquals(new Integer(577), report.getPrevious().get(2).getCount());
    }

    @Test
    @SneakyThrows
    public void shouldReturnTeamCustomersHelpedReport() {
        stubGETWithLikeUrl("/v1/reports/team/customers-helped.json?.*", "dates_and_counts_report");

        DatesAndCounts report = client.getTeamCustomersHelped(reportParameters);

        assertNotNull(report);
        assertEquals(5, report.getCurrent().size());
        assertEquals(new Integer(577), report.getPrevious().get(2).getCount());
    }

    @Test
    @SneakyThrows
    public void shouldReturnTeamDrillDownReport() {
        stubGETWithLikeUrl("/v1/reports/team/drilldown.json?.*", "conversation_drill_down_report");

        Page<Conversation> report = client.getTeamDrillDown(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getItems().size());
        assertEquals("Sample subject", report.getItems().get(0).getSubject());
    }

    @Test
    @SneakyThrows
    public void shouldReturnUserReport() {
        stubGETWithLikeUrl("/v1/reports/user.json?.*", "user_report");

        UserReport report = client.getUserReport(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getFilterTags().size());
        assertEquals("John Smith", report.getUser().getName());
    }

    @Test
    @SneakyThrows
    public void shouldReturnUserCustomersHelpedReport() {
        stubGETWithLikeUrl("/v1/reports/user/customers-helped.json?.*", "dates_and_counts_report");

        DatesAndCounts report = client.getUserCustomersHelped(reportParameters);

        assertNotNull(report);
        assertEquals(5, report.getCurrent().size());
        assertEquals(new Integer(577), report.getPrevious().get(2).getCount());
    }

    @Test
    @SneakyThrows
    public void shouldReturnUserRepliesReport() {
        stubGETWithLikeUrl("/v1/reports/user/replies.json?.*", "dates_and_counts_report");

        DatesAndCounts report = client.getUserReplies(reportParameters);

        assertNotNull(report);
        assertEquals(5, report.getCurrent().size());
        assertEquals(new Integer(577), report.getPrevious().get(2).getCount());
    }

    @Test
    @SneakyThrows
    public void shouldReturnUserResolutionsReport() {
        stubGETWithLikeUrl("/v1/reports/user/resolutions.json?.*", "dates_and_counts_report");

        DatesAndCounts report = client.getUserResolutions(reportParameters);

        assertNotNull(report);
        assertEquals(5, report.getCurrent().size());
        assertEquals(new Integer(577), report.getPrevious().get(2).getCount());
    }

    @Test
    @SneakyThrows
    public void shouldReturnUserDrillDownReport() {
        stubGETWithLikeUrl("/v1/reports/user/drilldown.json?.*", "conversation_drill_down_report");

        Page<Conversation> report = client.getUserDrillDown(reportParameters);

        assertNotNull(report);
        assertEquals(1, report.getItems().size());
        assertEquals("Sample subject", report.getItems().get(0).getSubject());
    }

}
