import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class MetaData {
    /** These are the system default value of a maximum number of hours an user can spend
      * playing continuously at this site, and the minimum number of hour an user must wait
      * before his account is unfreeze from "SUSPENDED" state. */
    public static final Integer DEFAULT_MAXIMUM_PLAY_TIME = 5;
    public static final Integer DEFAULT_MINIMUM_EXCLUSION_TIME = 24;
    public static final Integer MAX_DURATION = -1;

    public MetaData() {
        beginPlay = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
        endPlay = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
    }
    public void initializeMetaData(int userDeposit, int userMaxSpend, int userMaxLoss, int userPlayTime, int userExclusion) {
        init = true;
        deposit = userDeposit;
        maxSpend = userMaxSpend;
        maxLoss = userMaxLoss;
        userPlayTime = Math.abs(userPlayTime);
        if (userPlayTime < DEFAULT_MAXIMUM_PLAY_TIME){
            playTime = Duration.ofHours(userPlayTime);
        }
        userExclusion = Math.abs(userExclusion);
        if (userExclusion > DEFAULT_MINIMUM_EXCLUSION_TIME) {
            exclusionTime = Duration.ofHours(userExclusion);
        }
        beginPlay = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
        endPlay = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public void playTimeCountDownRestart() {
        beginPlay = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
        endPlay = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public int sessionTimeout() {
        LocalDateTime currentTime = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
        return currentTime.compareTo(beginPlay.plus(playTime));
    }

    public LocalDateTime unfreezeTime() {
        return endPlay.plus(exclusionTime);
    }

    public boolean isInitialized() {
        return init;
    }

    public void suspendNoExclusion() {
        init = true;
        exclusionTime = Duration.ZERO;
        endPlay = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public void suspendWithExclusion() {
        init = true;
        exclusionTime = exclusionTime.plus(exclusionTime);
        endPlay = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public void suspendInfiniteExclusion() {
        init = true;
        exclusionTime = Duration.ofHours(MAX_DURATION);
        endPlay = Instant.ofEpochMilli(System.currentTimeMillis()).
                atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public double getDeposit() {
        return ((double) deposit) / 100;
    }

    public void setDeposit(int newDeposit) {
        deposit = newDeposit;
    }

    public double getMaxSpend() {
        return ((double) maxSpend) / 100;
    }

    public double getMaxLoss() {
        return ((double) maxLoss) / 100;
    }

    public void reduceDeposit(int reduce) {
        deposit = deposit - reduce;
    }

    public void reduceSpend(int reduce) {
        maxSpend = maxSpend - reduce;
    }

    public void reduceLoss(int reduce) {
        maxLoss = maxLoss - reduce;
    }

    public LocalDateTime sessionBegin() {
        return beginPlay;
    }

    public LocalDateTime sessionEnd() {
        return endPlay;
    }

    public Duration getPlayTime() {
        return playTime;
    }

    public Duration getExclusionTime() {
        return exclusionTime;
    }

    public LocalDateTime setBeginTime(int hour) {
        return beginPlay.minusHours(hour);
    }

    public Duration getActualPlayTime() {
        return Duration.between(beginPlay, endPlay);
    }

    private boolean init = false;
    private int deposit = 0;
    private int maxSpend = 0;
    private int maxLoss = 0;
    private Duration playTime = Duration.ofHours(DEFAULT_MAXIMUM_PLAY_TIME);
    private Duration exclusionTime = Duration.ofHours(DEFAULT_MINIMUM_EXCLUSION_TIME);
    private LocalDateTime beginPlay;
    private LocalDateTime endPlay;
}
