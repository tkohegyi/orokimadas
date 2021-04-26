package website.magyar.adoration.web.json;

import website.magyar.adoration.helper.JsonField;

import java.util.Map;
import java.util.Set;

/**
 * Json structure to hold information necessary to draw coverage information.
 */
public class CoverageInformationJson {
    @JsonField
    public Map<String, String> dayNames; //textId - text pairs
    @JsonField
    public Map<Integer, Integer> visibleHours; //hourId - number of adorators, only prio 1,2
    @JsonField
    public Map<Integer, Set<Long>> allHours; //hourId - id of adorators, regardless of priority
    @JsonField
    public Map<Integer, Set<Long>> missHours; //hourId - id of adorators, where the adorator cannot participate
    @JsonField
    public Map<Integer, Set<Long>> oneTimeHours; //hourId - id of adorators, where a one-time adorator participates
    @JsonField
    public Map<Integer, Set<Long>> onlineHours; //hourId - id of online adorators
    @JsonField
    public Map<Long, PersonJson> adorators; //personId - adorator info
}
