package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.ProductorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductorRepository extends JpaRepository<ProductorEntity, Long> {
}
