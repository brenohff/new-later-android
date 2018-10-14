package later.brenohff.com.later.Models;

import java.io.Serializable;

import later.brenohff.com.later.Enums.UserType;

public class LTUser implements Serializable {

    private String id;
    private String image;
    private String image_long;
    private String name;
    private String birthday;
    private String email;
    private String gender;
    private String link;

    private UserType userType;

    public LTUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_long() {
        return image_long;
    }

    public void setImage_long(String image_long) {
        this.image_long = image_long;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
