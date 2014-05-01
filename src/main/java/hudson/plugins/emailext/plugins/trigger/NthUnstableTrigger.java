package hudson.plugins.emailext.plugins.trigger;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.EmailTriggerDescriptor;

/**
 * Triggers an email after the specified number of consecutive unstable builds
 * (preceded by a successful build).
 */
public abstract class NthUnstableTrigger extends EmailTrigger {

    protected int unstableCount;

    public NthUnstableTrigger(int unstableCount,
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
        super(sendToList, sendToDevs, sendToRequestor, sendToCulprits, recipientList, replyTo, subject, body, attachmentsPattern, attachBuildLog, contentType);
        this.unstableCount = unstableCount;
    }

    @Override
    public boolean trigger(AbstractBuild<?, ?> build, TaskListener listener) {
        try {
            AbstractBuild<?, ?> previousBuild = getNthPriorBuild(build, listener);
            return shouldTransitionTrigger(previousBuild);
        } catch (NthUnstableBuildNotFoundException e) {
            return false;
        }
    }

    private AbstractBuild<?, ?> getNthPriorBuild(AbstractBuild<?, ?> build, TaskListener listener) throws NthUnstableBuildNotFoundException {
        for (int i = 0; i < unstableCount; i++) {
            if (build == null) {
                // We don't have enough history to have reached the unstable count.
                throw new NthUnstableBuildNotFoundException();
            }

            Result buildResult = build.getResult();
            if (buildResult != Result.UNSTABLE) {
                throw new NthUnstableBuildNotFoundException();
            }

            build = ExtendedEmailPublisher.getPreviousBuild(build, listener);
        }
        return build;
    }

    private boolean shouldTransitionTrigger(AbstractBuild<?, ?> precedingBuild) {
        // Check the the preceding build was a success or failure.
        // if there is no previous build (null), this is a first unstable
        // if there is a previous build and it's result was success or failure, this is first unstable
        return (precedingBuild == null) || (precedingBuild.getResult() == Result.SUCCESS) || (precedingBuild.getResult() == Result.FAILURE);

    }

    private static class NthUnstableBuildNotFoundException extends Exception {
        NthUnstableBuildNotFoundException() {
        }
    }

    public abstract static class DescriptorImpl extends EmailTriggerDescriptor {

        public DescriptorImpl() {
            addTriggerNameToReplace(UnstableTrigger.TRIGGER_NAME);
            addTriggerNameToReplace(StillUnstableTrigger.TRIGGER_NAME);
        }

        @Override
        public boolean getDefaultSendToDevs() {
            return true;
        }

        @Override
        public boolean getDefaultSendToList() {
            return true;
        }
    }
}
