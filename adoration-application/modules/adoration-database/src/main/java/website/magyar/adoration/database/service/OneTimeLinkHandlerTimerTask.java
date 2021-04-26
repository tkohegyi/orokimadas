package website.magyar.adoration.database.service;

import java.util.TimerTask;

/**
 * Timer task for One Time Link Handler.
 */
public class OneTimeLinkHandlerTimerTask extends TimerTask {
    private final OneTimeLinkHandler oneTimeLinkHandler;

    /**
     * Constructor of the class.
     * @param oneTimeLinkHandler is the class this timer is used for
     */
    public OneTimeLinkHandlerTimerTask(OneTimeLinkHandler oneTimeLinkHandler) {
        this.oneTimeLinkHandler = oneTimeLinkHandler;
    }

    @Override
    public void run() {
        oneTimeLinkHandler.timerTick();
    }
}
