package website.magyar.adoration.database.business.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Supporter class to convert special date values to String values.
 */
public class DateTimeConverter {
    public static final long HOUR_IN_MS = 1000L * 60 * 60;

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final long DAY_IN_MS = HOUR_IN_MS * 24;

    /**
     * Gets actual date & time as standard String.
     *
     * @return with the string format of current date & time
     */
    public String getCurrentDateTimeAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        String format;
        format = simpleDateFormat.format(new Date());
        return format;
    }

    /**
     * Gets the actual date as standard String.
     *
     * @return with the string format of the current date.
     */
    public String getCurrentDateAsString() {
        return getDateAsString(new Date());
    }

    /**
     * Gets the standard String format of a given date.
     *
     * @param date is the give date & time
     * @return with its string format
     */
    public String getDateAsString(final Date date) {
        String dateStr = "";
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
            dateStr = simpleDateFormat.format(date);
        }
        return dateStr;
    }

    /**
     * Get date N days ago.
     *
     * @param n number of days to count back
     * @return with the Date N days ago
     */
    public Date getDateNDaysAgo(final long n) {
        return new Date(System.currentTimeMillis() - (n * DAY_IN_MS));
    }

    /**
     * Convert YYYY-MM-DD string to Date object.
     */
    public Date getDate(final String dateString) throws ParseException {
        if (dateString == null) {
            throw new ParseException("Null string received.", 0);
        }
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        return formatter.parse(dateString);
    }
}
