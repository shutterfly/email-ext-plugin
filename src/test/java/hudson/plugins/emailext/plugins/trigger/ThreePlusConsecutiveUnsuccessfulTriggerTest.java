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
    public void test_descriptorHasExtensionAnnotation(){
        assertNotNull(ThreePlusConsecutiveUnsuccessfulTrigger.DescriptorImpl.class.getAnnotation(Extension.class));
    }

    @Test
    public void test_descriptorDisplayName(){
        assertEquals(ThreePlusConsecutiveUnsuccessfulTrigger.TRIGGER_NAME,
                new ThreePlusConsecutiveUnsuccessfulTrigger.DescriptorImpl().getDisplayName());
    }

    @Test
    public void test_dataBoundConstructor(){
        final Constructor<?>[] constructors = ThreePlusConsecutiveUnsuccessfulTrigger.class.getConstructors();
        boolean foundOne = false;
        for(Constructor<?> constructor : constructors){
            if(constructor.getAnnotation(DataBoundConstructor.class) != null)
                foundOne = true;
        }
        assertTrue(foundOne);
    }

    @Test
    public void test_descriptor_defaults_send_to(){
        final ThreePlusConsecutiveUnsuccessfulTrigger.DescriptorImpl descriptor =
                new ThreePlusConsecutiveUnsuccessfulTrigger.DescriptorImpl();
        assertTrue(descriptor.getDefaultSendToDevs());
        assertFalse(descriptor.getDefaultSendToCulprits());
        assertTrue(descriptor.getDefaultSendToList());
        assertTrue(descriptor.getDefaultSendToRequester());
    }

    @Override
    EmailTrigger newInstance() {
        return ThreePlusConsecutiveUnsuccessfulTrigger.createDefault();
    }

}
