package net.helpscout.api.cbo;

/**
 * @Author: ivan
 * Date: 23.07.16
 * Time: 0:28
 */
public enum WebhookEventType {

    Assigned(1, "convo.assigned"),
    Created(2, "convo.created"),
    Deleted(3, "convo.deleted"),
    Merged(4, "convo.merged"),
    Moved(5, "convo.moved"),
    StatusUpdated(6, "convo.status"),
    TagsUpdated(7, "convo.tags"),
    CustomerReplyCreated(8, "convo.customer.reply.created"),
    AgentReplyCreated(9, "convo.agent.reply.create"),
    NoteCreated(10, "convo.note.created"),
    CustomerCreated(11, "customer.created"),
    RatingReceived(12, "satisfaction.ratings");

    private final int value;
    private final String label;

    private WebhookEventType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    private boolean isEventTypeOf(String eventType) {
        String event = this.getLabel();
        if (event != null) {
            return event.substring(0, eventType.length()).equals(eventType);
        }
        return false;
    }

    public boolean isTestEvent() {
        return "helpscout.test".equals(this.getLabel());
    }

    /**
     * Is the current event a type of conversation event
     * @return boolean
     */
    public boolean isConversationEvent() {
        return this.isEventTypeOf("convo");
    }

    /**
     * Is the current event a type of customer event
     * @return boolean
     */
    public boolean isCustomerEvent() {
        return this.isEventTypeOf("customer");
    }

    public static WebhookEventType findByValue(Integer value) {
        for (WebhookEventType item : WebhookEventType.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public static WebhookEventType findByLabel(String label) {
        for (WebhookEventType item : WebhookEventType.values()) {
            if (item.getLabel().equalsIgnoreCase(label)) {
                return item;
            }
        }
        return null;
    }
}
