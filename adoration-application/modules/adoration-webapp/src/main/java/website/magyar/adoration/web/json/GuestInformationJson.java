package website.magyar.adoration.web.json;

import website.magyar.adoration.helper.JsonField;

import java.util.List;

/**
 * Json structure to provide information for a "guest" user.
 */
public class GuestInformationJson {
    @JsonField
    public String error; // filled only in case of error
    @JsonField
    public String socialServiceUsed;
    @JsonField
    public Boolean isGoogle;
    @JsonField
    public String nameGoogle;
    @JsonField
    public String emailGoogle;
    @JsonField
    public Boolean isFacebook;
    @JsonField
    public String nameFacebook;
    @JsonField
    public String emailFacebook;
    @JsonField
    public String status;   //status of the adorator
    @JsonField
    public String id;   //id of the social record
    @JsonField
    public List<CoordinatorJson> leadership; //main coordinators
}
