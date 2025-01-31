package ch.heigvd.amt.db;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class StatsService {

    public record TopUser(Long count, String user, Boolean bot) {
    }

    public List<TopUser> topUsers() {
        // TODO
        return Collections.emptyList();
    }
}
