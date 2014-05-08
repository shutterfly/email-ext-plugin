package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.EmailTriggerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class FivePlusUnsuccessfulInPast14DaysTrigger extends EmailTrigger {
    public static final String TRIGGER_NAME = ">5 unsuccessful in past 14 days";
    public static final int MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
    public static final int NUM_BUILDS_UNSUCCESSFUL_LIMIT = 5;
    public static final int NUM_DAYS_IN_WINDOW = 14;
    public static final int DELTA_FOR_AGE_THRESHOLD = NUM_DAYS_IN_WINDOW * MILLIS_PER_DAY;

    @DataBoundConstructor
    public FivePlusUnsuccessfulInPast14DaysTrigger(
            boolean sendToList,
            boolean sendToDevs,
            boolean sendToRequestor,
            boolean sendToCulprits,
            String recipientList,
            String replyTo,
            String subject,
            String body,
            String attachmentsPattern,
            int attachBuildLog,
            String contentType) {
        super(sendToList, sendToDevs, sendToRequestor, sendToCulprits, recipientList,
                replyTo, subject, body, attachmentsPattern, attachBuildLog, contentType);
    }

    @Override
    public boolean trigger(AbstractBuild<?, ?> build, TaskListener listener) {
        return UnsuccessfulTrigger.isBuildUnsuccessful(build.getResult()) &&
                hasFiveUnsuccessfulBuildsInPast14Days(build, 0);
    }

    private boolean hasFiveUnsuccessfulBuildsInPast14Days(AbstractBuild<?, ?> currentBuild,
                                                          int currentCount) {
        if(exceedsUnsuccessfulLimit(currentCount))
            return true;
        if(currentBuild == null || getBuildStartTime(currentBuild) < minBuildTimeInMillis())
            return exceedsUnsuccessfulLimit(currentCount);
        if(UnsuccessfulTrigger.isBuildUnsuccessful(currentBuild.getResult()))
            currentCount += 1;

        return hasFiveUnsuccessfulBuildsInPast14Days(currentBuild.getPreviousBuild(), currentCount);
    }

    private long getBuildStartTime(AbstractBuild<?, ?> build) {
        return build.getStartTimeInMillis();
    }

    private boolean exceedsUnsuccessfulLimit(int currentCount) {
        return currentCount >= NUM_BUILDS_UNSUCCESSFUL_LIMIT;
    }

    private static long minBuildTimeInMillis() {
        return System.currentTimeMillis() - DELTA_FOR_AGE_THRESHOLD;
    }

    @Extension
    public static class DescriptorImpl extends EmailTriggerDescriptor {
        public String getDisplayName() {
            return TRIGGER_NAME;
        }
    }
}
