package net.helpscout.api;

import lombok.SneakyThrows;
import net.helpscout.api.cbo.ConversationType;
import net.helpscout.api.cbo.PersonType;
import net.helpscout.api.cbo.Status;
import net.helpscout.api.cbo.ThreadType;
import net.helpscout.api.model.Attachment;
import net.helpscout.api.model.Conversation;
import net.helpscout.api.model.Customer;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @Author: ivan
 * Date: 24.03.16
 * Time: 23:31
 */
public class ApiIntegrationTest {

    private static ApiClient client;

    /**
     * To setup tests need to pass your api key
     * use vm param -DApiKey=your_api_key
     */
    @BeforeClass
    public static void setUp() {
        client = ApiClient.getInstance();
        client.setKey(System.getProperty("ApiKey"));
    }


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

        // Delete test conversation and measure once again
        client.deleteConversation(testConversation.getId());
        assertThat(client.getConversationsForMailbox(workingMailBox).getCount(), equalTo(startConversationNumber));
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
}
