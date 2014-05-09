package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.EmailTriggerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class ThreePlusConsecutiveUnsuccessfulTrigger extends EmailTrigger {
    public static final String TRIGGER_NAME = ">3 consecutive unsuccessful";

    @DataBoundConstructor
    public ThreePlusConsecutiveUnsuccessfulTrigger(
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
        return lastXBuildsUnsuccessful(3, build);
    }

    private boolean lastXBuildsUnsuccessful(int buildsToCheck, AbstractBuild<?, ?> build) {
        if(build == null){
            return false;
        }

        final boolean currentBuildUnsuccessful = UnsuccessfulTrigger.isBuildUnsuccessful(build.getResult());
        if(buildsToCheck == 1){
            return currentBuildUnsuccessful;
        }
        return currentBuildUnsuccessful &&
                lastXBuildsUnsuccessful(buildsToCheck-1,build.getPreviousBuild());
    }


    @Extension
    public static class DescriptorImpl extends EmailTriggerDescriptor {
        @Override
        public String getDisplayName() {
            return TRIGGER_NAME;
        }

        @Override
        public boolean getDefaultSendToList() {
            return true;
        }

        @Override
        public boolean getDefaultSendToDevs() {
            return true;
        }

        @Override
        public boolean getDefaultSendToRequester() {
            return true;
        }

    }
}
