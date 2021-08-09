package webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRoutes {

    @Bean
    public RouterFunction<ServerResponse> routes(UserHandler handler) {
        return route().path(
                "/web-flux", builder -> builder
                        .GET("/retrieve", handler::retrieveUsers)
                        .POST("/save", handler::saveUser)
                        .GET("/users", handler::findAllUsers)
                        .DELETE("/delete", handler::deleteUser)
        ).build();
    }
}
