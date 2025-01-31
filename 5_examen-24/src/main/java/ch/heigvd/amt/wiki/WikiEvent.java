package ch.heigvd.amt.wiki;

public record WikiEvent(String id, String type, String title, String url) {
};
