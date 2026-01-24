package dk.tommer.workday.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoSuchElementException ex, Model model) {
        logger.error("Resource not found: {}", ex.getMessage());
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "Ikke fundet");
        model.addAttribute("errorMessage", "Siden eller ressourcen du leder efter findes ikke.");
        return "error/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        logger.error("Access denied: {}", ex.getMessage());
        model.addAttribute("errorCode", "403");
        model.addAttribute("errorTitle", "Ingen adgang");
        model.addAttribute("errorMessage", "Du har ikke rettigheder til at se denne side.");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralError(Exception ex, Model model) {
        logger.error("An unexpected error occurred", ex);
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Der opstod en fejl");
        model.addAttribute("errorMessage", "Der skete en uventet fejl i systemet. Prøv igen senere eller kontakt administrator.");
        return "error/error";
    }
}
