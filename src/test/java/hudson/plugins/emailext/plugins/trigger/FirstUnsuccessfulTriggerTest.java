package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.model.Result;
import hudson.plugins.emailext.plugins.EmailTrigger;
import org.junit.Test;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class FirstUnsuccessfulTriggerTest extends  TriggerTestBase{

    @Test
    public void test_fail_success_fail() throws IOException, InterruptedException {
        assertTriggered(randomUnsuccessfulState(), Result.SUCCESS, randomUnsuccessfulState());
    }

    @Test
    public void test_second_build_first_unsuccessful() throws IOException, InterruptedException {
        assertTriggered(Result.SUCCESS, randomUnsuccessfulState());
    }

    @Test
    public void test_second_unsuccessful() throws IOException, InterruptedException {
        assertNotTriggered(randomUnsuccessfulState(), randomUnsuccessfulState());
    }

    @Test
    public void test_only_build_is_unsuccessful() throws IOException, InterruptedException {
        assertTriggered(randomUnsuccessfulState());
    }

    @Test
    public void test_descriptor_display_name(){
        assertEquals(FirstUnsuccessfulTrigger.TRIGGER_NAME,
                new FirstUnsuccessfulTrigger.DescriptorImpl().getDisplayName());
    }

    @Override
    EmailTrigger newInstance() {
        return FirstUnsuccessfulTrigger.createDefault();
    }
}
