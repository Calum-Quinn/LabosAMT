package ch.heigvd.amt.wiki.dtos;

import java.util.Optional;

public record Revision(Optional<Long> newRevision, Optional<Long> oldRevision) {}
