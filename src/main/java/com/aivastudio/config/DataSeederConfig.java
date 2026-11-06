package com.aivastudio.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aivastudio.model.entity.AiModel;
import com.aivastudio.repository.AiModelRepository;

@Configuration
public class DataSeederConfig {

    @Bean
    CommandLineRunner seedAiModels(AiModelRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.saveAll(List.of(
                    new AiModel("lumina-v2", "Lumina Video Pro v2", "Lumina", "2.0",
                            new BigDecimal("2"), "Equilibrado",
                            "Una opción versátil para anuncios, historias y contenido diario.", "coral"),
                    new AiModel("sora-api", "Sora API Direct", "OpenAI", "1.0",
                            new BigDecimal("5"), "Alta fidelidad",
                            "Escenas detalladas y narrativas con acabado cinematográfico.", "blue"),
                    new AiModel("runway-gen3", "Runway Gen-3 Alpha", "Runway", "Gen-3 Alpha",
                            new BigDecimal("4"), "Creativo",
                            "Ideal para explorar estilos, movimiento y conceptos visuales.", "gold"),
                    new AiModel("svd-xl", "Stable Video Diffusion XL", "Stability AI", "XL",
                            new BigDecimal("1"), "Ligero",
                            "Prueba ideas y conceptos con un consumo de créditos menor.", "green")));
        };
    }
}
