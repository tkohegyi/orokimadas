package website.magyar.adoration.configuration;

/**
 * Holds module specific properties.
 */
public class PropertyDto {

    private final String smtpServer;
    private final String smtpPort;
    private final String smtpUserName;
    private final String smtpPassword;
    private final String emailFrom;
    private final String emailTo;

    /**
     * Constructs a new property holding object with the given fields.
     */
    public PropertyDto(final String smtpServer, final String smtpPort, final String smtpUserName,
                       final String smtpPassword, final String emailFrom, final String emailTo) {
        super();
        this.smtpServer = smtpServer;
        this.smtpPort = smtpPort;
        this.smtpUserName = smtpUserName;
        this.smtpPassword = smtpPassword;
        this.emailFrom = emailFrom;
        this.emailTo = emailTo;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public String getSmtpUserName() {
        return smtpUserName;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public String getEmailTo() {
        return emailTo;
    }

}
