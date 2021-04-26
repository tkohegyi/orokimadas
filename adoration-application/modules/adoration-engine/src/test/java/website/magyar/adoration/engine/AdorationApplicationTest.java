package website.magyar.adoration.engine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import website.magyar.adoration.bootstrap.AdorationBootstrap;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Test class of {@link AdorationApplication} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AdorationApplication.class)
public class AdorationApplicationTest {

    private final String[] arguments = {"blah", "blah"};

    @Mock
    private AdorationBootstrap adorationBootstrap;

    @InjectMocks
    private AdorationApplication underTest;

    @Before
    public void setUp() throws Exception {
        underTest = PowerMockito.mock(AdorationApplication.class);
        whenNew(AdorationApplication.class).withNoArguments().thenReturn(underTest);
        adorationBootstrap = spy(new AdorationBootstrap());
        whenNew(AdorationBootstrap.class).withNoArguments().thenReturn(adorationBootstrap);
        doNothing().when(adorationBootstrap).bootstrap(arguments);
    }

    @Test
    public void testMain() {
        AdorationApplication.main(arguments);
        assertArrayEquals(arguments, Whitebox.getInternalState(AdorationApplication.class, "arguments"));
        verify(adorationBootstrap).bootstrap(arguments);
    }
}