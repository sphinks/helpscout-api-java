package net.helpscout.api;

import lombok.SneakyThrows;
import net.helpscout.api.model.Attachment;
import org.junit.Test;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @Author: ivan
 * Date: 11.09.16
 * Time: 19:19
 */
public class AttachmentApiTest extends AbstractApiClientTest {

    @Test
    @SneakyThrows
    public void shouldCreateAttachment() {
        givenThat(post(urlEqualTo("/v1/attachments.json"))
                .willReturn(aResponse().withStatus(HTTP_CREATED)
                .withBody(getResponse("response_with_hash"))));

        Attachment attachment = new Attachment();
        attachment.setData("Test data");
        attachment.setFileName("http://developer.helpscout.net/img/logo.png");
        attachment.setMimeType("image/jpeg");
        client.createAttachment(attachment);

        assertThat(attachment.getHash(), equalTo("someHash"));

        verify(postRequestedFor(urlEqualTo("/v1/attachments.json"))
                .withRequestBody(equalToJson("{\"mimeType\":\"image/jpeg\",\"fileName\":\"http://developer.helpscout.net/img/logo.png\",\"size\":0,\"width\":0,\"height\":0,\"data\":\"Test data\"}")));

    }

    @Test
    @SneakyThrows
    public void shouldDeleteAttachment() {
        givenThat(delete(urlEqualTo("/v1/attachments/1.json"))
                .willReturn(aResponse().withStatus(HTTP_OK)));

        client.deleteAttachment(1L);

        verify(deleteRequestedFor(urlEqualTo("/v1/attachments/1.json")));
    }

    @Test
    @SneakyThrows
    public void shouldDeleteNote() {
        givenThat(delete(urlEqualTo("/v1/notes/5.json"))
                .willReturn(aResponse().withStatus(HTTP_OK)));

        client.deleteNote(5L);

        verify(deleteRequestedFor(urlEqualTo("/v1/notes/5.json")));
    }
}
