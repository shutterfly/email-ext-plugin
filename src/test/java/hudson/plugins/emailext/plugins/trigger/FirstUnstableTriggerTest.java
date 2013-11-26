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
    public void testTrigger_success() throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS);
    }

    @Test
    public void testTrigger_multipleSuccess() throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS, Result.SUCCESS, Result.SUCCESS);
    }

    @Test
    public void testTrigger_firstUnstableAfterSuccess() throws IOException, InterruptedException {
        assertTriggered(Result.SUCCESS, Result.UNSTABLE);
        assertTriggered(Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE, Result.SUCCESS, Result.UNSTABLE);
    }

    @Test
    public void testTrigger_secondFailureAfterSuccess() throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS, Result.UNSTABLE, Result.UNSTABLE);
    }

    @Test
    public void testTrigger_firstBuildFails() throws IOException, InterruptedException {
        assertTriggered(Result.UNSTABLE);
    }

    @Test
    public void testTrigger_firstTwoBuildsFail() throws IOException, InterruptedException {
        assertNotTriggered(Result.UNSTABLE, Result.UNSTABLE);
    }

    @Test
    public void testTrigger_firstUnstableAfterFailure() throws IOException, InterruptedException {
        assertTriggered(Result.FAILURE, Result.UNSTABLE);
        assertTriggered(Result.UNSTABLE, Result.UNSTABLE, Result.UNSTABLE, Result.FAILURE, Result.UNSTABLE);
    }

    @Test
    public void testUpgrade() throws IOException, InterruptedException {

        XStream2 xs = new XStream2();
        InputStream is = FirstUnstableTriggerTest.class.getResourceAsStream("oldformatunstable.xml");
        FirstUnstableTrigger t = (FirstUnstableTrigger) xs.fromXML(is);
        assertEquals(t.unstableCount, 1);
    }
}
