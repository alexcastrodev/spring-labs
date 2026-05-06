package workspaceweb.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import workspaceweb.web.Dtos.CreateWorkspaceRequest;
import workspaceweb.web.Dtos.WorkspaceResponse;
import workspaceweb.workspace.Workspace;
import workspaceweb.workspace.WorkspaceService;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaces;

    public WorkspaceController(WorkspaceService workspaces) {
        this.workspaces = workspaces;
    }

    @PostMapping
    public ResponseEntity<WorkspaceResponse> create(@RequestBody CreateWorkspaceRequest request) {
        Workspace created = workspaces.create(request.name());
        return ResponseEntity
                .created(URI.create("/workspaces/" + created.getId()))
                .body(WorkspaceResponse.from(created));
    }
}
