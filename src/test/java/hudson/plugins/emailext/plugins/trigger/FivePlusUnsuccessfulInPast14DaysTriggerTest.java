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

@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractBuild.class)
public class FivePlusUnsuccessfulInPast14DaysTriggerTest extends  TriggerTestBase{

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
    public void test_OneSuccessfulThenThreeUnsuccessfulAllRecentDoesNotTriggerNotification() {
        final int numBuilds = 4;
        final Result[] results = createUnsuccessfulResultArray(numBuilds);
        results[0] = Result.SUCCESS;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(results);
        setGetStartTimeInMillis(build, createJustHappenedBuildTimeArray(numBuilds));
        FivePlusUnsuccessfulInPast14DaysTrigger trigger = newInstance();
        assertFalse(trigger.trigger(build, getTaskListener()));
    }

    @Test
    public void test_fiveRecentUnsuccessfulBuildsDoesTriggerNotification() {
        final int numBuilds = 5;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(createUnsuccessfulResultArray(numBuilds));
        setGetStartTimeInMillis(build, createJustHappenedBuildTimeArray(numBuilds));
        FivePlusUnsuccessfulInPast14DaysTrigger trigger = newInstance();
        assertTrue(trigger.trigger(build, getTaskListener()));
    }

    @Test
    public void test_sixRecentUnsuccessfulBuildsDoesTriggerNotification() {
        final int numBuilds = 6;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(createUnsuccessfulResultArray(numBuilds));
        setGetStartTimeInMillis(build, createJustHappenedBuildTimeArray(numBuilds));
        FivePlusUnsuccessfulInPast14DaysTrigger trigger = newInstance();
        assertTrue(trigger.trigger(build, getTaskListener()));
    }

    @Test
    public void
    test_fiveRecentUnsuccessfulBuildsButOneIsBefore14DayMark_DoesNotTriggerNotification() {
        final int numBuilds = 5;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(createUnsuccessfulResultArray(numBuilds));
        long [] buildTimes = createJustHappenedBuildTimeArray(numBuilds);
        buildTimes[0] = olderThanThreshold();
        setGetStartTimeInMillis(build, buildTimes);
        FivePlusUnsuccessfulInPast14DaysTrigger trigger = newInstance();
        assertFalse(trigger.trigger(build, getTaskListener()));
    }

    @Test
    public void
    test_fiveRecentUnsuccessfulBuildsWithAFewSuccessfulBuilds_DoesTriggerNotification() {
        final int numBuilds = 7;
        Result [] results = createUnsuccessfulResultArray(numBuilds);
        results[2] = Result.SUCCESS;
        results[4] = Result.SUCCESS;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(results);
        setGetStartTimeInMillis(build, createJustHappenedBuildTimeArray(numBuilds));
        FivePlusUnsuccessfulInPast14DaysTrigger trigger = newInstance();
        assertTrue(trigger.trigger(build, getTaskListener()));
    }

    @Test
    public void
    test_fiveRecentUnsuccessfulBuildsButMostRecentBuildPassed_DoesNotTriggerNotification() {
        final int numBuilds = 6;
        Result [] results = createUnsuccessfulResultArray(numBuilds);
        results[numBuilds-1] = Result.SUCCESS;
        AbstractBuild<?, ?> build =  mockBuildWithPowerMock(results);
        setGetStartTimeInMillis(build, createJustHappenedBuildTimeArray(numBuilds));
        FivePlusUnsuccessfulInPast14DaysTrigger trigger = newInstance();
        assertFalse(trigger.trigger(build, getTaskListener()));
    }

    @Test
    public void test_descriptorHasExtensionAnnotation(){
        assertNotNull(FivePlusUnsuccessfulInPast14DaysTrigger.DescriptorImpl.class.getAnnotation(
                Extension.class));
    }

    @Test
    public void test_descriptorDisplayName(){
        assertEquals(FivePlusUnsuccessfulInPast14DaysTrigger.TRIGGER_NAME,
                new FivePlusUnsuccessfulInPast14DaysTrigger.DescriptorImpl().getDisplayName());
    }

    @Test
    public void test_dataBoundConstructor(){
        final Constructor<?>[] constructors =
                FivePlusUnsuccessfulInPast14DaysTrigger.class.getConstructors();
        boolean foundOne = false;
        for(Constructor<?> constructor : constructors){
            if(constructor.getAnnotation(DataBoundConstructor.class) != null)
                foundOne = true;
        }
        assertTrue(foundOne);
    }

    @Override
    FivePlusUnsuccessfulInPast14DaysTrigger newInstance() {
        return new FivePlusUnsuccessfulInPast14DaysTrigger(true, true, true, false, "", "",
                "", "", "",
                0, "project");
    }

    private long olderThanThreshold() {
        return System.currentTimeMillis() -
                FivePlusUnsuccessfulInPast14DaysTrigger.DELTA_FOR_AGE_THRESHOLD - 1000;
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
