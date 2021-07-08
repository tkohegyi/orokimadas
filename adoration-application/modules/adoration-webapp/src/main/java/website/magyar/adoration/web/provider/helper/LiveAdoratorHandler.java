package website.magyar.adoration.web.provider.helper;

import website.magyar.adoration.web.json.CurrentUserInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to handle online adoration.
 */
@Component
public class LiveAdoratorHandler {
    private static final Object OBJECT = new Object();
    private static final long TIMER_DELAY = 30000;
    private static final long TIMER_PERIOD = 30000;
    private static Timer liveMapCheckerTimer;
    private static boolean isLogActive = true;

    private final Logger logger = LoggerFactory.getLogger(LiveAdoratorHandler.class);
    private final Map<String, LiveMapElement> liveMap = new ConcurrentHashMap<>();

    /**
     * Method that generates the list of the live users in JSON format.
     *
     * @return with the response body
     */
    public String getLiveMapAsResponse() {
        var response = new StringBuilder("{\n  \"liveMap\": [\n");
        if (!liveMap.isEmpty()) {
            synchronized (OBJECT) {
                String[] keySet = liveMap.keySet().toArray(new String[liveMap.size()]);
                for (int i = 0; i < keySet.length; i++) {
                    String entryKey = keySet[i];
                    response.append("    { \"").append(entryKey).append("\": \"")
                            .append(liveMap.get(entryKey).getClass().getCanonicalName()).append("\" }");
                    if (i < keySet.length - 1) {
                        response.append(",");
                    }
                    response.append("\n");
                }
            }
        }
        response.append("  ]\n}\n");
        return response.toString();
    }

    /**
     * Method to register a live adorator in the map.
     *
     * @param currentUserInformationJson is the actual user
     * @return with a uuid associated to the live adorator
     */
    public String registerLiveAdorator(CurrentUserInformationJson currentUserInformationJson) {
        var uuid = UUID.randomUUID().toString();
        synchronized (OBJECT) {
            if (liveMap.get(uuid) == null) {
                logger.info("Live Adorator joined - {} - {}", currentUserInformationJson.userName, uuid);
            }
            liveMap.putIfAbsent(uuid, new LiveMapElement(currentUserInformationJson)); //add new online adorator
            isLogActive = true; //turn on logging
            if (liveMapCheckerTimer == null) { //initiate timer
                liveMapCheckerTimer = new Timer(true);
                liveMapCheckerTimer.scheduleAtFixedRate(new LiveAdoratorHandlerTimerTask(this), TIMER_DELAY, TIMER_PERIOD);
            }
        }
        return uuid;
    }

    /**
     * Need to be called to renew the existence of the live adorator in the map.
     *
     * @param hashString is the uuid of the live adorator to be extended
     */
    public void reNewLiveAdorator(String hashString) {
        synchronized (OBJECT) {
            if (liveMap.containsKey(hashString)) {
                LiveMapElement liveMapElement = liveMap.get(hashString);
                liveMapElement.extend();
                logger.info("Live Adorator extended - {} - {}", liveMapElement.getCurrentUserInformationJson().userName, hashString);
            } else {
                logger.warn("Unexpected incoming tick.");
            }
        }
    }

    /**
     * This is to clean up obsolete entries (live adorators) from the map.
     */
    public void timerTick() {
        if (isLogActive) {
            logger.info("LiveMap Timer tick... online adorators: {}", liveMap.size());
        }
        if (!liveMap.isEmpty()) {
            long now = System.currentTimeMillis();
            synchronized (OBJECT) {
                String[] keySet = liveMap.keySet().toArray(new String[liveMap.size()]);
                for (String entryKey : keySet) {
                    LiveMapElement liveMapElement = liveMap.get(entryKey);
                    if (liveMapElement.getDeadline() < now) { //if expired
                        liveMap.remove(entryKey); //remove the entry
                        logger.info("Live Adorator left - {} - {}", liveMapElement.getCurrentUserInformationJson().userName, entryKey);
                    }
                }
            }
        } else {
            //no online adorator
            synchronized (OBJECT) {
                isLogActive = false;
            }
        }
    }
}
