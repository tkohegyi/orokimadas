package website.magyar.adoration.database.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Descriptor class for Database table: Translator.
 * Technically: as preparation of the multi-language support of the site.
 */
@Entity
@Table(name = "dbo.translator")
public class Translator {

    private String textId;
    private String languageCode;
    private String text;

    /**
     * General constructor, used by Hibernate.
     * Shall be used only when a new record is created - then fields need to be filled of course before saving it to the database.
     */
    public Translator() {
        // this form used by Hibernate
    }

    @Column(name = "textId", nullable = false)
    @Id
    public String getTextId() {
        return textId;
    }

    public void setTextId(String textId) {
        this.textId = textId;
    }

    @Column(name = "languageCode", nullable = false)
    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Column(name = "text", nullable = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
