package webflux;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Service
public class UserHandler {

    private final UserService service;

    public UserHandler(UserService service) {
        this.service = service;
    }

    public Mono<ServerResponse> retrieveUsers(ServerRequest req) {
        return ok().body(service.retrieveUsersFromAPI(req.queryParam("vuNum").orElse("1")), User.class);
    }

    public Mono<ServerResponse> saveUser(ServerRequest req) {
        return req.bodyToMono(User.class).flatMap(user -> ok().body(
                service.saveUserToRepository(user),
                User.class
        ));
    }

    public Mono<ServerResponse> findAllUsers(ServerRequest req) {
        return ok().body(
                service.getAllUsers(
                        req.queryParam("vuNum")
                                .orElseThrow(() -> new ServerException("Can't find user with passed id"))
                ),
                User.class
        );
    }

    public Mono<ServerResponse> deleteUser(ServerRequest req) {
        return noContent().build(service.deleteUser(
                req.queryParam("id")
                        .map(Long::valueOf)
                        .orElseThrow(() -> new ServerException("Can't delete user with passed id"))
        ));
    }
}
