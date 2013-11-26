package hudson.plugins.emailext.plugins.trigger;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

public class FirstUnstableTrigger extends NthUnstableTrigger {

    public static final String TRIGGER_NAME = "1st Unstable";
    public static int triggerCount = 1;

    @DataBoundConstructor
    public FirstUnstableTrigger(boolean sendToList,
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
        super(triggerCount, sendToList, sendToDevs, sendToRequestor, sendToCulprits, recipientList, replyTo, subject, body, attachmentsPattern, attachBuildLog, contentType);
    }

    @Extension
    public static final class DescriptorImpl extends NthUnstableTrigger.DescriptorImpl {

        @Override
        public String getDisplayName() {
            return TRIGGER_NAME;
        }
    }

    /**
     * Maintaining backward compatibility
     *
     * @return this after checking for failureCount setting
     */
    public Object readResolve() {
        if (this.unstableCount == 0) {
            this.unstableCount = 1;
        }
        return this;
    }
}
