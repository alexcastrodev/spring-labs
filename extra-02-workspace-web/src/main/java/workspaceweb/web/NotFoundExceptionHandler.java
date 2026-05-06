package workspaceweb.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import workspaceweb.user.UserNotFoundException;
import workspaceweb.workspace.WorkspaceNotFoundException;

@RestControllerAdvice
public class NotFoundExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, WorkspaceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle(RuntimeException ex) {
        return ex.getMessage();
    }
}
