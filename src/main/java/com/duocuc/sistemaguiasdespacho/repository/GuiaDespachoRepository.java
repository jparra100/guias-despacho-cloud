package com.duocuc.sistemaguiasdespacho.repository;

import com.duocuc.sistemaguiasdespacho.entity.GuiaDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

    Optional<GuiaDespacho> findByNumeroGuia(String numeroGuia);

    // Consulta guias por transportista y fecha (endpoint 6 del requerimiento)
    List<GuiaDespacho> findByTransportistaContainingIgnoreCaseAndFechaDespacho(
            String transportista, LocalDate fechaDespacho);

    List<GuiaDespacho> findByTransportistaContainingIgnoreCase(String transportista);

    List<GuiaDespacho> findByFechaDespacho(LocalDate fechaDespacho);
}
