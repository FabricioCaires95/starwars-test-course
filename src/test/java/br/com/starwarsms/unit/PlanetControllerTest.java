package br.com.starwarsms.unit;

import br.com.starwarsms.domain.Planet;
import br.com.starwarsms.domain.PlanetService;
import br.com.starwarsms.web.PlanetController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static br.com.starwarsms.common.PlanetConstants.PLANET_1;
import static br.com.starwarsms.common.PlanetConstants.PLANET_2;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlanetService planetService;

    @Test
    public void createPlanet_WithValidData_ShouldReturn201() throws Exception {
        when(planetService.createPlanet(PLANET_1)).thenReturn(PLANET_1);

        mockMvc.perform(post("/planets")
                        .content(objectMapper.writeValueAsString(PLANET_1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(PLANET_1.getName()))
                .andExpect(jsonPath("$.climate").value(PLANET_1.getClimate()))
                .andExpect(jsonPath("$.terrain").value(PLANET_1.getTerrain()));
    }

    @Test
    public void createPlanet_WithInvalidData_ShouldReturn422() throws Exception {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet(null, "", "", "");

        mockMvc.perform(post("/planets")
                        .content(objectMapper.writeValueAsString(emptyPlanet)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/planets")
                        .content(objectMapper.writeValueAsString(invalidPlanet)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }


    @Test
    public void createPlanet_WithExistingName_ShouldReturn409() throws Exception {
        when(planetService.createPlanet(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/planets")
                        .content(objectMapper.writeValueAsString(PLANET_1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void getPlanetById_WithValidId_ShouldReturn200() throws Exception {
        when(planetService.getPlanetById(any())).thenReturn(Optional.of(PLANET_2));

        mockMvc.perform(get("/planets/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(PLANET_2.getName()))
                .andExpect(jsonPath("$.climate").value(PLANET_2.getClimate()))
                .andExpect(jsonPath("$.terrain").value(PLANET_2.getTerrain()));

    }

    @Test
    public void getPlanetById_WithInvalidId_ShouldReturn404() throws Exception {
        when(planetService.getPlanetById(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/planets/22").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPlanetByName_WithValidName_ShouldReturn200() throws Exception {
        when(planetService.getPlanetByName(any())).thenReturn(Optional.of(PLANET_2));

        mockMvc.perform(get("/planets/name/{name}", PLANET_2.getName()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(PLANET_2.getName()))
                .andExpect(jsonPath("$.climate").value(PLANET_2.getClimate()))
                .andExpect(jsonPath("$.terrain").value(PLANET_2.getTerrain()));
    }

    @Test
    public void getPlanetByName_WithInvalidName_ShouldReturn404() throws Exception {
        when(planetService.getPlanetByName(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/planets/name/{name}", "invalid name").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getPlanet_ByClimateAndTerrain_ShouldReturn200() throws Exception {
        when(planetService.getPlanets(any(), any())).thenReturn(List.of(PLANET_1));

        mockMvc.perform(get("/planets?climate=temperate?terrain=jungle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getPlanet_ByClimateAndTerrain_ShouldReturn200_and_emptyList() throws Exception {
        when(planetService.getPlanets(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/planets?climate=temperate?terrain=jungle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    public void deletePlanetById_WithValidId_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/planets/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deletePlanetById_ByUnexistedId_ShouldReturn204() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(planetService).deletePlanet(any());
        mockMvc.perform(delete("/planets/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
