package website.magyar.adoration.web.json;

import website.magyar.adoration.database.tables.Link;
import website.magyar.adoration.helper.JsonField;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Json structure that is used to provide information for a registered adorator.
 */
public class InformationJson {
    @JsonField
    public String error; // filled only in case of error
    @JsonField
    public String name; //name of the adorator
    @JsonField
    public String status;   //status of the adorator
    @JsonField
    public String id;   //id of the adorator
    @JsonField
    public List<Link> linkList; //committed hours of the adorator
    @JsonField
    public Set<Integer> hoursCancelled; //committed hours those are one-time cancelled
    @JsonField
    public List<CoordinatorJson> leadership; //main coordinators
    @JsonField
    public List<Link> currentHourList; //adorators in actual hour
    @JsonField
    public List<Link> futureHourList; //adorators in next hour
    @JsonField
    public List<PersonJson> relatedPersonList; //info about ppl
    @JsonField
    public Map<Integer, String> dayNames; //dayId - text pairs
    @JsonField
    public Integer hourInDayNow;
    @JsonField
    public Integer hourInDayNext;
}
