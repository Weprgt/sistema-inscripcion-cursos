package gt.intecap.cursosweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configura el cliente HTTP que consume cursos-api.
 */
@Configuration
public class ApiConfig {

    @Bean
    public RestClient cursosRestClient(
            RestClient.Builder builder,
            @Value("${cursos.api.url}") String apiUrl
    ) {
        return builder
                .baseUrl(apiUrl)
                .build();
    }
}