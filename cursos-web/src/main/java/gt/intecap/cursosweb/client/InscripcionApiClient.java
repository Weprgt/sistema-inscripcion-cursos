package gt.intecap.cursosweb.client;

import gt.intecap.cursosweb.dto.InscripcionRespuesta;
import gt.intecap.cursosweb.dto.InscripcionSolicitud;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Consume los endpoints de inscripciones mediante HTTP.
 */
@Component
public class InscripcionApiClient {

    private final RestClient restClient;

    public InscripcionApiClient(RestClient cursosRestClient) {
        this.restClient = cursosRestClient;
    }

    public List<InscripcionRespuesta> listar() {
        return restClient.get()
                .uri("/api/inscripciones")
                .retrieve()
                .body(new ParameterizedTypeReference<
                        List<InscripcionRespuesta>>() {});
    }

    /**
     * Registra una inscripción o reactiva una cancelada.
     */
    public InscripcionRespuesta inscribir(
            InscripcionSolicitud solicitud
    ) {
        return restClient.post()
                .uri("/api/inscripciones")
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitud)
                .retrieve()
                .body(InscripcionRespuesta.class);
    }

    public InscripcionRespuesta cancelar(Long id) {
        return restClient.put()
                .uri("/api/inscripciones/{id}/cancelacion", id)
                .retrieve()
                .body(InscripcionRespuesta.class);
    }

    /**
     * Busca inscripciones por curso o participante.
     */
    public List<InscripcionRespuesta> buscar(String texto) {
        return restClient.get()
                .uri(uriBuilder -> {
                    var uri = uriBuilder.path("/api/inscripciones");

                    if (texto != null && !texto.isBlank()) {
                        return uri.queryParam("texto", "{texto}")
                                .build(texto.trim());
                    }

                    return uri.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<
                        List<InscripcionRespuesta>>() {});
    }
}