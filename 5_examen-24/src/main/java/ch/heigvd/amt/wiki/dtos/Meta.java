package ch.heigvd.amt.wiki.dtos;

import java.net.URI;
import java.time.Instant;

public record Meta(
        String domain,
        Instant dt,
        String id,
        String request_id,
        String stream,
        URI uri
) {}
