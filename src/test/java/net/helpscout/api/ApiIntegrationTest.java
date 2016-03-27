package net.helpscout.api;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.SneakyThrows;
import net.helpscout.api.cbo.*;
import net.helpscout.api.model.*;
import net.helpscout.api.model.ref.CustomerRef;
import net.helpscout.api.model.ref.MailboxRef;
import net.helpscout.api.model.ref.PersonRef;
import net.helpscout.api.model.ref.UserRef;
import net.helpscout.api.model.thread.ConversationThread;
import net.helpscout.api.model.thread.LineItem;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @Author: ivan
 * Date: 24.03.16
 * Time: 23:31
 *
 * Integration test that should ping as much endpoints of API as it can to validate that API is not outdated.
 * Rules to add endpoint to this test:
 * 1. Action done by endpoint should make 0 affect on testing account, so get requests should be used.
 * 2. All data affect actions should be reverted to initial state at end of test (e.g. createConversation-deleteConversation).
 * 3. It is not a unit test, it is ping-test, so it should be divided in some different tests, in case every test does
 * not rely on results of other test.
 */
public class ApiIntegrationTest {

    private static ApiClient client;

    /**
     * To setup tests need to pass your api key
     * use vm param -DApiKey=your_api_key
     */
    @BeforeClass
    public static void setUp() throws Exception {

        assertTrue("You did not specify API key. Use option -DApiKey=your_api_key to set api key for test.",
                System.getProperty("ApiKey") != null && !System.getProperty("ApiKey").isEmpty());

        client = ApiClient.getInstance();
        client.setKey(System.getProperty("ApiKey"));
    }

    /**
     * Integration test for checking mailbox endpoints
     * Used:
     * List Mailboxes
     * Get Mailbox
     * Get Folders
     */
    @Test
    @SneakyThrows
    public void testMailboxEndpoint() {
        long workingMailBox = client.getMailboxes().getItems().get(0).getId();
        assertThat(workingMailBox, greaterThan(0L));

        assertNotNull(client.getMailbox(workingMailBox));
        assertThat(client.getFolders(workingMailBox).getCount(), greaterThan(0));
    }

    /**
     * Integration test for checking user endpoints
     * Used:
     * List User
     * Get User
     * List Users By Mailbox
     */
    @Test
    @SneakyThrows
    public void testUserEndpoint() {
        long workingMailBox = client.getMailboxes().getItems().get(0).getId();
        assertThat(workingMailBox, greaterThan(0L));
        // At least we should have one user 'Me'
        assertThat(client.getUsers().getCount(), greaterThan(0));
        assertThat(client.getUsersForMailbox(workingMailBox).getCount(), greaterThan(0));
        User userMe = client.getUserMe();
        assertThat(userMe.getId(), equalTo(client.getUser(userMe.getId()).getId()));
    }

    /**
     * Integration test for checking tag endpoints
     * Used:
     * List tags
     */
    @Test
    @SneakyThrows
    public void testTagEndpoint() {
        long workingMailBox = client.getMailboxes().getItems().get(0).getId();
        assertThat(workingMailBox, greaterThan(0L));
        // tag 'samples' at least should be
        assertThat(client.getTags(null).getCount(), greaterThan(0));
    }


    /**
     * Integration test for creating of conversation
     * Ping next endpoint:
     * List Conversations by mailbox
     * List Customers
     * Create Conversation
     * Get Conversation
     * Update Conversation
     * Delete Conversation
     * Create Thread
     * Update Thread
     */
    @Test
    @SneakyThrows
    public void conversationCreateReadDeleteTest() {

        long customerId = -1;
        long workingMailBox = client.getMailboxes().getItems().get(0).getId();

        // Start with measure how many conversations do we have
        int startConversationNumber = client.getConversationsForMailbox(workingMailBox).getCount();

        // Find out Help Scout customer to create new conversation
        List<Customer> customers = client.getCustomers().getItems();
        for (Customer readCustomer: customers) {
            // seems like every account should have customer 'Help Scout'
            if ("Help".equals(readCustomer.getFirstName()) && "Scout".equals(readCustomer.getLastName())) {
                customerId = readCustomer.getId();
                break;
            }
        }

        // Create test conversation
        // If operation is successful that id of new conversation will be placed in Conversation object
        Conversation testConversation = getTestConversation(workingMailBox, client.getUserMe().getId(), customerId);
        client.createConversation(testConversation);
        assertNotNull(testConversation.getId());

        // Measure that there are one more conversation in our mailbox
        assertThat(client.getConversationsForMailbox(workingMailBox).getCount(), equalTo(startConversationNumber + 1));

        // Lets update conversation
        testConversation.setSubject("It is updated!");
        client.updateConversation(testConversation);

        // Get conversation in separate object
        Conversation readConversation = client.getConversation(testConversation.getId());
        assertNotNull(readConversation);
        assertThat(readConversation.getSubject(), is("It is updated!"));

        // Create thread
        ConversationThread newCreatedThread = getConversationThread(customerId);
        client.createConversationThread(readConversation.getId(), newCreatedThread);
        int numberOfThreadBefore = readConversation.getThreadCount();
        readConversation = client.getConversation(testConversation.getId());
        assertThat(readConversation.getThreadCount(), equalTo(numberOfThreadBefore + 1));

        // Update Thread
        ConversationThread thread = (ConversationThread)readConversation.getThreads().get(0);
        client.updateConversationThreadText(testConversation.getId(), thread.getId(), "New Text");

        readConversation = client.getConversation(testConversation.getId());
        thread = (ConversationThread)readConversation.getThreads().get(0);
        assertThat(thread.getBody(), equalTo("New Text"));

        // Search conversation
//        Page<SearchConversation> findConversation = client.searchConversations("(subject:\"It is updated!\")", null, null, 1);

        // Delete test conversation and measure once again
        client.deleteConversation(testConversation.getId());
        assertThat(client.getConversationsForMailbox(workingMailBox).getCount(), equalTo(startConversationNumber));
    }

    /**
     * Test to ping attachment endpoints.
     * Set to @Ingnore, cause there is no way to delete created attachment at current api implementation.
     */
    @Test
    @Ignore
    @SneakyThrows
    public void testCreateDeleteAttachment() {
        // Create attachment
        Attachment attachment = new Attachment();
        attachment.setData("Test data");
        attachment.setFileName("http://developer.helpscout.net/img/logo.png");
        attachment.setMimeType("image/jpeg");
        client.createAttachment(attachment);
        // As result of request for attachment creating will be hash code
        assertThat(attachment.getHash(), not(isEmptyOrNullString()));

        // Delete attachment
        // can not be used due to missed id in returned object
    }


    private Conversation getTestConversation(long mailboxRef, long assignedToRef, long customerRef) {
        // The mailbox associated with the conversation
        MailboxRef mailbox = new MailboxRef();
        mailbox.setId(mailboxRef);

        // The customer associated with the conversation
        CustomerRef customer = new CustomerRef();
        customer.setId(customerRef);
        customer.setEmail("customer@example.com");
        customer.setPhone("800-555-1212");

        Conversation conversation = new Conversation();
        conversation.setSubject("I need help!"); // Required
        conversation.setMailbox(mailbox); // Required
        conversation.setCustomer(customer); // Required
        conversation.setType(ConversationType.Email); // Not required - defaults to email

        // A conversation must have at least one thread
        ConversationThread thread = new net.helpscout.api.model.thread.Customer();
        thread.setType(ThreadType.Customer); // Required
        thread.setBody("Hello. I need some help."); // Required
        thread.setStatus(Status.Active); // Required
        thread.setCreatedAt(new Date()); // Not required - defaults to current UTC time

        // Created by: required
        PersonRef createdBy = new CustomerRef();
        createdBy.setId(customerRef);
        createdBy.setType(PersonType.Customer);
        thread.setCreatedBy(createdBy); // Required

        // Assigned to: not required - defaults to 'anyone'
        UserRef assignedTo = new UserRef();
        assignedTo.setId(assignedToRef);
        thread.setAssignedTo(assignedTo);

        // Cc: list of emails to Cc
        List<String> ccList = new ArrayList<String>();
        ccList.add("foo@example.com");
        thread.setCcList(ccList);

        // Bcc: list of emails to Bcc
        List<String> bccList = new ArrayList<String>();
        bccList.add("bar@example.com");
        thread.setBccList(bccList);

        // Attachments: attachments must be sent to the API before they can
        // be used when creating a thread. Use the hash value returned when
        // creating the attachment to associate it with a ticket.
        List<Attachment> attachments = new ArrayList<Attachment>();

        Attachment attachment = new Attachment();
        attachment.setHash("j894hg93gh9egh934gh34g8hjhvbdjvhbweg3");
        attachments.add(attachment);
        thread.setAttachments(attachments);

        List<LineItem> threads = new ArrayList<LineItem>();
        threads.add((LineItem)thread);
        conversation.setThreads(threads);

        return conversation;
    }

    private ConversationThread getConversationThread(long customerRef) {
        // A conversation must have at least one thread
        ConversationThread thread = new net.helpscout.api.model.thread.Customer();
        thread.setType(ThreadType.Customer); // Required
        thread.setBody("New thread info."); // Required
        thread.setStatus(Status.Active); // Required
        thread.setCreatedAt(new Date()); // Not required - defaults to current UTC time

        // Created by: required
        PersonRef createdBy = new CustomerRef();
        createdBy.setId(customerRef);
        createdBy.setType(PersonType.Customer);
        thread.setCreatedBy(createdBy); // Required

        return thread;
    }
}
