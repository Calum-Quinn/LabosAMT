package ch.heigvd.amt.render;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Used to render the images representing bets
 */
@ApplicationScoped
public class HtmlRenderer implements AutoCloseable {
    private final Playwright playwright;
    private final Browser browserInstance;

    public HtmlRenderer() {
        playwright = Playwright.create();
        browserInstance = playwright.chromium().launch();
    }

    public InputStream render(String html) {
        // Create a new page
        synchronized (this) {
            Page page = browserInstance.newPage();
            page.setContent(html);

            return new ByteArrayInputStream(page.screenshot());
        }
    }

    public InputStream renderEmbed(String html) {
        synchronized (this) {
            Page page = browserInstance.newPage();
            page.setContent(html);
            page.evaluate("document.body.style.zoom = '2'");
            page.waitForFunction("document.fonts.ready");

            Locator.ScreenshotOptions options = new Locator.ScreenshotOptions();
            return new ByteArrayInputStream(page.locator("#embed").screenshot(options));
        }
    }

    @Override
    public void close() throws Exception {
        playwright.close();
    }
}
