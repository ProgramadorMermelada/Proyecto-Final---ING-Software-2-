package com.andina.trading.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Cliente para comunicarse con el microservicio de Inversionistas
 */
@Component
public class InversionistaClient {

    private final RestTemplate restTemplate;
    
    @Value("${inversionista.service.url:http://localhost:8081}")
    private String inversionistaServiceUrl;

    public InversionistaClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Obtener información de un inversionista
     */
    public Map<String, Object> obtenerInversionista(Long inversionistaId) {
        try {
            String url = inversionistaServiceUrl + "/inversionistas/" + inversionistaId;
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("✅ Inversionista obtenido: " + inversionistaId);
                return response.getBody();
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo inversionista: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validar si el inversionista tiene saldo suficiente
     */
    public boolean validarSaldo(Long inversionistaId, Double monto) {
        try {
            String url = inversionistaServiceUrl + "/inversionistas/" + inversionistaId + 
                        "/validar-saldo?monto=" + monto;
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Boolean tieneSaldo = (Boolean) response.getBody().get("tieneSaldoSuficiente");
                System.out.println("💰 Validación de saldo: " + (tieneSaldo ? "✅ Suficiente" : "❌ Insuficiente"));
                return tieneSaldo != null && tieneSaldo;
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error validando saldo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Descontar saldo del inversionista
     */
    public boolean descontarSaldo(Long inversionistaId, Double monto) {
        try {
            String url = inversionistaServiceUrl + "/inversionistas/" + inversionistaId + "/descontar-saldo";
            
            Map<String, Double> body = new HashMap<>();
            body.put("monto", monto);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Double>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            boolean exito = response.getStatusCode() == HttpStatus.OK;
            System.out.println(exito ? "✅ Saldo descontado" : "❌ Error descontando saldo");
            return exito;
            
        } catch (Exception e) {
            System.err.println("❌ Error descontando saldo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Agregar saldo al inversionista
     */
    public boolean agregarSaldo(Long inversionistaId, Double monto) {
        try {
            String url = inversionistaServiceUrl + "/inversionistas/" + inversionistaId + "/agregar-saldo";
            
            Map<String, Double> body = new HashMap<>();
            body.put("monto", monto);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Double>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            boolean exito = response.getStatusCode() == HttpStatus.OK;
            System.out.println(exito ? "✅ Saldo agregado" : "❌ Error agregando saldo");
            return exito;
            
        } catch (Exception e) {
            System.err.println("❌ Error agregando saldo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Vincular inversionista con comisionista
     */
    public boolean vincularComisionista(Long inversionistaId, Long comisionistaId, Long contratoId) {
        try {
            String url = inversionistaServiceUrl + "/inversionistas/" + inversionistaId + "/vincular-comisionista";
            
            Map<String, Long> body = new HashMap<>();
            body.put("comisionistaId", comisionistaId);
            body.put("contratoId", contratoId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Long>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            boolean exito = response.getStatusCode() == HttpStatus.OK;
            System.out.println(exito ? "✅ Inversionista vinculado" : "❌ Error vinculando");
            return exito;
            
        } catch (Exception e) {
            System.err.println("❌ Error vinculando inversionista: " + e.getMessage());
            return false;
        }
    }
}