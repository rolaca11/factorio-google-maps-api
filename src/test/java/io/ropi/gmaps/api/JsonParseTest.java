package io.ropi.gmaps.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.ropi.gmaps.api.image.FactorioMapSupplier;
import io.ropi.gmaps.api.map.Chunk;
import io.ropi.gmaps.api.map.FactorioMap;
import io.ropi.gmaps.api.map.Tile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@SpringBootTest
@Slf4j
class JsonParseTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private FactorioMapSupplier factorioMapSupplier;

    @Test
    void test() throws IOException {

        log.warn("\n{}", factorioMapSupplier.get().getChunks().values().stream()
                .map(Chunk::getTiles)
                .map(Map::values)
                .flatMap(Collection::parallelStream)
                .map(Tile::getName)
                .distinct()
                .sorted()
                .reduce((left, right) -> left + "\n" + right).orElse("")
        );
    }

}
