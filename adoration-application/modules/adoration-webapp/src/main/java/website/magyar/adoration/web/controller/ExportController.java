package website.magyar.adoration.web.controller;

import website.magyar.adoration.web.controller.helper.ControllerBase;
import website.magyar.adoration.web.controller.helper.enums.ExcelExportType;
import website.magyar.adoration.web.json.CurrentUserInformationJson;
import website.magyar.adoration.web.provider.CurrentUserProvider;
import website.magyar.adoration.web.provider.ExcelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * Controller for handling requests for the application pages about Exporting data.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class ExportController extends ControllerBase {
    private static final String ATTACHMENT_TEMPLATE = "attachment; filename=%s";

    private final Logger logger = LoggerFactory.getLogger(ExportController.class);

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private ExcelProvider excelProvider;

    /**
     * Serves request to get full export to Excel.
     */
    @GetMapping(value = "/adorationSecure/getExcelFull")
    public void getExcelContent(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        prepareAndSendExcelResponse(httpSession, httpServletResponse, ExcelExportType.BIG_INFO, isAdoratorAdmin(currentUserProvider, httpSession));
    }

    /**
     * Serves request to get full export to Excel.
     */
    @GetMapping(value = "/adorationSecure/getExcelDailyInfo")
    public void getExcelDailyInfo(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        prepareAndSendExcelResponse(httpSession, httpServletResponse, ExcelExportType.DAILY_INFO, isPrivilegedAdorator(currentUserProvider, httpSession));
    }

    /**
     * Serves request to get hourly coordinator info.
     */
    @GetMapping(value = "/adorationSecure/getExcelHourlyInfo")
    public void getExcelHourlyInfo(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        prepareAndSendExcelResponse(httpSession, httpServletResponse, ExcelExportType.HOURLY_INFO, isPrivilegedAdorator(currentUserProvider, httpSession));
    }

    /**
     * Serves request to get adorator info.
     */
    @GetMapping(value = "/adorationSecure/getExcelAdoratorInfo")
    public void getExcelAdoratorInfo(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        prepareAndSendExcelResponse(httpSession, httpServletResponse, ExcelExportType.ADORATOR_INFO, isRegisteredAdorator(currentUserProvider, httpSession));
    }

    private void prepareAndSendExcelResponse(HttpSession httpSession, HttpServletResponse httpServletResponse, ExcelExportType excelExportType, boolean isAllowed) {
        var templateFileName = excelExportType.getTemplateName();
        httpServletResponse.addHeader(CONTENT_DISPOSITION, String.format(ATTACHMENT_TEMPLATE, templateFileName));
        httpServletResponse.addHeader(CONTENT_TYPE, CONTENT_TYPE_XLSX);
        if (isAllowed) {
            try {
                httpServletResponse.setStatus(HttpStatus.OK.value());
                CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
                ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
                switch (excelExportType) {
                case BIG_INFO:
                    excelProvider.getExcelFull(currentUserInformationJson, servletOutputStream);
                    break;
                case DAILY_INFO:
                    excelProvider.getExcelDailyInfo(currentUserInformationJson, servletOutputStream);
                    break;
                case HOURLY_INFO:
                    excelProvider.getExcelHourlyInfo(currentUserInformationJson, servletOutputStream);
                    break;
                default:
                case ADORATOR_INFO:
                    excelProvider.getExcelAdoratorInfo(currentUserInformationJson, servletOutputStream);
                    break;
                }
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue at xls export: {}", templateFileName, e);
            }
        } else {
            try {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue at xls export preparation: {}", templateFileName, e);
            }
        }
    }

}
