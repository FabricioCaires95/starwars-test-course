package br.com.starwarsms.unit;


import br.com.starwarsms.domain.Planet;
import br.com.starwarsms.domain.PlanetRepository;
import br.com.starwarsms.domain.PlanetService;
import br.com.starwarsms.domain.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.Optional;


import static br.com.starwarsms.common.PlanetConstants.INVALID_PLANET;
import static br.com.starwarsms.common.PlanetConstants.PLANET_1;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {

    @InjectMocks
    private PlanetService planetService;

    @Mock
    private PlanetRepository planetRepository;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        when(planetRepository.save(any())).thenReturn(PLANET_1);

        var planet = planetService.createPlanet(PLANET_1);

        assertNotNull(planet);
        assertEquals(PLANET_1.getName(), planet.getName());
        assertEquals(PLANET_1.getClimate(), planet.getClimate());
        assertEquals(PLANET_1.getTerrain(), planet.getTerrain());
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        when(planetRepository.save(INVALID_PLANET)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> planetService.createPlanet(INVALID_PLANET));
    }

    @Test
    public void getPlanetById_WithValidId_ReturnsPlanet() {
        when(planetRepository.findById(any())).thenReturn(Optional.of(PLANET_1));

        var planet = planetService.getPlanetById(PLANET_1.getId());

        assertTrue(planet.isPresent());
        assertEquals(PLANET_1.getName(), planet.get().getName());
        assertEquals(PLANET_1.getClimate(), planet.get().getClimate());
        assertEquals(PLANET_1.getTerrain(), planet.get().getTerrain());
    }

    @Test
    public void getPlanetById_WithInvalidId_ThrowsException() {
        when(planetRepository.findById(any())).thenReturn(Optional.empty());

        var planet = planetService.getPlanetById(1L);

        assertFalse(planet.isPresent());
    }

    @Test
    public void getPlanetByName_WithValidName_ReturnsPlanet() {
        when(planetRepository.findByName(any())).thenReturn(Optional.of(PLANET_1));

        var planet = planetService.getPlanetByName(PLANET_1.getName());

        assertTrue(planet.isPresent());
        assertEquals(PLANET_1.getName(), planet.get().getName());
        assertEquals(PLANET_1.getClimate(), planet.get().getClimate());
        assertEquals(PLANET_1.getTerrain(), planet.get().getTerrain());
    }

    @Test
    public void getPlanetByName_ByUnexistedName_ThrowsException() {
        when(planetRepository.findByName(any())).thenReturn(Optional.empty());

        var planet = planetService.getPlanetByName("unexisted name");

        assertFalse(planet.isPresent());
    }

    @Test
    public void getPlanets_WithBothFilters_ReturnsPlanets() {
        Example<Planet> query = QueryBuilder.buildQuery(new Planet(null, null, PLANET_1.getClimate(), PLANET_1.getTerrain()));

        when(planetRepository.findAll(query)).thenReturn(List.of(PLANET_1));

        var planets = planetService.getPlanets(PLANET_1.getClimate(), PLANET_1.getTerrain());

        assertNotNull(planets);
        assertEquals(1, planets.size());

        verify(planetRepository).findAll(query);
    }

    @Test
    public void getPlanets_WithBothFilters_ReturnsEmptyList() {
        Example<Planet> query = QueryBuilder.buildQuery(new Planet(null, null, PLANET_1.getClimate(), PLANET_1.getTerrain()));

        when(planetRepository.findAll(query)).thenReturn(List.of());

        var planets = planetService.getPlanets(PLANET_1.getClimate(), PLANET_1.getTerrain());

        assertNotNull(planets);
        assertEquals(0, planets.size());
    }

    @Test
    public void deletePlanet_WithValidId_DeletesPlanet() {
        assertDoesNotThrow( () -> planetService.deletePlanet(PLANET_1.getId()));
    }

    @Test
    public void deletePlanet_WithInvalidId_ThrowsException() {
        doThrow(new RuntimeException()).when(planetRepository).deleteById(any());

        assertThrows(RuntimeException.class, () -> planetService.deletePlanet(1L));

    }

}
