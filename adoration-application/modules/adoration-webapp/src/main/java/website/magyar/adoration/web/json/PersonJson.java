package website.magyar.adoration.web.json;

import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.helper.JsonField;

/**
 * Json structure to be used about a person when the information is provided to a non-privileged person.
 */
public class PersonJson {
    private static final String UNKNOWN_DATA = "???";

    @JsonField
    public Long id;
    @JsonField
    public String name;
    @JsonField
    public String email;
    @JsonField
    public String mobile;
    @JsonField
    public String coordinatorComment;
    @JsonField
    public String visibleComment;

    /**
     * Create a data class.
     * If requestor is privileged user, then returns with all fields.
     * If requestor is not privileged, then:
     * - Only ID is available in case DCH is not enabled.
     * - Name is "Anonymous" in case person would like to remain anonymous, and only id and mobile fields are filled.
     * - email/mobile is visible based on visibility flag.
     * - coordinator comment requires privileged user
     * - visible comment is always available
     *
     * @param person
     */
    public PersonJson(Person person, Boolean isPrivilegedUser) {
        this.id = person.getId();
        if (!Boolean.TRUE.equals(person.getIsAnonymous()) || Boolean.TRUE.equals(isPrivilegedUser)) {
            this.name = person.getName();
        } else {
            this.name = "Anonymous";
        }
        if ((person.getIsAnonymous() || !person.getEmailVisible()) && !Boolean.TRUE.equals(isPrivilegedUser)) {
            this.email = UNKNOWN_DATA;
        } else {
            this.email = person.getEmail();
        }
        if (Boolean.TRUE.equals(person.getMobileVisible()) || Boolean.TRUE.equals(isPrivilegedUser)) {
            this.mobile = person.getMobile();
        } else {
            this.mobile = UNKNOWN_DATA;
        }
        if (Boolean.TRUE.equals(isPrivilegedUser)) {
            this.coordinatorComment = person.getCoordinatorComment();
        } else {
            this.coordinatorComment = "";
        }
        this.visibleComment = person.getVisibleComment();
    }
}


