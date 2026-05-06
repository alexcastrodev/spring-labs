package workspaceweb.user;

import workspaceweb.membership.WorkspaceMember;

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
