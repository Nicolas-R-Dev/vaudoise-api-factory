package ch.vaudoise.apifactory.common.error;

import ch.vaudoise.apifactory.common.exception.BadRequestException;
import ch.vaudoise.apifactory.common.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.*;
/**
 * Centralized exception handling for REST controllers.
 * <p>
 * Produces consistent JSON error responses with status, message, timestamp,
 * and detailed field errors when applicable.
 * Handles:
 * <ul>
 *   <li>{@code MethodArgumentNotValidException} / {@code BindException} for @Valid bodies and lists</li>
 *   <li>{@code ConstraintViolationException} for @RequestParam/@PathVariable validations</li>
 *   <li>{@code HttpMessageNotReadableException} for unknown/invalid JSON</li>
 *   <li>{@code DataIntegrityViolationException} for database constraints (e.g., unique email)</li>
 *   <li>Custom {@code BadRequestException} and {@code NotFoundException}</li>
 * </ul>
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> baseBody(int status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status);
        body.put("error", error);
        if (message != null) body.put("message", message);
        return body;
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> jsonUnreadable(HttpMessageNotReadableException ex){
        var body = new LinkedHashMap<String,Object>();
        body.put("timestamp", java.time.OffsetDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "BAD_REQUEST");
        // Message court et utile
        body.put("message", "Unknown or invalid JSON properties in request body");
        return ResponseEntity.badRequest().body(body);
    }


    // 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(NotFoundException ex) {
        var body = baseBody(404, "NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(404).body(body);
    }

    // 400 – tes BadRequest "métiers"
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> badRequest(BadRequestException ex) {
        var body = baseBody(400, "BAD_REQUEST", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    // 400 – IllegalArgumentException (fallback rapide)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> illegalArg(IllegalArgumentException ex) {
        var body = baseBody(400, "BAD_REQUEST", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    // 400 – erreurs Bean Validation sur @RequestBody (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        var body = baseBody(400, "VALIDATION_ERROR", "Request validation failed");
        List<Map<String, Object>> errors = new ArrayList<>();

        // Field-level errors (ex: clients[1].birthdate)
        ex.getBindingResult().getFieldErrors().forEach(fe -> {
            Map<String, Object> e = new LinkedHashMap<>();

            String fieldPath = fe.getField();          // ex: [1].birthdate ou clients[1].birthdate
            Integer index = null;

            // Cherche un indice [n] dans le chemin
            var m = java.util.regex.Pattern.compile("\\[(\\d+)]").matcher(fieldPath);
            if (m.find()) {
                index = Integer.parseInt(m.group(1));
                // nettoie le chemin pour ne garder que le nom de champ (ex: "birthdate")
                fieldPath = fieldPath.replaceAll("^.*\\[\\d+\\]\\.?","")
                        .replaceAll("^.*\\.","");
            }

            e.put("index", index);
            e.put("field", fieldPath);
            e.put("message", fe.getDefaultMessage());

            Object rejected = fe.getRejectedValue();
            if (rejected != null) e.put("rejectedValue", rejected);

            errors.add(e);
        });

        // Class-level errors (@ValidClientPayload) -> essaye aussi d'extraire un index s'il apparaît
        ex.getBindingResult().getGlobalErrors().forEach(ge -> {
            Map<String, Object> e = new LinkedHashMap<>();
            String obj = ge.getObjectName();           // ex: clientCreateDto[2]
            Integer index = null;
            var m = java.util.regex.Pattern.compile("\\[(\\d+)]").matcher(obj);
            if (m.find()) index = Integer.parseInt(m.group(1));
            e.put("index", index);
            e.put("object", obj.replaceAll("\\[\\d+]", ""));
            e.put("message", ge.getDefaultMessage());
            errors.add(e);
        });

        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }


    // 400 – erreurs Bean Validation sur @RequestParam / @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> constraintViolation(ConstraintViolationException ex) {
        var body = baseBody(400, "VALIDATION_ERROR", "Request validation failed");
        List<Map<String, Object>> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(v -> {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("parameter", v.getPropertyPath().toString());
            e.put("message", v.getMessage());
            Object invalid = v.getInvalidValue();
            if (invalid != null) e.put("rejectedValue", invalid);
            errors.add(e);
        });
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }
}
