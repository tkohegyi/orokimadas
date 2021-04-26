package website.magyar.adoration.web.json;

import website.magyar.adoration.helper.JsonField;

/**
 * Json structure that hold information about a specific person. Used for coordinators and other privileged users only.
 * For non-privileged users, the PersonJson class is used.
 */
public class PersonInformationJson {
    @JsonField
    public String id;
    @JsonField
    public String name;
    @JsonField
    public String adorationStatus;
    @JsonField
    public String mobile;
    @JsonField
    public String mobileVisible;
    @JsonField
    public String email;
    @JsonField
    public String emailVisible;
    @JsonField
    public String adminComment;
    @JsonField
    public String dhcSigned;
    @JsonField
    public String dhcSignedDate;
    @JsonField
    public String coordinatorComment;
    @JsonField
    public String visibleComment;
    @JsonField
    public String isAnonymous;
}
