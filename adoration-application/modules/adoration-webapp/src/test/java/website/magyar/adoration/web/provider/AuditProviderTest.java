package website.magyar.adoration.web.provider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import website.magyar.adoration.database.business.BusinessWithAuditTrail;
import website.magyar.adoration.web.helper.DummyTestObject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

/**
 * Unit test for {@link AuditProvider} class.
 */
public class AuditProviderTest {

    @Mock
    private BusinessWithAuditTrail businessWithAuditTrail;

    @InjectMocks
    private AuditProvider underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest, "businessWithAuditTrail", businessWithAuditTrail);

    }

    @Test
    public void getAuditTrailOfLastDays() {
        //given
        List<DummyTestObject> expected = new ArrayList<>();
        doReturn(expected).when(businessWithAuditTrail).getAuditTrailOfLastDays(1L);
        //when
        Object result = underTest.getAuditTrailOfLastDays(1L);
        //then
        assertEquals(expected, result);
    }
}