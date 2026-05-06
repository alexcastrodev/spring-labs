package workspaceweb.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import workspaceweb.membership.MembershipService;

@RestController
@RequestMapping("/workspaces/{workspaceId}/members/{userId}")
public class MembershipController {

    private final MembershipService memberships;

    public MembershipController(MembershipService memberships) {
        this.memberships = memberships;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@PathVariable Long workspaceId, @PathVariable Long userId) {
        memberships.add(workspaceId, userId);
    }

    @PostMapping("/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable Long workspaceId, @PathVariable Long userId) {
        memberships.suspend(workspaceId, userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long workspaceId, @PathVariable Long userId) {
        memberships.remove(workspaceId, userId);
    }
}
