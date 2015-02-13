package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.model.Result;
import hudson.plugins.emailext.plugins.EmailTrigger;
import org.junit.Test;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

/**
 * Created by jagte on 2/12/15.
 */
public class FixedThreePlusConsecutiveUnsuccessfulTriggerTest extends  TriggerTestBase {

    @Test
    public void test_successDoesNotTriggerNotification()
            throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS);
    }

    @Test
    public void test_twoUnsuccessfulDoNotTrigger()
            throws IOException, InterruptedException {
        assertNotTriggered(randomUnsuccessfulState(), randomUnsuccessfulState());
    }

    @Test
    public void test_twoUnsuccessfulThenOneSuccessfulDoNOTTrigger()
            throws IOException, InterruptedException {
        assertNotTriggered(randomUnsuccessfulState(), randomUnsuccessfulState(),
                Result.SUCCESS);
    }

    @Test
    public void test_oneSuccessAndTwoUnSuccessfulThenOneSuccessfulDoNotTrigger()
            throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS, randomUnsuccessfulState(),
                randomUnsuccessfulState(),Result.SUCCESS);
    }

    @Test
    public void test_ThreeUnsuccessfulThenOneSuccessDoesTrigger()
            throws IOException, InterruptedException {
        assertTriggered(randomUnsuccessfulState(), randomUnsuccessfulState(),
                randomUnsuccessfulState(), Result.SUCCESS);
    }

    @Test
    public void test_ThreeUnsuccessfulThenTwoSuccessDoesNotTrigger()
            throws IOException, InterruptedException {
        assertNotTriggered(randomUnsuccessfulState(), randomUnsuccessfulState(),
                randomUnsuccessfulState(), Result.SUCCESS, Result.SUCCESS);
    }

    @Test
    public void test_descriptorDisplayName(){
        assertEquals(FixedThreePlusConsecutiveUnsuccessfulTrigger.TRIGGER_NAME,
                new FixedThreePlusConsecutiveUnsuccessfulTrigger.DescriptorImpl().getDisplayName());
    }

    @Override
    EmailTrigger newInstance() {
        return FixedThreePlusConsecutiveUnsuccessfulTrigger.createDefault();
    }

}
