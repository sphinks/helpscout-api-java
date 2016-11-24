package net.helpscout.api;

import lombok.SneakyThrows;
import net.helpscout.api.model.Customer;
import net.helpscout.api.model.customer.EmailEntry;
import net.helpscout.api.model.customer.SearchCustomer;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * @Author: ivan
 * Date: 11.09.16
 * Time: 16:10
 */
public class CustomerApiTest extends AbstractApiClientTest {

    private Customer customer;

    @Before
    public void inti() {
        EmailEntry email = new EmailEntry();
        email.setValue("test.customer@email.com");
        email.setLocation(EmailEntry.Location.Home.getLabel());

        customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("Customer");
        customer.setEmails(Arrays.asList(email));
    }

    @Test
    @SneakyThrows
    public void shouldCreateCustomer() {
        givenThat(post(urlEqualTo("/v1/customers.json"))
                .willReturn(aResponse().withStatus(HTTP_CREATED)));

        client.createCustomer(customer);

        verify(postRequestedFor(urlEqualTo("/v1/customers.json"))
                .withRequestBody(equalToJson("{\"firstName\":\"Test\",\"lastName\":\"Customer\",\"emails\":[{\"value\":\"test.customer@email.com\",\"location\":\"home\"}]}")));

    }

    @Test
    @SneakyThrows
    public void shouldUpdateCustomer() {
        givenThat(put(urlEqualTo("/v1/customers/1.json"))
                .willReturn(aResponse().withStatus(HTTP_OK)));

        customer.setId(1L);
        client.updateCustomer(customer);

        verify(putRequestedFor(urlEqualTo("/v1/customers/1.json"))
                .withRequestBody(equalToJson("{\"id\":1,\"firstName\":\"Test\",\"lastName\":\"Customer\",\"emails\":[{\"value\":\"test.customer@email.com\",\"location\":\"home\"}]}")));

    }

    @Test
    @SneakyThrows
    public void shouldRetrieveSpecificCustomer() {
        stubGET("/v1/customers/60984612.json", "customer");

        Long customerId = 60984612L;
        Customer customer = client.getCustomer(customerId);

        assertThat(customer.getFirstName(), equalTo("Peter"));
        assertEquals(customerId, customer.getId());
        assertNotNull(customer.getAddress().getCreatedAt());
        Long addressId = 1187643L;
        assertEquals(addressId, customer.getAddress().getId());

        verify(getRequestedFor(urlEqualTo("/v1/customers/60984612.json")));
    }

    @Test
    @SneakyThrows
    public void shouldRetrieveSpecificCustomerWithFields() {
        stubGETWithLikeUrl("/v1/customers/60984612.json?.*", "customer_short");

        Long customerId = 60984612L;
        Customer customer = client.getCustomer(customerId, Arrays.asList("id", "firstName"));

        assertEquals("Peter", customer.getFirstName());
        assertEquals(customerId, customer.getId());
        assertThat(customer.getAddress(), is(nullValue()));

        verify(getRequestedFor(urlEqualTo("/v1/customers/60984612.json?fields=id,firstName")));
    }

    @Test
    @SneakyThrows
    public void shouldRetrieveCustomersWithFields() {
        stubGETWithLikeUrl("/v1/customers.json?.*", "customer_list_short");

        Page<Customer> customers = client.getCustomers(Arrays.asList("id", "firstName"));

        assertThat(customers.getItems().size(), equalTo(1));
        assertNotNull(customers.getItems().get(0));
        assertThat(customers.getItems().get(0).getFirstName(), equalTo("Vernon"));
        assertThat(customers.getItems().get(0).getId(), equalTo(29418L));

        verify(getRequestedFor(urlEqualTo("/v1/customers.json?fields=id,firstName")));
    }

    @Test(expected = ApiException.class)
    @SneakyThrows
    public void shouldNotRetrieveSpecificCustomerWithWrongId() {
        client.getCustomer(-1L);
    }

    @Test
    @SneakyThrows
    public void shouldReturnSearchResultOfCustomer() {
        stubGETWithLikeUrl("/v1/search/customers.json?.*", "customer_search");

        Page<SearchCustomer> searchCustomers = client.searchCustomers("firstName:\"John\" AND lastName:\"Appleseed\"", null, null, 1);

        assertThat(searchCustomers.getItems().size(), equalTo(1));
        assertNotNull(searchCustomers.getItems().get(0));
        assertThat(searchCustomers.getItems().get(0).getFullName(), equalTo("John Appleseed"));

        verify(getRequestedFor(urlEqualTo("/v1/search/customers.json?query=firstName:%22John%22%20AND%20lastName:%22Appleseed%22&page=1")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnSearchResultOfCustomerWithSpecifiedFields() {
        stubGETWithLikeUrl("/v1/customers.json?.*", "customers_list");

        Page<Customer> customers = client.searchCustomers(null, "Vernon", "Bear", 0, null);

        assertThat(customers.getItems().size(), equalTo(1));
        assertNotNull(customers.getItems().get(0));
        assertThat(customers.getItems().get(0).getLastName(), equalTo("Bear"));

        verify(getRequestedFor(urlEqualTo("/v1/customers.json?firstName=Vernon&lastName=Bear")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCustomers() {
        stubGET("/v1/customers.json", "customers_list");

        Page<Customer> customers = client.getCustomers();

        assertThat(customers.getItems().size(), equalTo(1));
        assertNotNull(customers.getItems().get(0));
        assertThat(customers.getItems().get(0).getLastName(), equalTo("Bear"));

        verify(getRequestedFor(urlEqualTo("/v1/customers.json")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCustomersWithPage() {
        stubGETWithLikeUrl("/v1/customers.json?.*", "customers_list");

        Page<Customer> customers = client.getCustomers(4);

        assertThat(customers.getItems().size(), equalTo(1));
        assertNotNull(customers.getItems().get(0));
        assertThat(customers.getItems().get(0).getLastName(), equalTo("Bear"));

        verify(getRequestedFor(urlEqualTo("/v1/customers.json?page=4")));
    }
}
