package computed.workspace;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import computed.user.User;
import computed.user.UserMembershipAccess;

@Entity
@Table(name = "T_WORKSPACE_MEMBER",
        uniqueConstraints = @UniqueConstraint(columnNames = {"workspace_id", "user_id"}))
public class WorkspaceMember {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status;

    protected WorkspaceMember() {
    }

    WorkspaceMember(Workspace workspace, User user) {
        this.workspace = workspace;
        this.user = user;
        this.status = MembershipStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public User getUser() {
        return user;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    void activate() {
        this.status = MembershipStatus.ACTIVE;
        UserMembershipAccess.recompute(user);
    }

    void suspend() {
        this.status = MembershipStatus.SUSPENDED;
        UserMembershipAccess.recompute(user);
    }

    public boolean isActive() {
        return status == MembershipStatus.ACTIVE;
    }
}
