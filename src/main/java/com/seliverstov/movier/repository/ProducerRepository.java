package com.seliverstov.movier.repository;

import com.seliverstov.movier.domain.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProducerRepository extends JpaRepository<Producer,Long> {

    Producer findById(int id);

    Producer findByName(String name);
}
