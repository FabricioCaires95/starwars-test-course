package br.com.starwarsms.domain;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static br.com.starwarsms.domain.QueryBuilder.buildQuery;

@Service
public class PlanetService {

    private final PlanetRepository planetRepository;

    public PlanetService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    public Planet createPlanet(Planet planet) {
        return planetRepository.save(planet);
    }

    public Optional<Planet> getPlanetById(Long id) {
        return planetRepository.findById(id);
    }

    public Optional<Planet> getPlanetByName(String name) {
        return planetRepository.findByName(name);
    }

    public List<Planet> getPlanets(String climate, String terrain) {
        Example<Planet> quey = buildQuery(new Planet(null, null, climate, terrain));
        return planetRepository.findAll(quey);
    }

    public void deletePlanet(Long id) {
        planetRepository.deleteById(id);
    }
}
