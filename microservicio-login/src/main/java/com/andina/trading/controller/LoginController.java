package com.andina.trading.controller;

import com.andina.trading.model.Usuario;
import com.andina.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    // Método para login (ya existente)
    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario user) {
        return userService.authenticate(user.getUsername(), user.getPassword());
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario user) {
        try {
            // Crear usuario
        	Usuario createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Si el rol no es válido
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Cualquier otro error
            return new ResponseEntity<>("Error al registrar usuario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
