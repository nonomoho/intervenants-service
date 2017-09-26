package org.miage.m2.intervenantservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.intervenantservice.boundary.IntervenantRessource;
import org.miage.m2.intervenantservice.entity.Intervenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntervenantServiceApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    IntervenantRessource ir;

    @Before
    public void setupContext() {
        ir.deleteAll();
    }

    @Test
    public void notFound() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/intervenants/150", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getOne() throws Exception{
        Intervenant i1 = new Intervenant("Freismuth", "Arnaud", "Waltembourg", "57370");
        i1.setId(UUID.randomUUID().toString());
        ir.save(i1);

        ResponseEntity<String> response = restTemplate.getForEntity("/intervenants/" + i1.getId(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Arnaud");
    }

    @Test
    public void getAll() throws Exception {
        Intervenant i1 = new Intervenant("Freismuth", "Arnaud", "Waltembourg", "57370");
        i1.setId(UUID.randomUUID().toString());
        ir.save(i1);

        Intervenant i2 = new Intervenant("Schersach", "Thomas", "Nancy", "54000");
        i2.setId(UUID.randomUUID().toString());
        ir.save(i2);

        ResponseEntity<String> response = restTemplate.getForEntity("/intervenants", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Arnaud");
        assertThat(response.getBody()).contains("Schersach");

        List<String> list = JsonPath.read(response.getBody(), "$..intervenants..nom");
        assertThat(list).hasSize(2);
    }

    @Test
    public void post() throws Exception {
        Intervenant i1 = new Intervenant("Freismuth", "Arnaud", "Waltembourg", "57370");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(this.toJsonString(i1), headers);

        ResponseEntity<?> response = restTemplate.postForEntity("/intervenants", entity, ResponseEntity.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = response.getHeaders().getLocation();
        response = restTemplate.getForEntity(location, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void put() throws Exception {
        Intervenant i1 = new Intervenant("Freismuth", "Arnaud", "Waltembourg", "57370");
        i1.setId(UUID.randomUUID().toString());
        ir.save(i1);

        Intervenant i1Modif = new Intervenant("Freismuth", "Anne", "Waltembourg", "57370");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(this.toJsonString(i1Modif), headers);

        restTemplate.put("/intervenants/"+i1.getId(), entity);
        ResponseEntity<String> response = restTemplate.getForEntity("/intervenants/"+i1.getId(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Anne");
    }

    @Test
    public void delete() throws Exception {
        Intervenant i1 = new Intervenant("Freismuth", "Arnaud", "Waltembourg", "57370");
        i1.setId(UUID.randomUUID().toString());
        ir.save(i1);

        restTemplate.delete("/intervenants/"+i1.getId());
        ResponseEntity<String> response = restTemplate.getForEntity("/intervenants/"+i1.getId(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);



    }

    private String toJsonString(Object r) throws Exception {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(r);
    }
}
