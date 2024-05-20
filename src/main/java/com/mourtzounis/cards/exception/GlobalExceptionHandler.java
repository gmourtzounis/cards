package com.mourtzounis.cards.exception;

import com.mourtzounis.cards.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_MSG = "Failed with error: {}";


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest webRequest) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        var firstError = errors.stream().findFirst().orElse("Request contains invalid argument");

        log.error(ERROR_MSG, firstError);

        return new ResponseEntity<>(new ApiResponse(firstError), BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(ERROR_MSG, ex.getMessage());


        return new ResponseEntity<>(new ApiResponse("The requested resource was not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> constraintViolationException(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));

        var firstError = errors.stream().findFirst().orElse("Constraint violation occurred");
        log.error(ERROR_MSG, firstError);


        return new ResponseEntity<>(new ApiResponse(firstError), BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ERROR_MSG, ex.getMessage());

        return ResponseEntity.unprocessableEntity().body(new ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler({UsernameNotFoundException.class, CardNotFoundException.class, RoleNotFoundException.class})
    public ResponseEntity<ApiResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.error(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ExpiredCredentialsException.class, AccessDeniedException.class})
    public ResponseEntity<ApiResponse> handleExpiredCredentialsException(Exception ex) {
        log.error(ERROR_MSG, ex.getMessage());

        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error(ERROR_MSG, ex.getMessage());

        return ResponseEntity.badRequest().body(new ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponse> handleAllExceptions(Exception ex) {
        var apiResponse = new ApiResponse(ex.getMessage());
        log.error(ERROR_MSG, ex.getMessage());

        return ResponseEntity.internalServerError().body(apiResponse);
    }
}
