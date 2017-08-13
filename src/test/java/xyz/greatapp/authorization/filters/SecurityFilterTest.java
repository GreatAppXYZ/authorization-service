package xyz.greatapp.authorization.filters;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecurityFilterTest
{
    private SecurityFilter securityFilter;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;

    @Before
    public void setUp() throws Exception
    {
        securityFilter = new SecurityFilter();
        given(httpServletRequest.getServerName()).willReturn("localhost");
        given(httpServletRequest.getMethod()).willReturn("GET");
    }

    @Test
    public void shouldSetAccessControlHeadersForValidClientsUsingOrigin() throws IOException, ServletException
    {
        verifyAccessControlHeadersUsingOrigin("http://localhost:8080");
        verifyAccessControlHeadersUsingOrigin("http://test.localhost:8080");
        verifyAccessControlHeadersUsingOrigin("http://www.greatapp.xyz");
        verifyAccessControlHeadersUsingOrigin("http://uat.greatapp.xyz");
    }

    private void verifyAccessControlHeadersUsingOrigin(String origin) throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getHeader("origin")).willReturn(origin);

        // when
        securityFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Max-Age", "360");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Origin", origin);
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Credentials", "true");
    }

    @Test
    public void shouldSetAccessControlHeadersForValidClientsUsingReferer() throws IOException, ServletException
    {
        verifyAccessControlHeadersUsingReferer("http://localhost:8080");
        verifyAccessControlHeadersUsingReferer("http://test.localhost:8080");
        verifyAccessControlHeadersUsingReferer("http://www.greatapp.xyz");
        verifyAccessControlHeadersUsingReferer("http://uat.greatapp.xyz");
    }

    private void verifyAccessControlHeadersUsingReferer(String origin) throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getHeader("origin")).willReturn(null);
        given(httpServletRequest.getHeader("referer")).willReturn(origin);

        // when
        securityFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Max-Age", "360");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Origin", origin);
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Credentials", "true");
    }
}
