package computed.user;

import computed.workspace.WorkspaceMember;

/**
 * Bridge that lets the workspace package mutate a User's membership set and
 * trigger the on-search recompute without exposing those operations as part
 * of User's public API.
 */
public final class UserMembershipAccess {

    private UserMembershipAccess() {
    }

    public static void add(User user, WorkspaceMember membership) {
        user.addMembership(membership);
    }

    public static void remove(User user, WorkspaceMember membership) {
        user.removeMembership(membership);
    }

    public static void recompute(User user) {
        user.recomputeOnSearch();
    }
}
