package website.magyar.adoration.database.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Descriptor class for Database table: Audit trail.
 */
@Entity
@Table(name = "dbo.auditTrail")
public class AuditTrail {

    private Long id;
    private Long refId;
    private String atWhen;
    private String byWho;
    private String activityType;
    private String description;
    private String data;

    /**
     * General constructor, used by Hibernate.
     * Shall be used only when a new record is created - then fields need to be filled of course before saving it to the database.
     */
    public AuditTrail() {
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

    @Column(name = "refId", nullable = false)
    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    @Column(name = "atWhen", nullable = false)
    public String getAtWhen() {
        return atWhen;
    }

    public void setAtWhen(String atWhen) {
        this.atWhen = atWhen;
    }

    @Column(name = "byWho", nullable = false)
    public String getByWho() {
        return byWho;
    }

    public void setByWho(String byWho) {
        this.byWho = byWho;
    }

    @Column(name = "activityType", nullable = false)
    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "data", nullable = true) // this can be null !
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
