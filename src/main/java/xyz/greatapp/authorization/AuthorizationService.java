package xyz.greatapp.authorization;

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.location.ServiceLocator;
import xyz.greatapp.libs.service.requests.common.ApiClientUtils;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;

@Component
class AuthorizationService
{
    @Autowired
    private ServiceLocator serviceLocator;

    @Autowired
    private ThreadContextService threadContextService;

    ServiceResult getUser(String email)
    {
        ApiClientUtils apiClientUtils = new ApiClientUtils();

        ColumnValue[] filters = new ColumnValue[] {
                new ColumnValue("email", email)
        };

        HttpEntity<SelectQueryRQ> entity = new HttpEntity<>(
                new SelectQueryRQ("users", filters),
                apiClientUtils.getHttpHeaders());
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }
}
