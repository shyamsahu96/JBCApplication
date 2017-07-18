package citzen.jbc.myapplication.firebase;

/**
 * Created by shyam on 18-Jul-17.
 */

public class Feed {

    private String name, feed;

    public Feed() {

    }

    public Feed(String name, String feed) {
        this.name = name;
        this.feed = feed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }
}
