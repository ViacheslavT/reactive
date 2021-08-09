package webflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WebClient webClient;

    @Autowired
    public UserService(UserRepository userRepository, WebClient webClient) {
        this.userRepository = userRepository;
        this.webClient = webClient;
    }

    public Mono<User> saveUserToRepository(User user) {
        return userRepository.save(user);
    }

    public Flux<User> getAllUsers(String vuNum) {
        return userRepository.findByNameEndsWith(vuNum);
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }

    public Flux<User> retrieveUsersFromAPI(String vuNum) {
        List<Mono<User>> requests = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            requests.add(retrieveUser(vuNum));
        }
        return Flux.merge(requests)
                .parallel()
                .runOn(Schedulers.boundedElastic()).sequential();
    }

    public Mono<User> retrieveUser(String vuNum) {
        return webClient.get()
                .uri("/user?vuNum={vuNum}", vuNum)
                .retrieve()
                .bodyToMono(User.class).onErrorReturn(User.builder().error(true).build());
    }
}
