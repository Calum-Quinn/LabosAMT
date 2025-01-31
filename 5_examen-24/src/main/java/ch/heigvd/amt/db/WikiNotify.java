package ch.heigvd.amt.db;

import jakarta.persistence.Entity;

@Entity
public class WikiNotify extends WikiRecentChange{
    String notify_url;

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }
}
