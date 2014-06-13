package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.plugins.emailext.plugins.EmailTriggerDescriptor;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class TriggerBasicFunctionalityTest {

    public static Object[] trigger_class_params() throws ClassNotFoundException, IOException {
        URL triggerClassUrl = UnsuccessfulTrigger.class.getResource("UnsuccessfulTrigger.class");
        File path = new File(triggerClassUrl.getPath()).getParentFile();
        File [] classFiles = path.listFiles();

        Collection<Object> data = new ArrayList<Object>();
        assert classFiles != null;
        for (File classFile : classFiles){
            addTriggerClassToData(data, classFile);
        }
        return data.toArray();
    }

    public static void addTriggerClassToData(Collection<Object> data, File classFile) throws ClassNotFoundException {
        String className = classFile.getName();
        if(className.endsWith("Trigger")) {
            final String fullClassName = "hudson.plugins.emailext.plugins.trigger." +
                    className;
            final Class aClass = Class.forName(fullClassName);
            if (!Modifier.isAbstract(aClass.getModifiers()))
                data.add(JUnitParamsRunner.$(aClass));
        }
    }

    @Test
    @Parameters(method="trigger_class_params")
    public void test_descriptor_defaults_send_to(Class triggerClass) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        final EmailTriggerDescriptor descriptor = getDescriptorInstance(triggerClass);
        assertFalse(descriptor.getDefaultSendToDevs());
        assertFalse(descriptor.getDefaultSendToCulprits());
        assertTrue(descriptor.getDefaultSendToList());
        assertTrue(descriptor.getDefaultSendToRequester());
    }

    public EmailTriggerDescriptor getDescriptorInstance(Class triggerClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final String fullClassName = triggerClass.getCanonicalName() + "$DescriptorImpl";
        final Class aClass = Class.forName(fullClassName);
        return (EmailTriggerDescriptor)aClass.newInstance();
    }


    @Test
    @Parameters(method="trigger_class_params")
    public void test_databound_constructor(Class triggerClass){
        final Constructor<?>[] constructors = triggerClass.getConstructors();
        boolean foundOne = false;
        for(Constructor<?> constructor : constructors){
            if(constructor.getAnnotation(DataBoundConstructor.class) != null)
                foundOne = true;
        }
        assertTrue(foundOne);
    }

    @Test
    @Parameters(method="trigger_class_params")
    public void test_descriptor_has_extension_annotation(Class triggerClass) throws
            InstantiationException, IllegalAccessException, ClassNotFoundException {
        assertNotNull(
                getDescriptorInstance(triggerClass).getClass().getAnnotation(Extension.class));
    }

}
