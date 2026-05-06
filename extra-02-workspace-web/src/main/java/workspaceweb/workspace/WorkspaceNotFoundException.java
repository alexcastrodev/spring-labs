package workspaceweb.workspace;

public class WorkspaceNotFoundException extends RuntimeException {

    public WorkspaceNotFoundException(Long id) {
        super("Workspace " + id + " not found");
    }
}
