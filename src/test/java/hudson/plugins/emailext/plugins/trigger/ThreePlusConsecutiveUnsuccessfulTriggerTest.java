package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.model.Result;
import hudson.plugins.emailext.plugins.EmailTrigger;
import org.junit.Test;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class ThreePlusConsecutiveUnsuccessfulTriggerTest extends  TriggerTestBase {

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
    public void test_threeUnsuccessfulDoTrigger()
            throws IOException, InterruptedException {
        assertTriggered(randomUnsuccessfulState(), randomUnsuccessfulState(),
                randomUnsuccessfulState());
    }

    @Test
    public void test_oneSuccessAndTwoUnSuccessfulDoNotTrigger()
            throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS, randomUnsuccessfulState(), randomUnsuccessfulState());
    }

    @Test
    public void test_ThreeUnsuccessfulThenOneSuccessDoesNotTrigger()
            throws IOException, InterruptedException {
        assertNotTriggered(randomUnsuccessfulState(), randomUnsuccessfulState(),
                randomUnsuccessfulState(), Result.SUCCESS);
    }

    @Test
    public void test_forthUnsuccessfulDoesTrigger()
            throws IOException, InterruptedException {
        assertTriggered(randomUnsuccessfulState(), randomUnsuccessfulState(),
                randomUnsuccessfulState(), randomUnsuccessfulState());
    }

    @Test
    public void test_descriptorDisplayName(){
        assertEquals(ThreePlusConsecutiveUnsuccessfulTrigger.TRIGGER_NAME,
                new ThreePlusConsecutiveUnsuccessfulTrigger.DescriptorImpl().getDisplayName());
    }

    @Override
    EmailTrigger newInstance() {
        return ThreePlusConsecutiveUnsuccessfulTrigger.createDefault();
    }

}
