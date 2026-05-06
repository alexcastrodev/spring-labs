package computed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import computed.user.User;
import computed.user.UserRepository;
import computed.workspace.MembershipStatus;
import computed.workspace.Workspace;
import computed.workspace.WorkspaceMember;
import computed.workspace.WorkspaceRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
class UserOnSearchTests {

    @Autowired
    private UserRepository users;

    @Autowired
    private WorkspaceRepository workspaces;

    @Autowired
    private EntityManager em;

    @Test
    void newUserWithoutWorkspaceIsNotOnSearch() {
        User saved = users.save(new User("Alice"));
        em.flush();
        em.clear();

        assertFalse(users.findById(saved.getId()).orElseThrow().isOnSearch());
    }

    @Test
    void addingMemberRecomputesOnSearchToTrue() {
        User user = users.save(new User("Alice"));
        Workspace workspace = workspaces.save(new Workspace("Acme"));

        workspace.addMember(user);
        em.flush();
        em.clear();

        User reloaded = users.findById(user.getId()).orElseThrow();
        assertTrue(reloaded.isOnSearch());
        assertEquals(1, reloaded.getMemberships().size());
        assertTrue(reloaded.getMemberships().iterator().next().isActive());
    }

    @Test
    void removingMembershipDeletesRowAndRecomputesOnSearchToFalse() {
        User user = users.save(new User("Alice"));
        Workspace workspace = workspaces.save(new Workspace("Acme"));
        workspace.addMember(user);
        em.flush();

        workspace.removeMember(user);
        em.flush();
        em.clear();

        User reloaded = users.findById(user.getId()).orElseThrow();
        assertFalse(reloaded.isOnSearch());
        assertTrue(reloaded.getMemberships().isEmpty());
    }

    @Test
    void suspendingMemberKeepsRowAndRecomputesOnSearchToFalse() {
        User user = users.save(new User("Alice"));
        Workspace workspace = workspaces.save(new Workspace("Acme"));
        workspace.addMember(user);
        em.flush();

        workspace.suspendMember(user);
        em.flush();
        em.clear();

        User reloaded = users.findById(user.getId()).orElseThrow();
        assertFalse(reloaded.isOnSearch());
        assertEquals(1, reloaded.getMemberships().size());
        assertEquals(MembershipStatus.SUSPENDED,
                reloaded.getMemberships().iterator().next().getStatus());
    }

    @Test
    void reAddingSuspendedMemberReactivatesAndPutsBackOnSearch() {
        User user = users.save(new User("Alice"));
        Workspace workspace = workspaces.save(new Workspace("Acme"));
        workspace.addMember(user);
        workspace.suspendMember(user);
        em.flush();
        assertFalse(users.findById(user.getId()).orElseThrow().isOnSearch());

        Workspace reloadedWorkspace = workspaces.findById(workspace.getId()).orElseThrow();
        User reloadedUser = users.findById(user.getId()).orElseThrow();
        reloadedWorkspace.addMember(reloadedUser);
        em.flush();
        em.clear();

        User after = users.findById(user.getId()).orElseThrow();
        assertTrue(after.isOnSearch());
        assertEquals(1, after.getMemberships().size());
        assertTrue(after.getMemberships().iterator().next().isActive());
    }

    @Test
    void userWithOnlySuspendedMembershipsIsNotOnSearch() {
        User user = users.save(new User("Alice"));
        Workspace first = workspaces.save(new Workspace("Acme"));
        Workspace second = workspaces.save(new Workspace("Globex"));
        first.addMember(user);
        second.addMember(user);
        first.suspendMember(user);
        second.suspendMember(user);
        em.flush();
        em.clear();

        assertFalse(users.findById(user.getId()).orElseThrow().isOnSearch());
    }

    @Test
    void userStaysOnSearchWhileAtLeastOneActiveMembershipRemains() {
        User user = users.save(new User("Alice"));
        Workspace first = workspaces.save(new Workspace("Acme"));
        Workspace second = workspaces.save(new Workspace("Globex"));
        first.addMember(user);
        second.addMember(user);
        em.flush();

        first.suspendMember(user);
        em.flush();
        em.clear();

        assertTrue(users.findById(user.getId()).orElseThrow().isOnSearch());
    }

    @Test
    void blankFullNameRecomputesOnSearchToFalse() {
        User user = users.save(new User("Alice"));
        Workspace workspace = workspaces.save(new Workspace("Acme"));
        workspace.addMember(user);
        em.flush();

        user.setFullName("   ");
        em.flush();
        em.clear();

        assertFalse(users.findById(user.getId()).orElseThrow().isOnSearch());
    }

    @Test
    void nullFullNameRecomputesOnSearchToFalse() {
        User user = users.save(new User("Alice"));
        Workspace workspace = workspaces.save(new Workspace("Acme"));
        workspace.addMember(user);
        em.flush();

        user.setFullName(null);
        em.flush();
        em.clear();

        assertFalse(users.findById(user.getId()).orElseThrow().isOnSearch());
    }

    @Test
    void restoringFullNameRecomputesOnSearchToTrue() {
        User user = users.save(new User("Alice"));
        Workspace workspace = workspaces.save(new Workspace("Acme"));
        workspace.addMember(user);
        user.setFullName("");
        em.flush();
        assertFalse(users.findById(user.getId()).orElseThrow().isOnSearch());

        user.setFullName("Alice");
        em.flush();
        em.clear();

        assertTrue(users.findById(user.getId()).orElseThrow().isOnSearch());
    }

    @Test
    void addingSameMemberTwiceIsIdempotent() {
        User user = users.save(new User("Alice"));
        Workspace workspace = workspaces.save(new Workspace("Acme"));

        workspace.addMember(user);
        workspace.addMember(user);
        em.flush();
        em.clear();

        User reloaded = users.findById(user.getId()).orElseThrow();
        assertTrue(reloaded.isOnSearch());
        assertEquals(1, reloaded.getMemberships().size());
    }
}
