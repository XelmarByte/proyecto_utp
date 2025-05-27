package com.lp.gestionusuariosroles.user.controller;

import com.lp.gestionusuariosroles.error.ErrorResponse;
import com.lp.gestionusuariosroles.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','SUPERVISOR')")
    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','SUPERVISOR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(service.getUserById(id));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Not Found", e.getMessage()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-data")
    public ResponseEntity<?> getUserInSessionData(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.getUserInSessionData(request));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Not Found", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','SUPERVISOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDto userDto){
        try{
            service.updateUser(id, userDto);
            return ResponseEntity.ok().build();
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Not Found", e.getMessage()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','SUPERVISOR')")
    @PatchMapping("/estado/{id}/{nuevoEstado}")
    public ResponseEntity<?> changeEstado(@PathVariable Long id, @PathVariable Boolean nuevoEstado){
        try{
            service.changeEstado(id, nuevoEstado);
            return ResponseEntity.ok().build();
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Not Found", e.getMessage()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody NewPasswordRequest passwordRequest){
        try {
            return ResponseEntity.ok(service.changePassword(request, passwordRequest));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Not Found", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','SUPERVISOR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response){
        try{
            service.deleteUser(id, request, response);
            return ResponseEntity.ok().build();
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Not Found", e.getMessage()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", e.getMessage()));
        }
    }

}
