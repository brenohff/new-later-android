package later.brenohff.com.later.Models;

import java.io.Serializable;
import java.util.List;

import later.brenohff.com.later.Enums.EventStatus;

public class LTEvent implements Serializable {

    private Long id;
    private LTUser users;
    private List<LTCategory> categories;
    private List<LTUser> attendances;
    private List<LTUser> favorites;

    private String title;
    private String description;
    private EventStatus status;
    private String date;
    private String hour;
    private String locale;
    private String image;

    private Double price;
    private Double lat;
    private Double lon;

    private boolean isPrivate;

    // region ---------------- GETTERS AND SETTERS ----------------


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LTUser getUser() {
        return users;
    }

    public void setUser(LTUser users) {
        this.users = users;
    }

    public List<LTUser> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<LTUser> attendances) {
        this.attendances = attendances;
    }

    public List<LTUser> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<LTUser> favorites) {
        this.favorites = favorites;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LTUser getUsers() {
        return users;
    }

    public void setUsers(LTUser users) {
        this.users = users;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public List<LTCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<LTCategory> categories) {
        this.categories = categories;
    }

    //endregion
}
