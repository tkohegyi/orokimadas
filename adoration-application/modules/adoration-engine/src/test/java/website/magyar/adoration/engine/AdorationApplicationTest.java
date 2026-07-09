package website.magyar.adoration.engine;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.springframework.test.util.ReflectionTestUtils;
import website.magyar.adoration.bootstrap.AdorationBootstrap;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

/**
 * Test class of {@link AdorationApplication} class.
 */
public class AdorationApplicationTest {

    private final String[] arguments = {"blah", "blah"};

    @Test
    public void testMain() {
        try (MockedConstruction<AdorationBootstrap> mockedBootstrap = mockConstruction(AdorationBootstrap.class,
                (mock, context) -> doNothing().when(mock).bootstrap(arguments))) {
            AdorationApplication.main(arguments);

            assertArrayEquals(arguments, (String[]) ReflectionTestUtils.getField(AdorationApplication.class, "arguments"));
            verify(mockedBootstrap.constructed().get(0)).bootstrap(arguments);
        }
    }
}
