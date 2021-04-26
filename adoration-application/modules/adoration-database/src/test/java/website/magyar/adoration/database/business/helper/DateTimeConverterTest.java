package website.magyar.adoration.database.business.helper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class DateTimeConverterTest {

    DateTimeConverter dateTimeConverter; //under test

    @Before
    public void setUp() {
        dateTimeConverter = new DateTimeConverter();
    }

    @Test(expected = ParseException.class)
    public void getDateBadStringNull() throws ParseException {
        dateTimeConverter.getDate((String) null);
    }

    @Test(expected = ParseException.class)
    public void getDateBadStringWrongText() throws ParseException {
        dateTimeConverter.getDate("ftzus");
    }

    @Test
    public void getDateOk() throws ParseException {
        Date date = dateTimeConverter.getDate("2021-04-02");
        Assert.assertTrue(date.before(new Date(System.currentTimeMillis())));
    }

}