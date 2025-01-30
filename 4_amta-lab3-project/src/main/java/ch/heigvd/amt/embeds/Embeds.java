package ch.heigvd.amt.embeds;


import ch.heigvd.amt.beans.BetDTO;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

/**
 * Represents a bet in the discord interface
 */
@CheckedTemplate
public class Embeds {
    public static native TemplateInstance bet(BetDTO bet);
}