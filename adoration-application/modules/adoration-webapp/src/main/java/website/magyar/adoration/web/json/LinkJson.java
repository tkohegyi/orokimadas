package website.magyar.adoration.web.json;

import website.magyar.adoration.database.tables.Link;
import website.magyar.adoration.helper.JsonField;

import java.util.List;
import java.util.Map;

/**
 * Json structure to hold all information that is necessary for presenting an hour-adorator link.
 */
public class LinkJson {
    @JsonField
    public List<Link> linkList; //committed hours
    @JsonField
    public List<PersonJson> relatedPersonList; //info about ppl
    @JsonField
    public Map<Integer, String> dayNames; //dayId - text pairs
}
