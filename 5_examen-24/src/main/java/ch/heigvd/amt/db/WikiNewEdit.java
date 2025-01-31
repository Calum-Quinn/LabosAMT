package ch.heigvd.amt.db;

import jakarta.persistence.Entity;

@Entity
public class WikiNewEdit extends WikiNotify {

    boolean minor;
    boolean patrolled;
    long revision_old;
    long revision_new;
    long length_old;
    long length_new;

    public boolean isMinor() {
        return minor;
    }

    public void setMinor(boolean minor) {
        this.minor = minor;
    }

    public boolean isPatrolled() {
        return patrolled;
    }

    public void setPatrolled(boolean patrolled) {
        this.patrolled = patrolled;
    }

    public long getRevision_old() {
        return revision_old;
    }

    public void setRevision_old(long revision_old) {
        this.revision_old = revision_old;
    }

    public long getRevision_new() {
        return revision_new;
    }

    public void setRevision_new(long revision_new) {
        this.revision_new = revision_new;
    }

    public long getLength_old() {
        return length_old;
    }

    public void setLength_old(long length_old) {
        this.length_old = length_old;
    }

    public long getLength_new() {
        return length_new;
    }

    public void setLength_new(long length_new) {
        this.length_new = length_new;
    }
}
