package website.magyar.adoration.web.json;

import website.magyar.adoration.database.business.helper.enums.TranslatorDayNames;
import website.magyar.adoration.helper.JsonField;

/**
 * Json structure with all info that is necessary to register a new adorator.
 */
public class RegisterAdoratorJson {
    @JsonField
    public String name;
    @JsonField
    public String comment;
    @JsonField
    public Integer dayId;
    @JsonField
    public Integer hourId;
    @JsonField
    public String email;
    @JsonField
    public String coordinate;
    @JsonField
    public Integer method;
    @JsonField
    public String dhc;
    @JsonField
    public String dhcSignedDate;
    @JsonField
    public String mobile;
    @JsonField
    public Long personId;
    @JsonField
    public Long socialId;
    @JsonField
    public String languageCode;

    /**
     * Method to convert the structure to a text format.
     * Such string is sent as mail body to the administrator as notification about the newly registered adorator.
     *
     * @return the text representation of a registration information
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Név/Name: ").append(name).append(",\n");
        sb.append("Megjegyzés/Comment: ").append(comment).append(",\n");
        sb.append("Nap/Day: ").append(TranslatorDayNames.getTranslatedString(dayId)).append(",\n");
        sb.append("Óra/Hour: ").append(hourId).append(",\n");
        sb.append("Email: ").append(email).append(",\n");
        sb.append("Segítség szervezésben: ").append(coordinate).append(",\n");
        sb.append("Adorálás módja: ").append(method).append(" (1: kápolnában hetente; 2: online hetente; 3: ad-hoc),\n");
        sb.append("Adatkezelési hozzájárulás: ").append(dhc).append(",\n");
        sb.append("Adatkezelési hozzájárulás dátuma: ").append(dhcSignedDate).append(",\n");
        sb.append("Telefonszám: ").append(mobile).append(",\n");
        sb.append("Nyelv kód: ").append(languageCode).append(",\n");
        if (personId == null) {
            sb.append("A személy nincs beazonosítva.\n");
        } else {
            sb.append("A személy azonosítója: ").append(personId).append(".\n");
        }
        if (socialId == null) {
            sb.append("A személy nincs belépve.\n");
        } else {
            sb.append("A személy belépett, Social azonosítója: ").append(socialId).append(".\n");
        }
        return sb.toString();
    }
}
