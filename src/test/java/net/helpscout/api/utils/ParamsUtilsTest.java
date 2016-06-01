package net.helpscout.api.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @Author: ivan
 * Date: 06.05.16
 * Time: 19:31
 */
public class ParamsUtilsTest {
    
    @Test
    public void testSetParamsWithTwoParams() {

        String url = "http://some-test-url.com";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("param1", "value1");
        parameters.put("param2", "Some value with space");
        String resultUrl = ParamsUtils.setParams(url, parameters);

        assertNotNull(resultUrl);
        assertThat(resultUrl, is("http://some-test-url.com?param1=value1&param2=Some+value+with+space"));
    }

    @Test
    public void testSetParamsWithZeroParams() {

        String url = "http://some-test-url.com";
        Map<String, String> parameters = new HashMap<>();

        String resultUrl = ParamsUtils.setParams(url, parameters);

        assertNotNull(resultUrl);
        assertThat(resultUrl, is("http://some-test-url.com"));
    }

    @Test
    public void testSetParamsWithNullParams() {

        String url = "http://some-test-url.com";

        String resultUrl = ParamsUtils.setParams(url, null);

        assertNotNull(resultUrl);
        assertThat(resultUrl, is("http://some-test-url.com"));
    }

    @Test
    public void testSetFieldsWithThreeNonEmptyFields() {

        String url = "http://some-test-url.com";
        List<String> fields = new ArrayList<>();
        fields.add("field1");
        fields.add("field2");
        fields.add("3field");

        String resultUrl = ParamsUtils.setFields(url, fields);

        assertNotNull(resultUrl);
        assertThat(resultUrl, is("http://some-test-url.com?fields=field1,field2,3field"));
    }

    @Test
    public void testSetFieldsWithThreeNonEmptyFieldsAndComplexUrl() {

        String url = "http://some-test-url.com?someParam=Some+value";
        List<String> fields = new ArrayList<>();
        fields.add("field1");
        fields.add("field2");
        fields.add("3field");

        String resultUrl = ParamsUtils.setFields(url, fields);

        assertNotNull(resultUrl);
        assertThat(resultUrl, is("http://some-test-url.com?someParam=Some+value&fields=field1,field2,3field"));
    }

    @Test
    public void testSetFieldsWithZeroFieldsAndComplexUrl() {

        String url = "http://some-test-url.com?someParam=someValue";
        List<String> fields = new ArrayList<>();

        String resultUrl = ParamsUtils.setFields(url, fields);

        assertNotNull(resultUrl);
        assertThat(resultUrl, is("http://some-test-url.com?someParam=someValue"));
    }

    @Test
    public void testSetFieldsWithNullFieldsAndComplexUrl() {

        String url = "http://some-test-url.com?someParam=someValue";

        String resultUrl = ParamsUtils.setFields(url, null);

        assertNotNull(resultUrl);
        assertThat(resultUrl, is("http://some-test-url.com?someParam=someValue"));
    }

    @Test
    public void testGetCustomerSearchParameters() {

        String email = "email@email.com";
        String firstName = "SomeFirstName";
        String lastName = "SomeLastName";
        Integer page = 5;

        Map<String, String> resultParamMap = ParamsUtils.getCustomerSearchParams(email, firstName, lastName, page);

        assertNotNull(resultParamMap);
        assertThat(resultParamMap.size(), is(4));
        assertThat(resultParamMap.get("email"), is(email));
        assertThat(resultParamMap.get("firstName"), is(firstName));
        assertThat(resultParamMap.get("lastName"), is(lastName));
        assertThat(resultParamMap.get("page"), is(String.valueOf(page)));
    }

    @Test
    public void testGetCustomerSearchParametersWithEmptyValues() {

        String email = "      ";
        String firstName = "     ";
        String lastName = "     ";
        Integer page = -1;

        Map<String, String> resultParamMap = ParamsUtils.getCustomerSearchParams(email, firstName, lastName, page);

        assertNotNull(resultParamMap);
        assertThat(resultParamMap.size(), is(0));
    }

    @Test
    public void testGetCustomerSearchParametersWithNullValues() {

        Map<String, String> resultParamMap = ParamsUtils.getCustomerSearchParams(null, null, null, null);

        assertNotNull(resultParamMap);
        assertThat(resultParamMap.size(), is(0));
    }
}
