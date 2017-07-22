#!/bin/bash
java $JAVA_OPTS \
    -Dserver.port=$PORT \
    -Deureka.client.service-url.defaultZone=https://greatapp-discovery-service.herokuapp.com/eureka/ \
    -Deureka.instance.hostname=auth.greatapp.xyz \
    -Deureka.instance.prefer-ip-address=false \
    -Dspring.cloud.config.uri=https://greatapp-configuration-service.herokuapp.com/ \
    -jar app.jar \
