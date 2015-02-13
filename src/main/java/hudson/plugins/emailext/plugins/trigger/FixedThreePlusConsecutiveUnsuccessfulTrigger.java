package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.EmailTriggerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.model.Result;

/**
 * Created by jagte on 2/11/15.
 */
public class FixedThreePlusConsecutiveUnsuccessfulTrigger extends EmailTrigger {
    public static final String TRIGGER_NAME = "Fixed >3 consecutive unsuccessful";

    @SuppressWarnings("UnusedDeclaration")
    public static FixedThreePlusConsecutiveUnsuccessfulTrigger createDefault() {
        return new FixedThreePlusConsecutiveUnsuccessfulTrigger(true, false, true, false, "",
                "$PROJECT_DEFAULT_REPLYTO",
                "$PROJECT_DEFAULT_SUBJECT", "$PROJECT_DEFAULT_CONTENT", "", 0, "project");
    }

    @DataBoundConstructor
    public FixedThreePlusConsecutiveUnsuccessfulTrigger(
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
        return (build.getResult()== Result.SUCCESS) &&
                ThreePlusConsecutiveUnsuccessfulTrigger.lastXBuildsUnsuccessful(3, build.getPreviousBuild());
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
            return false;
        }

        @Override
        public boolean getDefaultSendToRequester() {
            return true;
        }

    }
}
