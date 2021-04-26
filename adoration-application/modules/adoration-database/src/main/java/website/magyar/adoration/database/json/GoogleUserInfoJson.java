package website.magyar.adoration.database.json;

import website.magyar.adoration.helper.JsonField;

/**
 * Json class representing a Google Social user.
 * To be used by Google to build up info object for a logged-in user.
 */
public class GoogleUserInfoJson {
    @JsonField
    public String id;
    @JsonField
    public String email;
    @JsonField
    public String name;
    @JsonField
    public String picture;
}
