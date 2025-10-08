package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VarreduraRepository extends JpaRepository<Varredura, Long> {
    // Métodos customizados opcionais, buscar por cidade
    List<Varredura> findByCidade(String cidade);
    List<Varredura> findByCidadeAndStatus(String cidade, String status);
}