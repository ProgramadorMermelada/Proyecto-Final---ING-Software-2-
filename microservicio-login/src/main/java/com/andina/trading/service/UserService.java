package com.andina.trading.service;

import com.andina.trading.model.Usuario;
import com.andina.trading.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UsuarioRepository userRepository;

    // Método de autenticación
    public Usuario authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);  // Usa el repositorio para buscar un usuario por su nombre y contraseña
    }

    // Metodo para crear un usuario
    public Usuario createUser(Usuario user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }

        return userRepository.save(user); // Guardar el nuevo usuario
    }
}

