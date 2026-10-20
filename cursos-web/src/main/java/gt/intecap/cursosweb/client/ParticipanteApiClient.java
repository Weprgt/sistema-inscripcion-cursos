package gt.intecap.cursosweb.client;

import gt.intecap.cursosweb.dto.ParticipanteRespuesta;
import gt.intecap.cursosweb.dto.ParticipanteSolicitud;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Consume los endpoints de participantes mediante HTTP.
 */
@Component
public class ParticipanteApiClient {

    private final RestClient restClient;

    public ParticipanteApiClient(RestClient cursosRestClient) {
        this.restClient = cursosRestClient;
    }

    /**
     * Obtiene todos los participantes para los formularios.
     */
    public List<ParticipanteRespuesta> listar() {
        return buscar("");
    }

    /**
     * Busca participantes por nombre, apellidos o correo.
     */
    public List<ParticipanteRespuesta> buscar(String texto) {
        return restClient.get()
                .uri(uriBuilder -> {
                    var uri = uriBuilder.path("/api/participantes");

                    if (texto != null && !texto.isBlank()) {
                        return uri.queryParam("texto", "{texto}")
                                .build(texto.trim());
                    }

                    return uri.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<
                        List<ParticipanteRespuesta>>() {});
    }

    public ParticipanteRespuesta buscarPorId(Long id) {
        return restClient.get()
                .uri("/api/participantes/{id}", id)
                .retrieve()
                .body(ParticipanteRespuesta.class);
    }

    public ParticipanteRespuesta crear(ParticipanteSolicitud solicitud) {
        return restClient.post()
                .uri("/api/participantes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitud)
                .retrieve()
                .body(ParticipanteRespuesta.class);
    }

    public ParticipanteRespuesta actualizar(
            Long id,
            ParticipanteSolicitud solicitud
    ) {
        return restClient.put()
                .uri("/api/participantes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitud)
                .retrieve()
                .body(ParticipanteRespuesta.class);
    }
    /**
     * Solicita eliminar un participante sin inscripciones.
     */
    public void eliminar(Long id) {
        restClient.delete()
                .uri("/api/participantes/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}