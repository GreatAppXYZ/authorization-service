package xyz.greatapp.authorization;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static xyz.my_app.libs.service.ServiceName.DATABASE;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.RestTemplate;
import xyz.greatapp.authorization.filters.SecurityFilter;
import xyz.my_app.libs.service.ServiceResult;
import xyz.my_app.libs.service.context.ThreadContextService;
import xyz.my_app.libs.service.context.ThreadContextServiceImpl;
import xyz.my_app.libs.service.filters.ContextFilter;
import xyz.my_app.libs.service.location.ServiceLocator;
import xyz.my_app.libs.service.requests.database.Filter;
import xyz.my_app.libs.service.requests.database.SelectQuery;

@Configuration
public class AppConfiguration extends GlobalAuthenticationConfigurerAdapter
{
    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(getUserDetailsService());
    }

    @Bean
    UserDetailsService getUserDetailsService() {
        return username ->
        {
            JSONObject user = getUser(username);
            if(user.has("email")) {
                return new User(
                        user.getString("email"),
                        user.getString("password"),
                        true, true, true, true,
                        createAuthorityList());
            } else {
                throw new BadCredentialsException("BadCredentialsException");
            }
        };
    }

    private JSONObject getUser(String email)
    {
        Filter[] filters = new Filter[] {
                new Filter("email", email)
        };

        HttpEntity<SelectQuery> entity = new HttpEntity<>(
                new SelectQuery("users", filters),
                getHttpHeaders());
        ResponseEntity<ServiceResult> responseEntity = getRestTemplate().postForEntity(
                getServiceLocator().getServiceURI(DATABASE, getThreadContextService().getEnvironment()) + "/select",
                entity,
                ServiceResult.class);

        return new JSONObject(responseEntity.getBody().getObject());
    }

    private HttpHeaders getHttpHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private RestTemplate getRestTemplate()
    {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = new ArrayList<>();
        list.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(list);
        return restTemplate;
    }

    @Bean
    public ThreadContextService getThreadContextService()
    {
        return new ThreadContextServiceImpl();
    }

    @Bean
    public ServiceLocator getServiceLocator()
    {
        return new ServiceLocator();
    }

    @Bean
    public ContextFilter getContextFilter(ThreadContextService threadContextService)
    {
        return new ContextFilter(threadContextService);
    }

    @Bean
    public SecurityFilter getSecurityFilter()
    {
        return new SecurityFilter();
    }
}
