package org.miage.m2.intervenantservice.boundary;

import org.miage.m2.intervenantservice.entity.Intervenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    @GetMapping(value = "/{intervenantId}")
    public ResponseEntity<?> getIntervenant(@PathVariable("intervenantId") String id) {
        return Optional.ofNullable(ir.findOne(id))
                .map(u -> new ResponseEntity<>(intervenantToResource(u, true), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    //POST
    @PostMapping
    public ResponseEntity<?> saveIntervenant(@RequestBody Intervenant intervenant) {
        intervenant.setId(UUID.randomUUID().toString());
        Intervenant saved = ir.save(intervenant);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(linkTo(IntervenantRepresentation.class).slash(saved.getId()).toUri());
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }

    //PUT
    @PutMapping(value = "/{intervenantId}")
    public ResponseEntity<?> updateIntervenant(@RequestBody Intervenant intervenant, @PathVariable("intervenantId") String intervenantId) {
        Optional<Intervenant> body = Optional.ofNullable(intervenant);
        if (!body.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!ir.exists(intervenantId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        intervenant.setId(intervenantId);
        Intervenant result = ir.save(intervenant);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //DELETE
    @DeleteMapping(value = "/{intervenantId}")
    public ResponseEntity<?> deleteIntervenant(@PathVariable("intervenantId") String intervenantId) {
        ir.delete(intervenantId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
