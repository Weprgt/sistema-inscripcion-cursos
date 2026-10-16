package gt.intecap.cursosweb.client;

import gt.intecap.cursosweb.dto.CursoRespuesta;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import gt.intecap.cursosweb.dto.CursoSolicitud;
import org.springframework.http.MediaType;

import java.util.List;

/**
 * Consume los endpoints de cursos mediante HTTP.
 */
@Component
public class CursoApiClient {

    private final RestClient restClient;

    public CursoApiClient(RestClient cursosRestClient) {
        this.restClient = cursosRestClient;
    }

    /**
     * Obtiene los cursos registrados en la API.
     * @return lista de cursos
     */
    public List<CursoRespuesta> listar() {
        return restClient.get()
                .uri("/api/cursos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CursoRespuesta>>() {});
    }
    /**
     * Registra un curso mediante la API.
     * @param solicitud datos del nuevo curso
     * @return curso creado
     */
    public CursoRespuesta crear(CursoSolicitud solicitud) {
        return restClient.post()
                .uri("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitud)
                .retrieve()
                .body(CursoRespuesta.class);
    }
}