package workspaceweb.web;

import java.util.List;

import workspaceweb.membership.MembershipStatus;
import workspaceweb.membership.WorkspaceMember;
import workspaceweb.user.User;
import workspaceweb.workspace.Workspace;

public final class Dtos {

    private Dtos() {
    }

    public record CreateUserRequest(String fullName) {
    }

    public record CreateWorkspaceRequest(String name) {
    }

    public record UserResponse(Long id, String fullName, boolean onSearch, List<MembershipView> memberships) {
        public static UserResponse from(User user) {
            List<MembershipView> views = user.getMemberships().stream()
                    .map(MembershipView::from)
                    .toList();
            return new UserResponse(user.getId(), user.getFullName(), user.isOnSearch(), views);
        }
    }

    public record WorkspaceResponse(Long id, String name) {
        public static WorkspaceResponse from(Workspace w) {
            return new WorkspaceResponse(w.getId(), w.getName());
        }
    }

    public record MembershipView(Long workspaceId, MembershipStatus status) {
        public static MembershipView from(WorkspaceMember m) {
            return new MembershipView(m.getWorkspace().getId(), m.getStatus());
        }
    }
}
