package org.miage.m2.intervenantservice.boundary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class Testintervenant {
    @GetMapping("/listeInter")
    public String liste(){
        return "Arnaud, Thomas, Antoine, Je suis";
    }
}
