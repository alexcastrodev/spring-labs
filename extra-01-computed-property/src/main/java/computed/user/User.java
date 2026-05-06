package computed.user;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import computed.workspace.WorkspaceMember;

@Entity
@Table(name = "T_USER")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String fullName;

    private boolean onSearch;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkspaceMember> memberships = new LinkedHashSet<>();

    protected User() {
    }

    public User(String fullName) {
        this.fullName = fullName;
        recomputeOnSearch();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        recomputeOnSearch();
    }

    public boolean isOnSearch() {
        return onSearch;
    }

    public Set<WorkspaceMember> getMemberships() {
        return Collections.unmodifiableSet(memberships);
    }

    void addMembership(WorkspaceMember membership) {
        memberships.add(membership);
        recomputeOnSearch();
    }

    void removeMembership(WorkspaceMember membership) {
        memberships.remove(membership);
        recomputeOnSearch();
    }

    @PrePersist
    @PreUpdate
    void recomputeOnSearch() {
        boolean hasName = fullName != null && !fullName.trim().isEmpty();
        boolean hasActiveMembership = memberships.stream().anyMatch(WorkspaceMember::isActive);
        this.onSearch = hasName && hasActiveMembership;
    }
}
