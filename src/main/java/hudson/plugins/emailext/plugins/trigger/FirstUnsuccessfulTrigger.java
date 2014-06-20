package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.EmailTriggerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class FirstUnsuccessfulTrigger  extends EmailTrigger {
    public static final String TRIGGER_NAME = "1st Unsuccessful";

    @DataBoundConstructor
    public FirstUnsuccessfulTrigger(
            boolean sendToList, boolean sendToDevs, boolean sendToRequestor,
            boolean sendToCulprits, String recipientList, String replyTo, String subject,
            String body, String attachmentsPattern, int attachBuildLog,
            String contentType) {
        super(sendToList, sendToDevs, sendToRequestor, sendToCulprits, recipientList,
                replyTo, subject, body, attachmentsPattern, attachBuildLog, contentType);
    }

    @Override
    public boolean trigger(AbstractBuild<?, ?> build, TaskListener listener) {
        if(!UnsuccessfulTrigger.isBuildUnsuccessful(build.getResult()))
            return false;
        AbstractBuild<?, ?> prevBuild = build.getPreviousBuild();
        return prevBuild == null || !UnsuccessfulTrigger.isBuildUnsuccessful(prevBuild.getResult());

    }

    public static FirstUnsuccessfulTrigger createDefault() {
        return new FirstUnsuccessfulTrigger(true, false, true, false, "",
                "$PROJECT_DEFAULT_REPLYTO",
                "$PROJECT_DEFAULT_SUBJECT", "$PROJECT_DEFAULT_CONTENT", "", 0, "project");
    }

    @Extension
    public static final class DescriptorImpl extends EmailTriggerDescriptor {
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
