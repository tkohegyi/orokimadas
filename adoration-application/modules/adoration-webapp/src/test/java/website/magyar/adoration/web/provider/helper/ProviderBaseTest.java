package website.magyar.adoration.web.provider.helper;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link ProviderBase} class.
 */
public class ProviderBaseTest {

    private ProviderBase underTest;

    @Before
    public void setUp() {
        underTest = Mockito.spy(new ProviderBase());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsLongChangedNullNull() {
        boolean result = underTest.isLongChanged(null, null);
        assertFalse(result);
    }

    @Test
    public void testIsLongChangedNullA() {
        Long a = 0L;
        boolean result = underTest.isLongChanged(null, a);
        assertTrue(result);

    }

    @Test
    public void testIsLongChangedANull() {
        Long a = 0L;
        boolean result = underTest.isLongChanged(a, null);
        assertTrue(result);
    }

    @Test
    public void testIsLongChangedSame() {
        Long a = 0L;
        Long b = 0L;
        boolean result = underTest.isLongChanged(a, b);
        assertFalse(result);
    }

    @Test
    public void testIsLongChangedDifferent() {
        Long a = 0L;
        Long b = 1L;
        boolean result = underTest.isLongChanged(a, b);
        assertTrue(result);
    }

}