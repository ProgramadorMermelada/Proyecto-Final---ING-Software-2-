package com.andina.trading.repository;

import com.andina.trading.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	Usuario findByUsernameAndPassword(String username, String password);
}
