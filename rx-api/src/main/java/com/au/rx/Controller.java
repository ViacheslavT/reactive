package com.au.rx;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rx")
public class Controller {

    private final UserService userService;

    @Autowired
    public Controller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/retrieve", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<ResponseEntity<Response>> retrieveUsers(@RequestParam String vuNum) {
        return userService.retrieveUsersFromAPI(vuNum).subscribeOn(Schedulers.io())
                .map(list -> ResponseEntity.ok(Response.builder().users(list).success(true).build()));
    }

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<ResponseEntity<Response>> save(@RequestBody User user) {
        return userService.saveUserToRepository(user).subscribeOn(Schedulers.io()).map(
                s -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(Response.builder().user(user).success(true).build()));
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<ResponseEntity<Response>> findAllUsers(@RequestParam String vuNum) {
        return userService.getAllUsers(vuNum).subscribeOn(Schedulers.io())
                .map(list -> ResponseEntity.ok(Response.builder().users(list).success(true).build()));
    }

    @DeleteMapping(value = "/delete")
    public Single<ResponseEntity<Response>> deleteUser(@RequestParam String id) {
        return userService.deleteUser(id).subscribeOn(Schedulers.io()).map(
                s -> ResponseEntity.status(HttpStatus.OK)
                        .body(Response.builder().success(true).build()));
    }
}
