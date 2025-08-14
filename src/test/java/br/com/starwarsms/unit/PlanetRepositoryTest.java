package br.com.starwarsms.unit;

import br.com.starwarsms.domain.Planet;
import br.com.starwarsms.domain.PlanetRepository;
import br.com.starwarsms.domain.QueryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import static br.com.starwarsms.common.PlanetConstants.PLANET_1;
import static br.com.starwarsms.common.PlanetConstants.PLANET_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager entityManager;

    Planet planet1;

    @BeforeEach
    public void setUp() {
        planet1 = new Planet(null, PLANET_1.getName(), PLANET_2.getClimate(), PLANET_2.getTerrain());
        entityManager.flush();

    }

    @AfterEach
    public void tearDown() {
        entityManager.clear();
        planetRepository.deleteAll();
    }

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        var planet = planetRepository.save(planet1);

        var sut = entityManager.find(Planet.class, planet.getId());

        assertNotNull(sut);
        assertEquals(sut.getName(), planet.getName());
        assertEquals(sut.getClimate(), planet.getClimate());
        assertEquals(sut.getTerrain(), planet.getTerrain());
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        var emptyPlanet = new Planet();
        var invalidPlanet = new Planet(null, "", "", "");

        assertThrows(RuntimeException.class, () -> planetRepository.save(emptyPlanet));
        assertThrows(RuntimeException.class, () -> planetRepository.save(invalidPlanet));
    }

    @Test
    public void createPlanet_WithExistingName_ThrowsException() {
        var planet = entityManager.persistFlushFind(planet1);

        assertThrows(RuntimeException.class, () -> planetRepository.save(new Planet(null, planet.getName(), planet.getClimate(), planet.getTerrain())));
    }

    @Test
    public void getPlanetById_WithValidId_ReturnsPlanet() {
        var planet = entityManager.persistFlushFind(planet1);

        var sut = planetRepository.findById(planet.getId());

        assertTrue(sut.isPresent());
        assertEquals(sut.get().getName(), planet.getName());
        assertEquals(sut.get().getClimate(), planet.getClimate());
        assertEquals(sut.get().getTerrain(), planet.getTerrain());
    }

    @Test
    public void getPlanetById_WithInvalidId_ReturnsEmpty() {
        var sut = planetRepository.findById(0L);

        assertFalse(sut.isPresent());
    }

    @Test
    public void getPlanetByName_WithValidName_ReturnsPlanet() {
        var planet = entityManager.persistFlushFind(planet1);

        var sut = planetRepository.findByName(planet.getName());

        assertTrue(sut.isPresent());
        assertEquals(sut.get().getName(), planet.getName());
        assertEquals(sut.get().getClimate(), planet.getClimate());
        assertEquals(sut.get().getTerrain(), planet.getTerrain());
    }

    @Test
    public void getPlanetByName_WithUnexistingName_ReturnsEmpty() {
        var planet = entityManager.persistFlushFind(planet1);

        var sut = planetRepository.findByName("unexistingname");

        assertFalse(sut.isPresent());
    }

    @Sql(scripts = {"/import_planets.sql"})
    @Test
    public void getPlanetList_ReturnsFilteredPlanets() {
        Example<Planet> queryWithFilters = QueryBuilder.buildQuery(new Planet(null, null, "frozen", "tundra"));
        Example<Planet> queryWithoutFilters = QueryBuilder.buildQuery(new Planet());

        var responseWithFilters = planetRepository.findAll(queryWithFilters);
        var responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);

        assertNotNull(responseWithFilters);
        assertEquals(2, responseWithFilters.size());
        assertEquals("frozen", responseWithFilters.getFirst().getClimate());
        assertEquals("tundra", responseWithFilters.getFirst().getTerrain());

        assertNotNull(responseWithoutFilters);
        assertEquals(4, responseWithoutFilters.size());
    }

    @Test
    public void getPlanetList_ByUnexistingClimateAndTerrain_ReturnsEmptyList() {
        var planet = new Planet(null, null, "teste", "teste");

        Example<Planet> queryWithFilters = QueryBuilder.buildQuery(planet);

        var sut = planetRepository.findAll(queryWithFilters);

        assertNotNull(sut);
        assertEquals(0, sut.size());
    }

    @Sql(scripts = {"/import_planets.sql"})
    @Test
    public void getPlanetList_ByClimateOrTerrain_ReturnsPlanets() {
        Example<Planet> queryClimateFilter = QueryBuilder.buildQuery(new Planet(null, null, "murky", null));
        Example<Planet> queryTerrainFilter = QueryBuilder.buildQuery(new Planet(null, null, null, "swamp"));

        var responseClimatePlanet = planetRepository.findAll(queryClimateFilter);
        var responseTerrainPlanet = planetRepository.findAll(queryTerrainFilter);

        assertNotNull(responseClimatePlanet);
        assertEquals(1, responseClimatePlanet.size());
        assertEquals("murky", responseClimatePlanet.getFirst().getClimate());

        assertNotNull(responseTerrainPlanet);
        assertEquals(1, responseTerrainPlanet.size());
        assertEquals("swamp", responseTerrainPlanet.getFirst().getTerrain());
    }

    @Test
    public void deletePlanetById_WithValidId_DeletesPlanet() {
        var planetTobeDeleted = entityManager.persistFlushFind(new Planet(null, "any", "climate", "terrain"));

        assertNotNull(planetTobeDeleted);

        planetRepository.deleteById(planetTobeDeleted.getId());

        var sut = entityManager.find(Planet.class, planetTobeDeleted.getId());

        assertNull(sut);
    }
}
