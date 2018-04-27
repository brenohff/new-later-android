package later.brenohff.com.later.Models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class LTCategory implements Serializable {

    private Long id;
    private Set<LTEvent> events = new HashSet<>();
    private String url;
    private String name;
    private String baseColor;
    private String baseColor700;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<LTEvent> getEvents() {
        return events;
    }

    public void setEvents(Set<LTEvent> events) {
        this.events = events;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(String baseColor) {
        this.baseColor = baseColor;
    }

    public String getBaseColor700() {
        return baseColor700;
    }

    public void setBaseColor700(String baseColor700) {
        this.baseColor700 = baseColor700;
    }
}
