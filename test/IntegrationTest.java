import commons.AbstractIntegrationTest;
import org.junit.Test;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class IntegrationTest extends AbstractIntegrationTest {

    private static final Logger logger = getLogger(IntegrationTest.class);

    /**
     * add your integration test here
     * in this example we just check if the welcome page is being shown
     */
    @Test
    public void test() {
        /*
        running(testServer(3333, fakeApplication(database)), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333");
            assertThat(browser.pageSource(), containsString("Add Person"));
        });
        assertTrue(true);
        */
    }

}
