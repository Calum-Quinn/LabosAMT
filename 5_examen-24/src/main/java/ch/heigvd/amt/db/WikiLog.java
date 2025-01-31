package ch.heigvd.amt.db;

import jakarta.persistence.Entity;

@Entity
public class WikiLog extends WikiRecentChange {

    long log_id;
    long log_type;
    long action;
    long action_comment;

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public long getLog_type() {
        return log_type;
    }

    public void setLog_type(long log_type) {
        this.log_type = log_type;
    }

    public long getAction() {
        return action;
    }

    public void setAction(long action) {
        this.action = action;
    }

    public long getAction_comment() {
        return action_comment;
    }

    public void setAction_comment(long action_comment) {
        this.action_comment = action_comment;
    }
}
