package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.EmailTriggerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

public class FivePlusUnsuccessfulInPast14DaysTrigger extends EmailTrigger {
    public static final String TRIGGER_NAME = ">5 unsuccessful in past 14 days";

    @SuppressWarnings("UnusedDeclaration")
    public static FivePlusUnsuccessfulInPast14DaysTrigger createDefault() {
        return new FivePlusUnsuccessfulInPast14DaysTrigger(true, false, true, false, "",
                "$PROJECT_DEFAULT_REPLYTO",
                "$PROJECT_DEFAULT_SUBJECT", "$PROJECT_DEFAULT_CONTENT", "", 0, "project");
    }

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

    /* This should send email, as long as:
    -The current build is unsuccessful
    -There were 5 unsuccessful builds in the last 14 days
    -This trigger has not already fired based on the previous build (i.e. the 6th, 7th, etc. failure)
*/
    @Override
    public boolean trigger(AbstractBuild<?, ?> build, TaskListener listener) {
        return rules.isBuildUnsuccessful(build.getResult()) &&
                rules.hasFiveUnsuccessfulBuildsInPast14Days(build, 0) &&
                !rules.hasFiveUnsuccessfulBuildsInPast14Days(build.getPreviousBuild(), 0);
    }

    @Extension
    public static class DescriptorImpl extends EmailTriggerDescriptor {
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
