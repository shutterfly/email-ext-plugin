package hudson.plugins.emailext.plugins.trigger;

import hudson.model.Result;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.util.XStream2;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class FirstUnstableTriggerTest extends TriggerTestBase {

    @Override
    EmailTrigger newInstance() {
        return new FirstUnstableTrigger(true, true, true, false, "", "", "", "", "", 0, "project");
    }

    @Test
    public void testNotTriggeredBySuccess() throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS);
    }

    @Test
    public void testNotTriggeredByFailure() throws IOException, InterruptedException {
        assertNotTriggered(Result.FAILURE);
    }

    @Test
    public void testNotTriggeredByMultipleSuccess() throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS, Result.SUCCESS, Result.SUCCESS);
    }

    @Test
    public void testNotTriggeredByMultipleFailure() throws IOException, InterruptedException {
        assertNotTriggered(Result.FAILURE, Result.FAILURE, Result.FAILURE);
    }

    @Test
    public void testIsTriggeredByFirstUnstableAfterSuccess() throws IOException, InterruptedException {
        assertTriggered(Result.SUCCESS, Result.UNSTABLE);
        assertTriggered(Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE, Result.SUCCESS, Result.UNSTABLE);
    }

    @Test
    public void testNotTriggeredBySecondUnstableAfterSuccess() throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS, Result.UNSTABLE, Result.UNSTABLE);
    }

    @Test
    public void testIsTriggeredByFirstBuildUnstable() throws IOException, InterruptedException {
        assertTriggered(Result.UNSTABLE);
    }

    @Test
    public void testNotTriggeredByMultipleBuildsUnstable() throws IOException, InterruptedException {
        assertNotTriggered(Result.UNSTABLE, Result.UNSTABLE);
        assertNotTriggered(Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE);
    }

    @Test
    public void testIsTriggeredByFirstUnstableAfterFailure() throws IOException, InterruptedException {
        assertTriggered(Result.FAILURE, Result.UNSTABLE);
        assertTriggered(Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE, Result.FAILURE, Result.UNSTABLE);
    }

    @Test
    public void testIsNotTriggeredByNull() throws IOException, InterruptedException {
        assertNotTriggered(null);

    }
}
