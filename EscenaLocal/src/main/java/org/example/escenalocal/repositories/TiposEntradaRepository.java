package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.ArtistaEntity;
import org.example.escenalocal.entities.TiposEntradaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface TiposEntradaRepository extends JpaRepository<TiposEntradaEntity,Long> {

}
