package citzen.jbc.myapplication.firebase;

/**
 * Created by shyam on 17-Jun-17.
 */

public class Recents {

    private String message;
    private String photoLink;

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    private String dataKey;

    public Recents() {
    }

    public Recents(String message, String photoLink) {
        this.message = message;
        this.photoLink = photoLink;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}
