package app.provider.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;


@Slf4j
@Component
public class ServerConfig implements ApplicationListener<WebServerInitializedEvent> {
    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {

        InetAddress host = null;
        try{
            host = InetAddress.getLocalHost();
            log.info(host.getHostAddress());
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }

        log.info(host.getHostAddress() + " : " + webServerInitializedEvent.getWebServer().getPort());
    }
}
