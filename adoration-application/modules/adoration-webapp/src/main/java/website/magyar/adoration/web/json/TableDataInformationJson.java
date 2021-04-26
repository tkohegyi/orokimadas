package website.magyar.adoration.web.json;

import website.magyar.adoration.helper.JsonField;

/**
 * Json structure to be used to hold and transfer a single (but sometimes large) object.
 */
public class TableDataInformationJson {
    @JsonField
    public Object data;

    /**
     * Constructor of the Json structure, by filling its content immediately.
     */
    public TableDataInformationJson(Object data) {
        this.data = data;
    }
}
