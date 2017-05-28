package citzen.jbc.myapplication;

/**
 * Created by shyam on 28-May-17.
 */

public class Replies {

    public String photoUrl, name, message;

    public Replies(String photoUrl, String name, String message) {
        this.photoUrl = photoUrl;
        this.name = name;
        this.message = message;
    }

    public Replies() {
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
