package computed.workspace;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import computed.user.User;
import computed.user.UserMembershipAccess;

@Entity
@Table(name = "T_WORKSPACE")
public class Workspace {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkspaceMember> memberships = new LinkedHashSet<>();

    protected Workspace() {
    }

    public Workspace(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<WorkspaceMember> getMemberships() {
        return Collections.unmodifiableSet(memberships);
    }

    public void addMember(User user) {
        Optional<WorkspaceMember> existing = findMembership(user);
        if (existing.isPresent()) {
            existing.get().activate();
            return;
        }
        WorkspaceMember membership = new WorkspaceMember(this, user);
        memberships.add(membership);
        UserMembershipAccess.add(user, membership);
    }

    public void suspendMember(User user) {
        findMembership(user).ifPresent(WorkspaceMember::suspend);
    }

    public void removeMember(User user) {
        findMembership(user).ifPresent(m -> {
            memberships.remove(m);
            UserMembershipAccess.remove(user, m);
        });
    }

    private Optional<WorkspaceMember> findMembership(User user) {
        return memberships.stream().filter(m -> m.getUser().equals(user)).findFirst();
    }
}
