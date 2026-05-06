package workspaceweb.workspace;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkspaceService {

    private final WorkspaceRepository workspaces;

    public WorkspaceService(WorkspaceRepository workspaces) {
        this.workspaces = workspaces;
    }

    public Workspace create(String name) {
        return workspaces.save(new Workspace(name));
    }

    @Transactional(readOnly = true)
    public Workspace get(Long id) {
        return workspaces.findById(id).orElseThrow(() -> new WorkspaceNotFoundException(id));
    }
}
