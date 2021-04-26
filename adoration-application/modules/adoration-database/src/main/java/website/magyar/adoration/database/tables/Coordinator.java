package website.magyar.adoration.database.tables;

import website.magyar.adoration.database.business.helper.enums.CoordinatorTypes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Descriptor class for Database table: Coordinator.
 */
@Entity
@Table(name = "dbo.coordinator")
public class Coordinator {

    private Long id;
    private Integer coordinatorType;
    private Long personId;

    /**
     * General constructor, used by Hibernate.
     * Shall be used only when a new record is created - then fields need to be filled of course before saving it to the database.
     */
    public Coordinator() {
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

    @Column(name = "coordinatorType", nullable = false)
    public Integer getCoordinatorType() {
        return coordinatorType;
    }

    /**
     * Set the coordinatorType field of the Coordinator object.
     * The method check the value validity in implicit mode - searches for the associated enum object,
     * and throws DatabaseHandlingException if the given value is incorrect.
     *
     * @param coordinatorType is the integer representation of the coordinator type enum value.
     */
    public void setCoordinatorType(Integer coordinatorType) {
        CoordinatorTypes.getTypeFromId(coordinatorType);
        this.coordinatorType = coordinatorType;
    }

    @Column(name = "personId", nullable = true)
    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

}
