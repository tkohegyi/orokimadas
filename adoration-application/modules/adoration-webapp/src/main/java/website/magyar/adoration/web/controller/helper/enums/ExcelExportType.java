package website.magyar.adoration.web.controller.helper.enums;

/**
 * Enum of the available excel exports.
 */
public enum ExcelExportType {
    BIG_INFO("nagyRegiszter.xlsx"),
    DAILY_INFO("napszakFedettség.xlsx"),
    HOURLY_INFO("órainformáció.xlsx"),
    ADORATOR_INFO("adoráló-adatok.xlsx");

    private final String templateName;

    ExcelExportType(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
