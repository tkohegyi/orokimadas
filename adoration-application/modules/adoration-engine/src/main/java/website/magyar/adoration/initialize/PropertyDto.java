package website.magyar.adoration.initialize;

/**
 * Holds module specific properties.
 */
public class PropertyDto {

    private final Integer port;

    /**
     * Constructs a new property holding object with the given fields.
     *
     * @param port the port used by the web application
     */
    public PropertyDto(final Integer port) {
        super();
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

}
