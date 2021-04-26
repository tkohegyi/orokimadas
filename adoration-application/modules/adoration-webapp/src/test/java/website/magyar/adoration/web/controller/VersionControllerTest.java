package website.magyar.adoration.web.controller;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import website.magyar.adoration.configuration.VersionTitleProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import org.junit.Test;
import org.junit.Before;

/**
 * Unit test for {@link VersionController}.
 */
public class VersionControllerTest {

    @Mock
    private VersionTitleProvider titleProvider;

    @InjectMocks
    private VersionController underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetVersionShouldReturnVersionResponse() {
        //GIVEN
        given(titleProvider.getVersionTitle()).willReturn("version");
        //WHEN
        ResponseEntity<String> result = underTest.getVersion();
        //THEN
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("{\"adorationApplicationVersion\":\"version\"}", result.getBody());
        assertEquals(MediaType.APPLICATION_JSON, result.getHeaders().getContentType());
    }

}
