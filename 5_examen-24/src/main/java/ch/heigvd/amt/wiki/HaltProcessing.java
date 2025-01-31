package ch.heigvd.amt.wiki;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Processors marked with this annotation will cause the service to halt events processing if
 * an exception is thrown by the Processor.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HaltProcessing {
}
