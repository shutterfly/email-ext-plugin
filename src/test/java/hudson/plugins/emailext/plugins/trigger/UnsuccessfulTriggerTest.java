package hudson.plugins.emailext.plugins.trigger;

import hudson.model.Result;
import hudson.plugins.emailext.plugins.EmailTrigger;
import org.junit.Test;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class UnsuccessfulTriggerTest extends  TriggerTestBase{
    @Test
    public void test_success_does_not_trigger_notification() throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS);
    }

    @Test
    public void test_unstable_does_trigger_notification() throws IOException, InterruptedException {
        assertTriggered(Result.UNSTABLE);
    }

    @Test
    public void test_aborted_does_trigger_notification() throws IOException, InterruptedException {
        assertTriggered(Result.ABORTED);
    }

    @Test
    public void test_failed_does_trigger_notification() throws IOException, InterruptedException {
        assertTriggered(Result.FAILURE);
    }

    @Test
    public void test_descriptor_has_extension_annotation(){
        assertEquals(UnsuccessfulTrigger.TRIGGER_NAME,
                new UnsuccessfulTrigger.DescriptorImpl().getDisplayName());
    }

    @Test
    public void test_descriptor_display_name(){
        assertEquals(UnsuccessfulTrigger.TRIGGER_NAME,
                new UnsuccessfulTrigger.DescriptorImpl().getDisplayName());
    }

    @Test
    public void test_descriptor_defaults_send_to(){
        final UnsuccessfulTrigger.DescriptorImpl descriptor =
                new UnsuccessfulTrigger.DescriptorImpl();
        assertFalse(descriptor.getDefaultSendToCulprits());
        assertTrue(descriptor.getDefaultSendToDevs());
        assertFalse(descriptor.getDefaultSendToCulprits());
        assertTrue(descriptor.getDefaultSendToList());
        assertTrue(descriptor.getDefaultSendToRequester());
    }

    @Test
    public void test_databound_constructor(){
        final Constructor<?>[] constructors = UnsuccessfulTrigger.class.getConstructors();
        boolean foundOne = false;
        for(Constructor<?> constructor : constructors){
            if(constructor.getAnnotation(DataBoundConstructor.class) != null)
                foundOne = true;
        }
        assertTrue(foundOne);
    }

    @Override
    EmailTrigger newInstance() {
        return new UnsuccessfulTrigger(true, true, true, false, "", "", "", "", "", 0, "project");
    }
}
