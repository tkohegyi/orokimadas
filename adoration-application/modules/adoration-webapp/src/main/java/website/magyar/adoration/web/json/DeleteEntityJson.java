package website.magyar.adoration.web.json;

import website.magyar.adoration.helper.JsonField;

/**
 * Json structure to be used to delete an entity.
 * Since every database record has its own unique id, specifying the id is enough.
 */
public class DeleteEntityJson {
    @JsonField
    public String entityId;
}
