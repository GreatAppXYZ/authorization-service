package xyz.greatapp.authorization.filters;

import static java.lang.Integer.MIN_VALUE;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Order(MIN_VALUE + 1)
public class SecurityFilter extends GenericFilterBean
{
    private final List<String> WHITE_LIST_CLIENTS = asList(
            "http://localhost",
            "https://localhost",
            "http://test.localhost",
            "https://test.localhost",
            "http://www.greatapp.xyz",
            "https://www.greatapp.xyz",
            "http://uat.greatapp.xyz",
            "https://uat.greatapp.xyz");

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (isNotValidClientUrl(httpServletRequest.getHeader("origin")))
        {
            System.out.println("Invalid Origin: " + httpServletRequest.getHeader("origin"));
            ((HttpServletResponse) response).sendError(SC_FORBIDDEN, "Client not allowed: " + httpServletRequest.getServerName());
        }
        setCORSHeaders(httpServletResponse, httpServletRequest);
        if (!httpServletRequest.getMethod().equals("OPTIONS"))
        {
            chain.doFilter(request, response);
        }
    }

    private void setCORSHeaders(HttpServletResponse response, HttpServletRequest request)
    {
        if(response.getHeader("Access-Control-Allow-Headers") == null) response.addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization");
        if(response.getHeader("Access-Control-Max-Age") == null) response.addHeader("Access-Control-Max-Age", "360");
        if(response.getHeader("Access-Control-Allow-Methods") == null) response.addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        if(response.getHeader("Access-Control-Allow-Origin") == null) response.addHeader("Access-Control-Allow-Origin", getClientUrl(request));
        if(response.getHeader("Access-Control-Allow-Credentials") == null) response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    private boolean isNotValidClientUrl(String serverName)
    {
        return WHITE_LIST_CLIENTS.indexOf(serverName) < 0;
    }

    private String getClientUrl(HttpServletRequest request)
    {
        if (nonNull(request.getHeader("origin")))
        {
            return request.getHeader("origin");
        }
        else if (nonNull(request.getHeader("referer")))
        {
            return request.getHeader("referer");
        }
        return "";
    }
}
