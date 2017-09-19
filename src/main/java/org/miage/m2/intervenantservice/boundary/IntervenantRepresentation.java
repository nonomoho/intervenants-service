package org.miage.m2.intervenantservice.boundary;

import org.miage.m2.intervenantservice.entity.Intervenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/intervenants", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Intervenant.class)
public class IntervenantRepresentation {
    private final IntervenantRessource ir;

    @Autowired
    public IntervenantRepresentation(IntervenantRessource ir) {
        this.ir = ir;
    }

    //GET all
    @GetMapping
    public ResponseEntity<?> getAllIntervenants() {
        Iterable<Intervenant> intervenantsCollection = ir.findAll();
        return new ResponseEntity<Object>(intervenantToRessource(intervenantsCollection), HttpStatus.OK);
    }

    //GET one
    @GetMapping(value = "{intervenantId}")
    public ResponseEntity<?> getIntervenant(@PathVariable("intervenantId") String id){
        return Optional.ofNullable(ir.findOne(id))
                .map(u -> new ResponseEntity<>(intervenantToResource(u, true), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    private Resources<Resource<Intervenant>> intervenantToRessource(Iterable<Intervenant> intervenants) {
        Link selfLink = linkTo(methodOn(IntervenantRepresentation.class).getAllIntervenants()).withSelfRel();
        List<Resource<Intervenant>> intervenantRessources = new ArrayList<>();
        intervenants.forEach(intervenant ->
                intervenantRessources.add(intervenantToResource(intervenant, false)));
        return new Resources<>(intervenantRessources, selfLink);
    }

    private Resource<Intervenant> intervenantToResource(Intervenant intervenant, Boolean collection) {
        Link self = linkTo(IntervenantRepresentation.class)
                .slash(intervenant.getId())
                .withSelfRel();
        if (collection) {
            Link collectionLink = linkTo(methodOn(IntervenantRepresentation.class).getAllIntervenants())
                    .withRel("collection");

            return new Resource<>(intervenant, self, collectionLink);
        } else {
            return new Resource<>(intervenant, self);
        }
    }
}
