package br.com.starwarsms.web;

import br.com.starwarsms.domain.Planet;
import br.com.starwarsms.domain.PlanetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/planets")
public class PlanetController {

    private final PlanetService planetService;

    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }

    @PostMapping
    public ResponseEntity<Planet> createPlanet(@RequestBody @Valid Planet planet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planetService.createPlanet(planet));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planet> getPlanetById(@PathVariable Long id) {
        return planetService.getPlanetById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Planet> getPlanetByName(@PathVariable String name) {
        return planetService.getPlanetByName(name).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Planet>> getPlanetsByFilter(
            @RequestParam(required = false) String climate,
            @RequestParam(required = false) String terrain) {
        return ResponseEntity.ok(planetService.getPlanets(climate, terrain));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanetById(@PathVariable Long id) {
        planetService.deletePlanet(id);
        return ResponseEntity.noContent().build();
    }
}
