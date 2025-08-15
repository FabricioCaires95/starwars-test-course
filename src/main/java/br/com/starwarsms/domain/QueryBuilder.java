package br.com.starwarsms.domain;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

public class QueryBuilder {

    private QueryBuilder() {
        // Private constructor to hide implicit public one.
    }

    public static Example<Planet> buildQuery(Planet planet) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreCase().withIgnoreNullValues();
        return Example.of(planet, matcher);
    }
}
