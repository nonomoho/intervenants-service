package org.miage.m2.intervenantservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor //obligatoire pour JPA => Sinon plante avec un msg non explicite !
@Data
public class Intervenant implements Serializable{
    @Id
    private String id;
    private String nom;
    private String prenom;
    private String commune;
    private String codepostal;
}