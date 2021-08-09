package rx;

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

import java.util.List;

@RestController
@RequestMapping(value = "/usual")
public class Controller {

    private final UserService userService;

    @Autowired
    public Controller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/retrieve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> retrieveUsers(@RequestParam String vuNum) {
        return ResponseEntity.ok(userService.retrieveUsersFromAPI(vuNum));
    }

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> save(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.saveUserToRepository(user));
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> findAllUsers(@RequestParam String vuNum) {
        return ResponseEntity.ok(userService.getAllUsers(vuNum));
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam String id) {
        userService.deleteUser(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }
}
