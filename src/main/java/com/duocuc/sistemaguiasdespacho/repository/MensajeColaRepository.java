package com.duocuc.sistemaguiasdespacho.repository;

import com.duocuc.sistemaguiasdespacho.entity.MensajeCola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeColaRepository extends JpaRepository<MensajeCola, Long> {
}
