package org.example;

import jakarta.persistence.*; // CORREÇÃO: Usando jakarta.persistence para Spring Boot 3+
import java.time.LocalDateTime;

@Entity
@Table(name = "varredura")  // Nome da tabela no BD
public class Varredura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private String status;  // Ex: "Sucesso", "Erro"

    @Column(columnDefinition = "TEXT")  // Para JSON grande
    private String dadosJson;  // Armazena o resultado como JSON

    // Construtores
    public Varredura() {}

    public Varredura(String cidade, String status, String dadosJson) {
        this.cidade = cidade;
        this.status = status;
        this.dadosJson = dadosJson;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDadosJson() { return dadosJson; }
    public void setDadosJson(String dadosJson) { this.dadosJson = dadosJson; }
}