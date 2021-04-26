package website.magyar.adoration.web.provider.helper;

import java.util.TimerTask;

/**
 * Timer task for Live Adorator Handler.
 */
public class LiveAdoratorHandlerTimerTask extends TimerTask {
    private final LiveAdoratorHandler liveAdoratorHandler;

    /**
     * Constructor of the class.
     * @param liveAdoratorHandler is the class this timer is used for
     */
    public LiveAdoratorHandlerTimerTask(LiveAdoratorHandler liveAdoratorHandler) {
        this.liveAdoratorHandler = liveAdoratorHandler;
    }

    @Override
    public void run() {
        liveAdoratorHandler.timerTick();
    }
}
