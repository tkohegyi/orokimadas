package website.magyar.adoration.web.provider;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import website.magyar.adoration.database.business.BusinessWithCoordinator;
import website.magyar.adoration.database.business.BusinessWithLink;
import website.magyar.adoration.database.business.BusinessWithPerson;
import website.magyar.adoration.database.business.helper.enums.AdorationMethodTypes;
import website.magyar.adoration.database.business.helper.enums.AdoratorStatusTypes;
import website.magyar.adoration.database.tables.Coordinator;
import website.magyar.adoration.database.tables.Link;
import website.magyar.adoration.database.tables.Person;
import website.magyar.adoration.web.configuration.PropertyDto;
import website.magyar.adoration.web.configuration.WebAppConfigurationAccess;
import website.magyar.adoration.web.json.CoverageInformationJson;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to provide support of Excel based exports and downloads.
 */
@Component
public class ExcelProvider {

    private static final Map<Integer, String> HOUR_CODES;
    private static final String THERE_IS_NO_INFORMATION = "-";

    static {
        HOUR_CODES = new HashMap<>();
        HOUR_CODES.put(0, "V;");
        HOUR_CODES.put(1, "H;");
        HOUR_CODES.put(2, "K;");
        HOUR_CODES.put(3, "Sze;");
        HOUR_CODES.put(4, "Cs;");
        HOUR_CODES.put(5, "P;");
        HOUR_CODES.put(6, "Szo;");
    }

    @Autowired
    private BusinessWithPerson businessWithPerson;
    @Autowired
    private BusinessWithLink businessWithLink;
    @Autowired
    private BusinessWithCoordinator businessWithCoordinator;
    @Autowired
    private WebAppConfigurationAccess webAppConfigurationAccess;
    @Autowired
    private CoverageProvider coverageProvider;
    @Autowired
    private CoordinatorProvider coordinatorProvider;

    private Row getRow(Sheet sheet, int rowNo) {
        Row row = sheet.getRow(rowNo);
        if (row == null) {
            row = sheet.createRow(rowNo);
        }
        return row;
    }

    private Cell getCell(Row row, int colNo) {
        Cell cell = row.getCell(colNo);
        if (cell == null) {
            cell = row.createCell(colNo);
        }
        return cell;
    }

    private Cell getSheetCell(Sheet sheet, int rowNo, int colNo) {
        Row row;
        Cell cell;
        row = getRow(sheet, rowNo);
        cell = getCell(row, colNo);
        return cell;
    }

    private void setCellOnSheet(Sheet sheet, int rowNo, int colNo, String cellValue) {
        getSheetCell(sheet, rowNo, colNo).setCellValue(cellValue);
    }

    private void reCalculateAllExcelCells(Workbook w) {
        XSSFFormulaEvaluator.evaluateAllFormulaCells(w); //NOSONAR - this is an external lib call, we have to use this
    }

    private Workbook getSpecificXlsTemplate(final String excelFilename) throws IOException {
        Workbook workbook;
        try (FileInputStream file = new FileInputStream(new File(excelFilename))) {
            workbook = new XSSFWorkbook(file);
        }
        return workbook;
    }

    /**
     * Get full excel export - usually used by main coordinators only.
     *
     * @throws IOException if any issue happens
     */
    public void getExcelFull(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialXls(userInformation.languageCode);
        updateAdorators(w);
        updateCoverage(userInformation, w);
        reCalculateAllExcelCells(w);
        w.write(outputStream);
    }

    private Workbook createInitialXls(final String languageCode) throws IOException {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        return getSpecificXlsTemplate(propertyDto.getBaseExcelFolder() + languageCode + propertyDto.getExcelFileName());
    }

    private String getHourString(Integer hourId) {
        Integer day = hourId / BusinessWithLink.HOUR_IN_A_DAY;
        int hour = hourId % BusinessWithLink.HOUR_IN_A_DAY;
        String dayString = HOUR_CODES.get(day);
        return dayString + hour + ";";
    }

    private void fillCellsWithLinks(List<Link> links, Cell cellOfRow14, Cell cellOfRow15) {
        StringBuilder physical = new StringBuilder();
        StringBuilder online = new StringBuilder();
        boolean wasP = false;
        boolean wasO = false;
        for (Link l : links) {
            String hString = getHourString(l.getHourId()) + l.getPriority().toString();
            if (l.getType().equals(AdorationMethodTypes.PHYSICAL.getAdorationMethodValue())) {
                //physical
                if (wasP) {
                    physical.append(" ");
                }
                wasP = true;
                physical.append(hString);
            } else {
                //online
                if (wasO) {
                    online.append(" ");
                }
                wasO = true;
                online.append(hString);
            }
        }
        cellOfRow14.setCellValue(physical.toString());
        cellOfRow15.setCellValue(online.toString());
    }

    private void updateAdorators(Workbook w) {
        Sheet sheet = w.getSheet("Adorálók");
        int rowCount = 1;
        List<Person> people = businessWithPerson.getPersonList();
        for (Person p : people) {
            Row row = getRow(sheet, rowCount);
            Cell c = getCell(row, 0);
            c.setCellValue(p.getId().toString());
            c.setCellType(CellType.NUMERIC);
            row.createCell(1).setCellValue(p.getName());
            row.createCell(2).setCellValue(AdoratorStatusTypes.getTranslatedString(p.getAdorationStatus()));
            row.createCell(3).setCellValue(p.getIsAnonymous());
            row.createCell(4).setCellValue(p.getMobile());
            row.createCell(5).setCellValue(p.getMobileVisible());
            row.createCell(6).setCellValue(p.getEmail());
            row.createCell(7).setCellValue(p.getEmailVisible());
            row.createCell(8).setCellValue(p.getAdminComment());
            row.createCell(9).setCellValue(p.getDhcSigned());
            row.createCell(10).setCellValue(p.getDhcSignedDate());
            row.createCell(11).setCellValue(p.getCoordinatorComment());
            row.createCell(12).setCellValue(p.getVisibleComment());
            row.createCell(13).setCellValue(p.getLanguageCode());
            List<Link> links = businessWithLink.getLinksOfPerson(p);
            if (links == null) {
                row.createCell(14).setCellValue("");
                row.createCell(15).setCellValue("");
            } else {
                fillCellsWithLinks(links, row.createCell(14), row.createCell(15));
            }
            rowCount++;
        }
    }

    private void updateCoverage(CurrentUserInformationJson userInformation, Workbook w) {
        CoverageInformationJson coverageInformationJson = coverageProvider.getCoverageInfo(userInformation);
        Sheet sheet = w.getSheet("Fedettség");
        //public Map<Integer,Integer> visibleHours; //hourId - number of adorators, only prio 1,2
        int rowCount = 39;
        for (int d = 0; d < 7; d++) {
            Row row = sheet.createRow(rowCount + d);
            for (int h = 0; h < BusinessWithLink.HOUR_IN_A_DAY; h++) {
                Integer noOfAdorators = coverageInformationJson.visibleHours.get(d * BusinessWithLink.HOUR_IN_A_DAY + h);
                Cell c = getCell(row, 4 + h);
                long cellValue = 2 - noOfAdorators.longValue();
                if (cellValue < 0) {
                    cellValue = 0;
                }
                c.setCellValue(cellValue);
                c.setCellType(CellType.NUMERIC);
            }
        }
    }

    /**
     * Get excel export for daily coordinators.
     *
     * @throws IOException if any issue happens
     */
    public void getExcelDailyInfo(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialDailyInfoXls(userInformation.languageCode);
        updateDailyInfo(w);
        reCalculateAllExcelCells(w);
        renameWorksheet("Hajnal", userInformation.getLanguageString("xls.night"), w);
        renameWorksheet("Délelőtt", userInformation.getLanguageString("xls.morning"), w);
        renameWorksheet("Délután", userInformation.getLanguageString("xls.afternoon"), w);
        renameWorksheet("Este", userInformation.getLanguageString("xls.evening"), w);
        w.setSheetHidden(w.getSheetIndex("Adatok"), true);
        w.write(outputStream);
    }

    private Workbook createInitialDailyInfoXls(final String languageCode) throws IOException {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        return getSpecificXlsTemplate(propertyDto.getBaseExcelFolder() + languageCode + propertyDto.getDailyInfoFileName());
    }

    private void updateDailyInfo(Workbook w) {
        //fill hour coordinators
        Sheet sheet = w.getSheet("Adatok");
        fillCellsWithHourlyCoordinators(sheet);
        //prepare data
        Map<Integer, Integer> posRecord = new HashMap<>();
        for (int i = Link.MIN_HOUR; i <= Link.MAX_HOUR; i++) { //this is about priority only
            posRecord.put(i, 0);
        }
        //fill data
        int colBase = 4;
        int rowBase = 3;
        for (int i = Link.MIN_HOUR; i <= Link.MAX_HOUR; i++) {
            List<Link> links = businessWithLink.getPhysicalLinksOfHour(i);
            if (links != null && !links.isEmpty()) {
                links.sort(Comparator.comparing(Link::getPriority));
                for (Link l : links) {
                    Cell cell = getSheetCell(sheet, rowBase + posRecord.get(i), colBase + i);
                    Long personId = l.getPersonId();
                    if (personId != null) {
                        Person p = businessWithPerson.getPersonById(personId);
                        cell.setCellValue(p.getId().toString() + " - " + p.getName() + "\n" + p.getMobile());
                        posRecord.put(i, posRecord.get(i) + 1);
                    }
                }
            }
        }
    }

    private void fillCellsWithHourlyCoordinators(Sheet sheet) {
        int rowPos = 3;
        int colPos = 2;
        List<Coordinator> coordinators = coordinatorProvider.getCoordinatorList();
        for (Coordinator coo : coordinators) {
            if (coo.getCoordinatorType() < 24) { //if hourly coordinator
                Long personId = coo.getPersonId();
                if (personId != null && personId.intValue() > 0) {
                    Person p = businessWithPerson.getPersonById(coo.getPersonId());
                    String cellValue = p.getId().toString() + " - " + p.getName() + "\n" + p.getMobile();
                    setCellOnSheet(sheet, rowPos + coo.getCoordinatorType(), colPos, cellValue);
                }
            }
        }
    }

    /**
     * Get excel export for hourly coordinators.
     *
     * @throws IOException if any issue happens
     */
    public void getExcelHourlyInfo(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialHourlyInfoXls(userInformation.languageCode);
        if (userInformation.personId != null) {
            //person is identified
            updateHourlyInfo(userInformation, userInformation.personId, w);
        }
        reCalculateAllExcelCells(w);
        renameWorksheet("Órakoordinátor", userInformation.getLanguageString("xls.hourCoordinator"), w);
        w.write(outputStream);
    }

    private Workbook createInitialHourlyInfoXls(final String languageCode) throws IOException {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        return getSpecificXlsTemplate(propertyDto.getBaseExcelFolder() + languageCode + propertyDto.getHourlyInfoFileName());
    }

    private void updateHourlyInfo(CurrentUserInformationJson currentUserInformationJson, Long personId, Workbook w) {
        Sheet sheet = w.getSheet("Órakoordinátor");
        Coordinator coordinator = businessWithCoordinator.getCoordinatorFromPersonId(personId);
        if (coordinator != null) {
            //coordinator has been identified
            Integer coordinatorType = coordinator.getCoordinatorType();
            if (coordinatorType < BusinessWithLink.HOUR_IN_A_DAY) {
                //person is an hourly coordinator, fill the xls
                Cell c = getSheetCell(sheet, 2, 2);
                c.setCellValue(coordinator.getCoordinatorType());
                Coordinator dailyCoo = businessWithCoordinator.getDailyCooOfHour(coordinatorType);
                if (dailyCoo != null) {
                    Person p = businessWithPerson.getPersonById(dailyCoo.getPersonId());
                    if (p != null) {
                        Row row = getRow(sheet, 6); //row of daily coo
                        c = getCell(row, 1);
                        c.setCellValue(p.getName());
                        c = getCell(row, 3);
                        c.setCellValue(p.getMobile());
                        c = getCell(row, 4);
                        c.setCellValue(p.getEmail());
                    }
                }
                Person p = businessWithPerson.getPersonById(coordinator.getPersonId());
                if (p != null) { // if we have the hourly coo person
                    Row row = getRow(sheet, 10); //hourly coo row
                    c = getCell(row, 1);
                    c.setCellValue(p.getName());
                    c = getCell(row, 3);
                    c.setCellValue(p.getMobile());
                    c = getCell(row, 4);
                    c.setCellValue(p.getEmail());
                    c = getCell(row, 5);
                    c.setCellValue(p.getVisibleComment());
                }
                //iterate through the adorators
                iterateThroughAdorators(currentUserInformationJson, coordinatorType, sheet);
            }
        }
    }

    private void iterateThroughAdorators(CurrentUserInformationJson currentUserInformationJson, Integer coordinatorType, Sheet sheet) {
        List<Link> links = businessWithLink.getLinksOfWeek(coordinatorType);
        int baseRow = 14;
        for (Link l : links) {
            Integer hourId = l.getHourId();
            int day = hourId / BusinessWithLink.HOUR_IN_A_DAY;
            String dayString = currentUserInformationJson.getLanguageString("common.day." + day);
            Person p = businessWithPerson.getPersonById(l.getPersonId());
            if (p != null) {
                Row row = getRow(sheet, baseRow); //adorator row
                Cell c = getCell(row, 1);
                c.setCellValue(dayString);
                c = getCell(row, 2);
                c.setCellValue(p.getName());
                c = getCell(row, 3);
                c.setCellValue(p.getMobile());
                c = getCell(row, 4);
                c.setCellValue(p.getEmail());
                c = getCell(row, 5);
                c.setCellValue(p.getCoordinatorComment());
                c = getCell(row, 6);
                c.setCellValue(p.getVisibleComment());
                baseRow++;
            }
        }
    }

    /**
     * Get excel export about the adorator him/herself.
     *
     * @throws IOException if any issue happens
     */
    public void getExcelAdoratorInfo(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialAdoratorInfoXls(userInformation.languageCode);
        if (userInformation.personId != null) {
            updateAdoratorInfo(userInformation.personId, userInformation, w);
        }
        reCalculateAllExcelCells(w);
        renameWorksheet("Adoráló", userInformation.getLanguageString("xls.adorator"), w);
        w.write(outputStream);
    }

    private Workbook createInitialAdoratorInfoXls(final String languageCode) throws IOException {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        return getSpecificXlsTemplate(propertyDto.getBaseExcelFolder() + languageCode + propertyDto.getAdoratorInfoFileName());
    }

    private void updateAdoratorInfo(Long personId, CurrentUserInformationJson currentUserInformationJson, Workbook w) {
        final String publicName = currentUserInformationJson.getLanguageString("xls.public");
        final String hiddenName = currentUserInformationJson.getLanguageString("xls.hidden");;
        Sheet sheet = w.getSheet("Adoráló");
        Person p = businessWithPerson.getPersonById(personId);
        setCellOnSheet(sheet, 2, 2, p.getName());
        if (!Boolean.TRUE.equals(p.getIsAnonymous())) {
            setCellOnSheet(sheet, 2, 3, publicName);
        } else {
            setCellOnSheet(sheet, 2, 3, hiddenName);
        }
        setCellOnSheet(sheet, 3, 2, AdoratorStatusTypes.getTranslatedString(p.getAdorationStatus()));
        setCellOnSheet(sheet, 4, 2, p.getId().toString());
        setCellOnSheet(sheet, 5, 2, p.getMobile());
        if (Boolean.TRUE.equals(p.getMobileVisible())) {
            setCellOnSheet(sheet, 5, 3, publicName);
        } else {
            setCellOnSheet(sheet, 5, 3, hiddenName);
        }
        setCellOnSheet(sheet, 6, 2, p.getEmail());
        if (Boolean.TRUE.equals(p.getEmailVisible())) {
            setCellOnSheet(sheet, 6, 3, publicName);
        } else {
            setCellOnSheet(sheet, 6, 3, hiddenName);
        }
        setCellOnSheet(sheet, 7, 2, p.getVisibleComment());
        //fill assigned hours
        int baseRow = 10;
        fillCellsWithAssignedHours(currentUserInformationJson, sheet, p, baseRow);
        //fill daily coordinators
        baseRow = 20;
        List<Coordinator> allCoordinators = businessWithCoordinator.getLeadership();
        for (Coordinator c : allCoordinators) {
            if (c.getPersonId() != null) {
                Person coo = businessWithPerson.getPersonById(c.getPersonId());
                if (coo != null) {
                    String coordinatorText = currentUserInformationJson.getLanguageString("coordinator." + c.getCoordinatorType().toString());
                    setCellOnSheet(sheet, baseRow, 1, coordinatorText);
                    setCellOnSheet(sheet, baseRow, 3, getPersonNameInformation(coo));
                    setCellOnSheet(sheet, baseRow, 4, getPersonMobileAndEmailInformation(currentUserInformationJson, coo));
                    baseRow++;
                }
            }
        }
        fillDateField(sheet); //fill date
    }

    private void fillDateField(Sheet sheet) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        setCellOnSheet(sheet, 30, 2, dtf.format(now));
    }

    private void fillCellsWithAssignedHours(CurrentUserInformationJson currentUserInformationJson, Sheet sheet, Person p, int baseRow) {
        int actualRow = baseRow;
        String hourString = " " + currentUserInformationJson.getLanguageString("common.hour");
        List<Link> links = businessWithLink.getLinksOfPerson(p);
        if (links != null) {  //have assigned hours to be filled
            for (Link l : links) {
                if (l.getType() >= 2) { //so if one-time hour
                    continue;
                }
                Integer hourId = l.getHourId();
                int day = hourId / BusinessWithLink.HOUR_IN_A_DAY;
                Integer hour = hourId - day * BusinessWithLink.HOUR_IN_A_DAY;
                String dayString = currentUserInformationJson.getLanguageString("common.day." + day); //  DAY_CODES.get(day);
                setCellOnSheet(sheet, actualRow, 1, dayString + " " + hour.toString() + hourString);
                Coordinator coo = businessWithCoordinator.getHourlyCooOfHour(hour);
                Person cooP = null;
                if (coo != null) {
                    cooP = businessWithPerson.getPersonById(coo.getPersonId());
                }
                if (cooP != null) {
                    setCellOnSheet(sheet, actualRow, 2, getPersonNameInformation(cooP));
                    setCellOnSheet(sheet, actualRow, 3, getPersonMobileAndEmailInformation(currentUserInformationJson, cooP));
                } else {
                    setCellOnSheet(sheet, actualRow, 2, THERE_IS_NO_INFORMATION);
                    setCellOnSheet(sheet, actualRow, 3, THERE_IS_NO_INFORMATION);
                }
                //fill previous hour
                Integer previousHour = businessWithLink.getPreviousHour(hourId);
                fillNeighbourHour(currentUserInformationJson, sheet, previousHour, actualRow, 4);
                //fill next hour
                Integer nextHour = businessWithLink.getNextHour(hourId);
                fillNeighbourHour(currentUserInformationJson, sheet, nextHour, actualRow, 6);

                actualRow++;
            }
        }
    }

    private void fillNeighbourHour(CurrentUserInformationJson currentUserInformationJson, Sheet sheet, Integer previousHour, int baseRow, int baseCol) {
        List<Link> previousLinks = businessWithLink.getPhysicalLinksOfHour(previousHour);
        if (previousLinks != null && !previousLinks.isEmpty()) {
            Link aPreviousLink = previousLinks.get(0);
            Person aPreviousPerson = businessWithPerson.getPersonById(aPreviousLink.getPersonId());
            if (aPreviousPerson != null) {
                setCellOnSheet(sheet, baseRow, baseCol, getPersonNameInformation(aPreviousPerson));
                setCellOnSheet(sheet, baseRow, baseCol + 1, getPersonMobileAndEmailInformation(currentUserInformationJson, aPreviousPerson));
            } else {
                setCellOnSheet(sheet, baseRow, baseCol, THERE_IS_NO_INFORMATION);
                setCellOnSheet(sheet, baseRow, baseCol + 1, THERE_IS_NO_INFORMATION);
            }
        }
    }

    private String getPersonNameInformation(Person p) {
        String name;
        if (!Boolean.TRUE.equals(p.getIsAnonymous())) {
            name = p.getName();
        } else {
            name = "Anoním";
        }
        return name;
    }

    private String getPersonMobileAndEmailInformation(CurrentUserInformationJson currentUserInformationJson, Person p) {
        String mobile;
        String telString = currentUserInformationJson.getLanguageString("xls.ph");
        if (Boolean.TRUE.equals(p.getMobileVisible())) {
            mobile = p.getMobile();
        } else {
            mobile = THERE_IS_NO_INFORMATION;
        }
        String email;
        if (Boolean.TRUE.equals(p.getEmailVisible())) {
            email = p.getEmail();
        } else {
            email = THERE_IS_NO_INFORMATION;
        }
        return telString + mobile + " / e-mail: " + email;
    }

    private void renameWorksheet(String fromString, String toString, Workbook w) {
        w.setSheetName(w.getSheetIndex(fromString), toString);
    }

}
