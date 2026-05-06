package workspaceweb.membership;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import workspaceweb.user.UserService;
import workspaceweb.workspace.WorkspaceService;

@Service
@Transactional
public class MembershipService {

    private final WorkspaceService workspaces;
    private final UserService users;

    public MembershipService(WorkspaceService workspaces, UserService users) {
        this.workspaces = workspaces;
        this.users = users;
    }

    public void add(Long workspaceId, Long userId) {
        workspaces.get(workspaceId).addMember(users.get(userId));
    }

    public void suspend(Long workspaceId, Long userId) {
        workspaces.get(workspaceId).suspendMember(users.get(userId));
    }

    public void remove(Long workspaceId, Long userId) {
        workspaces.get(workspaceId).removeMember(users.get(userId));
    }
}
