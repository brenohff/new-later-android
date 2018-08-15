package later.brenohff.com.later.Models;

import java.io.Serializable;
import java.util.Date;

public class LTChat implements Serializable {

    private Long id;
    private MessageType type;
    private String content;
    private String eventId;
    private Date dtPost;
    private LTUser users;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    //region GETTES AND SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Date getDtPost() {
        return dtPost;
    }

    public void setDtPost(Date dtPost) {
        this.dtPost = dtPost;
    }

    public LTUser getUser() {
        return users;
    }

    public void setUser(LTUser user) {
        this.users = users;
    }

    //endregion
}
