package ch.heigvd.amt.wiki.dtos;

import java.util.Optional;

public record Length(Optional<Long> newLength, Optional<Long> oldLength) {}