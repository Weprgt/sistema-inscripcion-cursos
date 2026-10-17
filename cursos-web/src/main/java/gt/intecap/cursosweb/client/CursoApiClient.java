package gt.intecap.cursosweb.client;

import gt.intecap.cursosweb.dto.CursoRespuesta;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import gt.intecap.cursosweb.dto.CursoSolicitud;
import org.springframework.http.MediaType;
import gt.intecap.cursosweb.dto.CupoCursoRespuesta;
import gt.intecap.cursosweb.dto.InscripcionRespuesta;

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
    /**
     * Consulta un curso por su identificador.
     */
    public CursoRespuesta buscarPorId(Long id) {
        return restClient.get()
                .uri("/api/cursos/{id}", id)
                .retrieve()
                .body(CursoRespuesta.class);
    }

    /**
     * Actualiza un curso mediante la API.
     */
    public CursoRespuesta actualizar(Long id, CursoSolicitud solicitud) {
        return restClient.put()
                .uri("/api/cursos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitud)
                .retrieve()
                .body(CursoRespuesta.class);
    }
    /**
     * Consulta los cupos de un curso.
     */
    public CupoCursoRespuesta consultarCupos(Long id) {
        return restClient.get()
                .uri("/api/cursos/{id}/cupos", id)
                .retrieve()
                .body(CupoCursoRespuesta.class);
    }

    /**
     * Consulta las inscripciones activas y canceladas de un curso.
     */
    public List<InscripcionRespuesta> listarInscripciones(Long idCurso) {
        return restClient.get()
                .uri("/api/inscripciones/curso/{idCurso}", idCurso)
                .retrieve()
                .body(new ParameterizedTypeReference<
                        List<InscripcionRespuesta>>() {});
    }
    /**
     * Solicita eliminar un curso sin inscripciones.
     */
    public void eliminar(Long id) {
        restClient.delete()
                .uri("/api/cursos/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}