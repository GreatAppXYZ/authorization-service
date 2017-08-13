package xyz.greatapp.authorization;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.security.Principal;

import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableResourceServer
public class AuthorizationController
{
    @RequestMapping(method = GET, value = "/user")
    public Principal getUser(Principal user)
    {
        return user;
    }
}
