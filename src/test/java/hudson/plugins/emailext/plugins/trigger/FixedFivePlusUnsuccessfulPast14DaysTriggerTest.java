package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.FreeStyleBuild;
import hudson.model.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.stapler.DataBoundConstructor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jagte on 2/12/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractBuild.class)
public class FixedFivePlusUnsuccessfulPast14DaysTriggerTest extends  TriggerTestBase {

    @Test
    public void test_successDoesNotTriggerNotification() throws IOException, InterruptedException {
        assertNotTriggered(Result.SUCCESS);
    }

    @Test
    public void test_oneRecentUnsuccessfulBuildsDoesNotTriggerNotification() {
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(randomUnsuccessfulState());
        assertFalse(newInstance().trigger(build, getTaskListener()));
    }

    @Test
    public void test_ThreeUnsuccessfulThenOneSuccessfulAllRecentDoesNotTriggerNotification() {
        final int numBuilds = 4;
        final Result[] results = createUnsuccessfulResultArray(numBuilds);
        results[3] = Result.SUCCESS;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(results);
        setGetStartTimeInMillis(build, createJustHappenedBuildTimeArray(numBuilds));
        FixedFivePlusUnsuccessfulPast14DaysTrigger trigger = newInstance();
        assertFalse(trigger.trigger(build, getTaskListener()));
    }


    @Test
    public void test_fiveUnsuccessfulBuildsNotConsecutiveThenOneSuccessfulDoesTriggerNotification() {
        final int numBuilds = 7;
        Result [] results = createUnsuccessfulResultArray(numBuilds);
        results[3] = Result.SUCCESS;
        results[6] = Result.SUCCESS;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(results);
        setGetStartTimeInMillis(build, createJustHappenedBuildTimeArray(numBuilds));
        FixedFivePlusUnsuccessfulPast14DaysTrigger trigger = newInstance();
        assertTrue(trigger.trigger(build, getTaskListener()));
    }

    @Test
    public void test_fiveNotConsecutiveUnsuccessfulBuildsThenTwoSuccessfulDoesNotTriggerNotification() {
        final int numBuilds = 8;
        Result [] results = createUnsuccessfulResultArray(numBuilds);
        results[4] = Result.SUCCESS;
        results[6] = Result.SUCCESS;
        results[7] = Result.SUCCESS;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(results);
        setGetStartTimeInMillis(build, createJustHappenedBuildTimeArray(numBuilds));
        FixedFivePlusUnsuccessfulPast14DaysTrigger trigger = newInstance();
        assertFalse(trigger.trigger(build, getTaskListener()));
    }

    @Test
    public void
    test_fiveNotConsecutiveUnsuccessfulBuildsButOneIsBefore14DayMarkThenOneSuccessful_DoesNotTriggerNotification() {
        final int numBuilds = 8;
        Result [] results = createUnsuccessfulResultArray(numBuilds);
        results[4] = Result.SUCCESS;
        results[6] = Result.SUCCESS;
        results[7] = Result.SUCCESS;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(results);
        long [] buildTimes = createJustHappenedBuildTimeArray(numBuilds);
        buildTimes[0] = olderThanThreshold();
        setGetStartTimeInMillis(build, buildTimes);
        FixedFivePlusUnsuccessfulPast14DaysTrigger trigger = newInstance();
        assertFalse(trigger.trigger(build, getTaskListener()));
    }


    @Test
    public void test_descriptorDisplayName(){
        assertEquals(FixedFivePlusUnsuccessfulPast14DaysTrigger.TRIGGER_NAME,
                new FixedFivePlusUnsuccessfulPast14DaysTrigger.DescriptorImpl().getDisplayName());
    }

    @Test
    public void test_descriptor_defaults_send_to(){
        final FixedFivePlusUnsuccessfulPast14DaysTrigger.DescriptorImpl descriptor =
                new FixedFivePlusUnsuccessfulPast14DaysTrigger.DescriptorImpl();
        assertFalse(descriptor.getDefaultSendToDevs());
        assertFalse(descriptor.getDefaultSendToCulprits());
        assertTrue(descriptor.getDefaultSendToList());
        assertTrue(descriptor.getDefaultSendToRequester());
    }

    @Override
    FixedFivePlusUnsuccessfulPast14DaysTrigger newInstance() {
        return FixedFivePlusUnsuccessfulPast14DaysTrigger.createDefault();
    }

    private long olderThanThreshold() {
        FixedFivePlusUnsuccessfulPast14DaysTrigger trigger = newInstance();
        return System.currentTimeMillis() -
                trigger.rules.DELTA_FOR_AGE_THRESHOLD - 1000;
    }

    private long justHappened() {
        return System.currentTimeMillis() - 1000;
    }

    /**
     * mock build using powermock
     */
    AbstractBuild<?, ?> mockBuildWithPowerMock(Result... resultHistory) {
        FreeStyleBuild toRet = mock(FreeStyleBuild.class);

        FreeStyleBuild build = toRet;
        for (int i = resultHistory.length - 1; i >= 0; i--) {
            when(build.getResult()).thenReturn(resultHistory[i]);

            if (i != 0) {
                FreeStyleBuild prevBuild = mock(FreeStyleBuild.class);
                when(build.getPreviousBuild()).thenReturn(prevBuild);
                build = prevBuild;
            }
        }

        return toRet;
    }

    void setGetStartTimeInMillis(AbstractBuild<?, ?> currentBuild, long... buildTimes) {
        AbstractBuild<?, ?> build = currentBuild;
        for (int i = buildTimes.length - 1; i >= 0; i--) {
            when(build.getStartTimeInMillis()).thenReturn(buildTimes[i]);

            if (i != 0) {
                build = build.getPreviousBuild();
            }
        }
    }

    private long [] createJustHappenedBuildTimeArray(int n){
        long [] buildTimes = new long[n];
        for(int i=0; i<n; i++){
            buildTimes[i] = justHappened();
        }
        return buildTimes;
    }

}
