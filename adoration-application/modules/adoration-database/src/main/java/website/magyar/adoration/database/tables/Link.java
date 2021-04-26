package website.magyar.adoration.database.tables;

import website.magyar.adoration.database.business.helper.enums.AdorationMethodTypes;
import website.magyar.adoration.database.exception.DatabaseHandlingException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Descriptor class for Database table: Link - connection betwen hours and adorators.
 */
@Entity
@Table(name = "dbo.link")
public class Link {

    public static final Integer MIN_HOUR = 0;
    public static final Integer MAX_HOUR = 167;
    private Long id;
    private Long personId;
    private Integer hourId;
    private Integer type;
    private Integer priority;
    private String adminComment;
    private String publicComment;

    /**
     * General constructor, used by Hibernate.
     * Shall be used only when a new record is created - then fields need to be filled of course before saving it to the database.
     */
    public Link() {
        // this form used by Hibernate
    }

    @Column(name = "id", nullable = false)
    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "personId", nullable = false)
    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Column(name = "hourId", nullable = false)
    public Integer getHourId() {
        return hourId;
    }

    public void setHourId(Integer hourId) {
        if (hourId == null || hourId > MAX_HOUR || hourId < MIN_HOUR) {
            throw new DatabaseHandlingException("Specified HourId is out of bounds.");
        }
        this.hourId = hourId;
    }

    @Column(name = "type", nullable = false)
    public Integer getType() {
        return type;
    }

    /**
     * Sets the type field of the Link table.
     * The method check the value validity in implicit mode - searches for the associated enum object,
     * and throws DatabaseHandlingException if the given value is incorrect.
     *
     * @param type is the integer representation of the adoration method type enum.
     */
    public void setType(Integer type) {
        AdorationMethodTypes.getTypeFromId(type); //this will fail if the type is incorrect
        this.type = type;
    }

    @Column(name = "priority", nullable = false)
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * Gets adminComment field of a Link record, ensures that it never will have null value.
     *
     * @return with the adminComment field value or with an empty string.
     */
    @Column(name = "adminComment", nullable = true)
    public String getAdminComment() {
        if (adminComment != null) {
            return adminComment;
        } else {
            return "";
        }
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    /**
     * Gets publicComment field of a Link record, ensures that it never will have null value.
     *
     * @return with the publicComment field value or with an empty string.
     */
    @Column(name = "publicComment", nullable = true)
    public String getPublicComment() {
        if (publicComment != null) {
            return publicComment;
        } else {
            return "";
        }
    }

    public void setPublicComment(String publicComment) {
        this.publicComment = publicComment;
    }
}
