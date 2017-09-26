package org.miage.m2.intervenantservice.boundary;

import org.miage.m2.intervenantservice.entity.Intervenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

public interface IntervenantRessource extends JpaRepository<Intervenant, String>{
    //GET, POST, PUT, DELETE sont traités auto
}
