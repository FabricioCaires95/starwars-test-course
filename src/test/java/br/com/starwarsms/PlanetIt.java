package br.com.starwarsms;

import br.com.starwarsms.domain.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static br.com.starwarsms.common.PlanetConstants.PLANET_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/import_planets.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/remove_planets.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PlanetIt {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createPlanet_shouldReturn200() {
        var sut = restTemplate.postForEntity("/planets", PLANET_1, Planet.class);

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.CREATED.value());
        assertNotNull(sut.getBody());
        assertNotNull(sut.getBody().getId());
        assertEquals(sut.getBody().getName(), PLANET_1.getName());
        assertEquals(sut.getBody().getClimate(), PLANET_1.getClimate());
        assertEquals(sut.getBody().getTerrain(), PLANET_1.getTerrain());
    }

    @Test
    public void createPlanet_shouldReturn422() {
        var sut = restTemplate.postForEntity("/planets", new Planet(), Planet.class);

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void getPlanetById_returnsPlanet() {
        var sut = restTemplate.getForEntity("/planets/{id}", Planet.class, 10);

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.OK.value());
        assertNotNull(sut.getBody());
    }

    @Test
    public void getPlanetById_ByInvalidId_shouldReturn404() {
        var sut = restTemplate.getForEntity("/planets/{id}", Planet.class, 0);

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getPlanetByName_returnsPlanet() {
        var sut = restTemplate.getForEntity("/planets/name/{name}", Planet.class, "Jedha");

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.OK.value());
        assertNotNull(sut.getBody());
        assertEquals("Jedha", sut.getBody().getName());
    }

    @Test
    public void getPlanetByName_returns404() {
        var sut = restTemplate.getForEntity("/planets/name/{name}", Planet.class, "Tatooine");

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void listPlanets_ByClimate_returnsPlanets() {
        var sut = restTemplate.getForEntity("/planets?climate=arid", Planet[].class);

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.OK.value());
        assertNotNull(sut.getBody());
        assertEquals(1, sut.getBody().length);
        assertEquals("arid", sut.getBody()[0].getClimate());
    }

    @Test
    public void listPlanets_ByTerrain_returnsPlanets() {
        var sut = restTemplate.getForEntity("/planets?terrain=swamp", Planet[].class);

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.OK.value());
        assertNotNull(sut.getBody());
        assertEquals(1, sut.getBody().length);
        assertEquals("swamp", sut.getBody()[0].getTerrain());
    }

    @Test
    public void listPlanets_returnsAllPlanets() {
        var sut = restTemplate.getForEntity("/planets", Planet[].class);

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.OK.value());
        assertNotNull(sut.getBody());
        assertEquals(4, sut.getBody().length);
    }

    @Test
    public void listPlanets_ByClimateAndByTerrain_returnsPlanets() {
        var sut = restTemplate.getForEntity("/planets?climate=frozen&terrain=tundra", Planet[].class);

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.OK.value());
        assertNotNull(sut.getBody());
        assertEquals(2, sut.getBody().length);
        assertEquals("frozen", sut.getBody()[0].getClimate());
        assertEquals("tundra", sut.getBody()[0].getTerrain());
        assertEquals("frozen", sut.getBody()[1].getClimate());
        assertEquals("tundra", sut.getBody()[1].getTerrain());
    }

    @Test
    public void deletePlanetById_shouldReturn204() {
        var sut = restTemplate.exchange("/planets/{id}", HttpMethod.DELETE, null, Void.class, 10);

        assertNotNull(sut);
        assertEquals(sut.getStatusCode().value(), HttpStatus.NO_CONTENT.value());
    }


}
