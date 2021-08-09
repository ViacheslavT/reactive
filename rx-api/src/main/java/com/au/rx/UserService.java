package com.au.rx;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public UserService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public Single<User> saveUserToRepository(User user) {
        return Single.create(singleSubscriber -> {
            Optional<User> optionalAuthor = userRepository.findById(user.getId());
            if (optionalAuthor.isEmpty()) {
                singleSubscriber.onSuccess(userRepository.save(user));
            }
        });
    }

    public Single<List<User>> getAllUsers(String vuNum) {
        return Single.create(singleSubscriber -> {
            List<User> users = userRepository.findByIdEndsWith(vuNum);
            singleSubscriber.onSuccess(users);
        });
    }

    public Single<Boolean> deleteUser(String id) {
        return Single.create(subscriber -> {
            Optional<User> optionalAuthor = userRepository.findById(id);
            if (optionalAuthor.isEmpty())
                subscriber.onError(new EntityNotFoundException());
            else {
                userRepository.deleteById(id);
                subscriber.onSuccess(true);
            }
        });
    }

    public Single<List<User>> retrieveUsersFromAPI(String vuNum) {
        return Single.create(singleSubscriber -> {
            List<Integer> iterations = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                iterations.add(i);
            }
            List<User> users = Observable.fromIterable(iterations)
                    .flatMap(iteration -> asyncUserRetrieve(vuNum)
                            .subscribeOn(Schedulers.io()))
                    .toList()
                    .blockingGet();
            singleSubscriber.onSuccess(users);
        });
    }

    private Observable<User> asyncUserRetrieve(String vuNum) {
        return Observable.fromCallable(() -> {
            URI uri = UriComponentsBuilder.fromUriString("http://127.0.0.1:8080/user")
                    .queryParam("vuNum", "{vuNum}")
                    .buildAndExpand(vuNum)
                    .toUri();
            RequestEntity<Void> requestEntity = RequestEntity
                    .get(uri)
                    .build();
            ResponseEntity<User> response = restTemplate.exchange(requestEntity, User.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            return User.builder().error(true).build();

        });
    }

}
