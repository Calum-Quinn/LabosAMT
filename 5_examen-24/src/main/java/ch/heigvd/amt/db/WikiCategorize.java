package ch.heigvd.amt.db;

import jakarta.persistence.Entity;

@Entity
public class WikiCategorize extends WikiNotify {

    String parsedcomment;


    public String getParsedcomment() {
        return parsedcomment;
    }

    public void setParsedcomment(String parsedcomment) {
        this.parsedcomment = parsedcomment;
    }


}
