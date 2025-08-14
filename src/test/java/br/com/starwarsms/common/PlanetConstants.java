package br.com.starwarsms.common;


import br.com.starwarsms.domain.Planet;

public final class PlanetConstants {

    private PlanetConstants() {
    }

    public static final Planet PLANET_1 = new Planet(null, "Tatooine", "temperate", "jungle");
    public static final Planet PLANET_2 = new Planet(2L, "Alderaan", "temperate", "forest");
    public static final Planet INVALID_PLANET = new Planet(null, "", null, "");

}
