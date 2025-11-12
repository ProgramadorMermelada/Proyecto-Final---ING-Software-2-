package com.andina.trading.controller;

import com.andina.trading.model.Usuario;
import com.andina.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario user) {
        try {
            System.out.println("========================================");
            System.out.println("🔐 Intento de login para usuario: " + user.getUsername());
            
            Usuario authenticatedUser = userService.authenticate(user.getUsername(), user.getPassword());
            
            if (authenticatedUser == null) {
                System.out.println("❌ Autenticación fallida para: " + user.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
            }

            System.out.println("✅ Usuario autenticado: " + authenticatedUser.getUsername());
            System.out.println("👤 Rol del usuario: " + authenticatedUser.getRole());
            System.out.println("🆔 ID del usuario: " + authenticatedUser.getId());

            // Crear respuesta con información básica
            Map<String, Object> response = new HashMap<>();
            response.put("id", authenticatedUser.getId());
            response.put("username", authenticatedUser.getUsername());
            response.put("role", authenticatedUser.getRole());

            // Obtener el ID del inversionista o comisionista desde sus microservicios
            if ("inversionista".equals(authenticatedUser.getRole())) {
                try {
                    String url = "http://localhost:8081/inversionistas";
                    System.out.println("🔍 Consultando inversionistas en: " + url);
                    
                    List<Map<String, Object>> inversionistas = restTemplate.getForObject(url, List.class);
                    System.out.println("📥 Total de inversionistas obtenidos: " + (inversionistas != null ? inversionistas.size() : 0));
                    
                    if (inversionistas != null && !inversionistas.isEmpty()) {
                        System.out.println("📋 Lista completa de inversionistas:");
                        for (Map<String, Object> inv : inversionistas) {
                            System.out.println("  - ID: " + inv.get("id") + ", Nombre: '" + inv.get("nombre") + "'");
                        }
                    }
                    
                    // Buscar el inversionista que coincida con el username
                    Integer inversionistaId = null;
                    if (inversionistas != null) {
                        for (Map<String, Object> inv : inversionistas) {
                            String nombreInv = (String) inv.get("nombre");
                            System.out.println("🔎 Comparando:");
                            System.out.println("   Username (login): '" + authenticatedUser.getUsername() + "'");
                            System.out.println("   Nombre (BD):      '" + nombreInv + "'");
                            System.out.println("   ¿Son iguales? " + authenticatedUser.getUsername().equals(nombreInv));
                            
                            if (authenticatedUser.getUsername().equals(nombreInv)) {
                                inversionistaId = (Integer) inv.get("id");
                                System.out.println("✅ ¡MATCH ENCONTRADO! inversionistaId: " + inversionistaId);
                                break;
                            }
                        }
                    }
                    
                    if (inversionistaId != null) {
                        response.put("inversionistaId", inversionistaId);
                        System.out.println("✅ Devolviendo inversionistaId: " + inversionistaId);
                    } else {
                        System.out.println("⚠️ No se encontró inversionista con nombre: " + authenticatedUser.getUsername());
                        System.out.println("🔧 Creando inversionista automáticamente...");
                        
                        // Si no existe, crear el inversionista automáticamente
                        Map<String, Object> inversionistaData = new HashMap<>();
                        inversionistaData.put("nombre", authenticatedUser.getUsername());
                        inversionistaData.put("saldoDisponible", 100000.0);
                        
                        System.out.println("📤 Datos a enviar: " + inversionistaData);
                        
                        Map<String, Object> nuevoInversionista = restTemplate.postForObject(
                            "http://localhost:8081/inversionistas", 
                            inversionistaData, 
                            Map.class
                        );
                        
                        Integer nuevoId = (Integer) nuevoInversionista.get("id");
                        System.out.println("✅ Inversionista creado con ID: " + nuevoId);
                        response.put("inversionistaId", nuevoId);
                    }
                    
                } catch (Exception e) {
                    System.err.println("❌ ERROR al obtener/crear inversionista:");
                    System.err.println("   Tipo: " + e.getClass().getName());
                    System.err.println("   Mensaje: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Si falla, usar el ID del usuario como fallback
                    System.out.println("⚠️ Usando ID del usuario como fallback: " + authenticatedUser.getId());
                    response.put("inversionistaId", authenticatedUser.getId());
                }
                
            } else if ("comisionista".equals(authenticatedUser.getRole())) {
                try {
                    String url = "http://localhost:8082/comisionistas";
                    System.out.println("🔍 Consultando comisionistas en: " + url);
                    
                    List<Map<String, Object>> comisionistas = restTemplate.getForObject(url, List.class);
                    System.out.println("📥 Total de comisionistas obtenidos: " + (comisionistas != null ? comisionistas.size() : 0));
                    
                    // Buscar el comisionista que coincida con el username
                    Integer comicionistaId = null;
                    if (comisionistas != null) {
                        for (Map<String, Object> com : comisionistas) {
                            String nombreCom = (String) com.get("nombre");
                            System.out.println("🔎 Comparando: '" + authenticatedUser.getUsername() + "' con '" + nombreCom + "'");
                            
                            if (authenticatedUser.getUsername().equals(nombreCom)) {
                                comicionistaId = (Integer) com.get("id");
                                System.out.println("✅ Match encontrado! comicionistaId: " + comicionistaId);
                                break;
                            }
                        }
                    }
                    
                    if (comicionistaId != null) {
                        response.put("comicionistaId", comicionistaId);
                        System.out.println("✅ Devolviendo comicionistaId: " + comicionistaId);
                    } else {
                        System.out.println("⚠️ No se encontró comisionista, creando uno nuevo...");
                        
                        // Si no existe, crear el comisionista automáticamente
                        Map<String, Object> comicionistaData = new HashMap<>();
                        comicionistaData.put("nombre", authenticatedUser.getUsername());
                        comicionistaData.put("saldoComisiones", 0.0);
                        
                        Map<String, Object> nuevoComisionista = restTemplate.postForObject(
                            "http://localhost:8082/comisionistas", 
                            comicionistaData, 
                            Map.class
                        );
                        
                        Integer nuevoId = (Integer) nuevoComisionista.get("id");
                        System.out.println("✅ Comisionista creado con ID: " + nuevoId);
                        response.put("comicionistaId", nuevoId);
                    }
                    
                } catch (Exception e) {
                    System.err.println("❌ ERROR al obtener/crear comisionista:");
                    System.err.println("   Mensaje: " + e.getMessage());
                    e.printStackTrace();
                    response.put("comicionistaId", authenticatedUser.getId());
                }
            }

            System.out.println("📤 Respuesta final del login:");
            System.out.println("   " + response);
            System.out.println("========================================");

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTICO en login:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error en el servidor: " + e.getMessage());
        }
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> userData) {
        try {
            System.out.println("========================================");
            System.out.println("📝 DATOS COMPLETOS RECIBIDOS DEL FRONTEND:");
            System.out.println(userData);
            System.out.println("========================================");
            
            String username = (String) userData.get("username");
            String password = (String) userData.get("password");
            String role = (String) userData.get("role");
            String email = (String) userData.get("email");
            String telefono = (String) userData.get("telefono");

            // 1. Crear el usuario en el microservicio de Login
            Usuario user = new Usuario();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            
            Usuario createdUser = userService.createUser(user);
            System.out.println("✅ Usuario creado en BD de login con ID: " + createdUser.getId());

            // 2. Crear respuesta base
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("username", createdUser.getUsername());
            response.put("role", createdUser.getRole());

            // 3. Dependiendo del rol, crear en el microservicio correspondiente
            if ("inversionista".equals(role)) {
                String urlInversionista = "http://localhost:8081/inversionistas";
                System.out.println("📤 Creando inversionista en: " + urlInversionista);
                
                Map<String, Object> inversionistaData = new HashMap<>();
                inversionistaData.put("nombre", username);
                inversionistaData.put("email", email);
                inversionistaData.put("telefono", telefono);
                
                Object saldoInicialObj = userData.get("saldoInicial");
                Double saldoInicial = 100000.0;
                if (saldoInicialObj != null) {
                    if (saldoInicialObj instanceof Number) {
                        saldoInicial = ((Number) saldoInicialObj).doubleValue();
                    } else if (saldoInicialObj instanceof String) {
                        saldoInicial = Double.parseDouble((String) saldoInicialObj);
                    }
                }
                inversionistaData.put("saldoDisponible", saldoInicial);

                Map<String, Object> inversionistaCreado = restTemplate.postForObject(
                    urlInversionista,
                    inversionistaData,
                    Map.class
                );

                // ⭐ AGREGAR EL ID DEL INVERSIONISTA A LA RESPUESTA
                response.put("inversionistaId", inversionistaCreado.get("id"));
                
                System.out.println("✅ Inversionista creado con ID: " + inversionistaCreado.get("id"));

            } else if ("comisionista".equals(role)) {
                String urlComisionista = "http://localhost:8082/comisionistas";
                System.out.println("📤 Creando comisionista en: " + urlComisionista);
                
                String ciudad = (String) userData.get("ciudad");
                String pais = (String) userData.get("pais");
                String numeroLicencia = (String) userData.get("numeroLicencia");

                Map<String, Object> comisionistaData = new HashMap<>();
                comisionistaData.put("nombre", username);
                comisionistaData.put("email", email);
                comisionistaData.put("telefono", telefono);
                comisionistaData.put("ciudad", ciudad);
                comisionistaData.put("pais", pais);
                comisionistaData.put("numeroLicencia", numeroLicencia);
                comisionistaData.put("saldoComisiones", 0.0);
                
                System.out.println("📦 Datos del comisionista a enviar:");
                System.out.println("   " + comisionistaData);

                Map<String, Object> comisionistaCreado = restTemplate.postForObject(
                    urlComisionista,
                    comisionistaData,
                    Map.class
                );

                // ⭐ AGREGAR EL ID DEL COMISIONISTA A LA RESPUESTA
                response.put("comicionistaId", comisionistaCreado.get("id"));
                
                System.out.println("✅ Comisionista creado con ID: " + comisionistaCreado.get("id"));
                System.out.println("📤 Devolviendo comicionistaId en respuesta: " + comisionistaCreado.get("id"));

            } else {
                System.out.println("❌ Rol inválido: " + role);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Rol no válido"));
            }

            System.out.println("✅ Registro completado exitosamente");
            System.out.println("📤 Respuesta completa: " + response);
            System.out.println("========================================");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("❌ ERROR en registro:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al registrar usuario: " + e.getMessage()));
        }
    }
}