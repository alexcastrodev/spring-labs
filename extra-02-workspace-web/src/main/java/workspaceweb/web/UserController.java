package workspaceweb.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import workspaceweb.user.User;
import workspaceweb.user.UserService;
import workspaceweb.web.Dtos.CreateUserRequest;
import workspaceweb.web.Dtos.UserResponse;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        User created = users.create(request.fullName());
        return ResponseEntity
                .created(URI.create("/users/" + created.getId()))
                .body(UserResponse.from(created));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return UserResponse.from(users.get(id));
    }
}
