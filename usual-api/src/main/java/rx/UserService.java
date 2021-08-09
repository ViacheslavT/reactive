package rx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String baseUrl;

    @Autowired
    public UserService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public User saveUserToRepository(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers(String vuNum) {
        return userRepository.findByNameEndsWith(vuNum);
    }

    public void deleteUser(Long id) {
        Optional<User> optionalAuthor = userRepository.findById(id);
        if (optionalAuthor.isEmpty())
            throw new EntityNotFoundException();
        else {
            userRepository.deleteById(id);
        }
    }

    public List<User> retrieveUsersFromAPI(String vuNum) {
        List<CompletableFuture<User>> requests = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            requests.add(CompletableFuture.supplyAsync(() -> asyncUserRetrieve(vuNum)));
        }
        return requests.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    private User asyncUserRetrieve(String vuNum) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("vuNum", "{vuNum}")
                .buildAndExpand(vuNum)
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .build();
        ResponseEntity<User> response;
        try {
            response = restTemplate.exchange(requestEntity, User.class);
        } catch (HttpStatusCodeException e) {
            return User.builder().error(true).build();
        }
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        return User.builder().error(true).build();
    }
}
