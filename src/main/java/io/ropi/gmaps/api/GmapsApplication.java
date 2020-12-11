package io.ropi.gmaps.api;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.ropi.gmaps.api.image.FactorioMapSupplier;
import io.ropi.gmaps.api.map.FactorioMap;
import io.ropi.gmaps.api.parse.Chunk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@SpringBootApplication
public class GmapsApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmapsApplication.class, args);
    }

    @Bean
    public FactorioMapSupplier factorioMapSupplier(ResourceLoader resourceLoader) throws IOException {
        File mapFile = resourceLoader.getResource("classpath:map.json").getFile();
        AtomicReference<FactorioMap> reference = new AtomicReference<>();

        return () -> {
            try {
                if(reference.get() == null) {
                    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    Map<String, Chunk> chunkMap = mapper.readValue(mapFile, new TypeReference<>() {});

                    reference.set(new FactorioMap(chunkMap));
                }

                return reference.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
