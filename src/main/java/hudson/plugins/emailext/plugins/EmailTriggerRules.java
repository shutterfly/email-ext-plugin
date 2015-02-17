package hudson.plugins.emailext.plugins;

import hudson.plugins.emailext.plugins.trigger.UnsuccessfulTrigger;
import hudson.model.Result;
import hudson.model.AbstractBuild;

/**
 * Created by jagte on 2/17/15.
 */
public class EmailTriggerRules {
    public final int MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
    public final int NUM_BUILDS_UNSUCCESSFUL_LIMIT = 5;
    public final int NUM_DAYS_IN_WINDOW = 14;
    public final int DELTA_FOR_AGE_THRESHOLD = NUM_DAYS_IN_WINDOW * MILLIS_PER_DAY;

    public boolean hasFiveUnsuccessfulBuildsInPast14Days(AbstractBuild<?, ?> currentBuild,
                                                                int currentCount) {
        if(exceedsUnsuccessfulLimit(currentCount))
            return true;
        if((currentBuild == null) || (getBuildStartTime(currentBuild) < minBuildTimeInMillis()))
            return exceedsUnsuccessfulLimit(currentCount);
        if(isBuildUnsuccessful(currentBuild.getResult()))
            currentCount += 1;

        return hasFiveUnsuccessfulBuildsInPast14Days(currentBuild.getPreviousBuild(), currentCount);
    }

    private long getBuildStartTime(AbstractBuild<?, ?> build) {
        return build.getStartTimeInMillis();
    }

    private boolean exceedsUnsuccessfulLimit(int currentCount) {
        return currentCount >= NUM_BUILDS_UNSUCCESSFUL_LIMIT;
    }

    private long minBuildTimeInMillis() {
        return System.currentTimeMillis() - DELTA_FOR_AGE_THRESHOLD;
    }

    public boolean lastXBuildsUnsuccessful(int buildsToCheck, AbstractBuild<?, ?> build) {
        if(build == null){
            return false;
        }

        final boolean currentBuildUnsuccessful = isBuildUnsuccessful(build.getResult());
        if(buildsToCheck == 1){
            return currentBuildUnsuccessful;
        }
        return currentBuildUnsuccessful &&
                lastXBuildsUnsuccessful(buildsToCheck-1,build.getPreviousBuild());
    }

    public boolean isBuildUnsuccessful(Result result) {
        return result.isWorseThan(Result.SUCCESS);
    }

}
