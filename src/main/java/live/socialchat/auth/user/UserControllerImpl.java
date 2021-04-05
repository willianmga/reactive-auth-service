package live.socialchat.auth.user;

import live.socialchat.auth.authentication.model.PasswordUpdateRequest;
import live.socialchat.auth.authentication.model.ServerResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/v1/user")
public class UserControllerImpl implements UserController {
    
//    @PostMapping
//    @RequestMapping("/password/update")
//    public Mono<ServerResponse> requestPasswordUpdate(@RequestBody final PasswordUpdateRequest passwordUpdateRequest) {
//
//    }
//
}
