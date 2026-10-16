package gt.intecap.cursosapi.controller;

import gt.intecap.cursosapi.dto.InscripcionRespuesta;
import gt.intecap.cursosapi.dto.InscripcionSolicitud;
import gt.intecap.cursosapi.service.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expone las operaciones REST de inscripciones.
 */
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    /**
     * Inicializa el controlador.
     * @param inscripcionService servicio de inscripciones
     */
    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    /**
     * Lista las inscripciones.
     * @return inscripciones registradas
     */
    @GetMapping
    public List<InscripcionRespuesta> listar() {
        return inscripcionService.listar();
    }

    /**
     * Consulta una inscripción.
     * @param id identificador de inscripción
     * @return inscripción encontrada
     */
    @GetMapping("/{id}")
    public InscripcionRespuesta buscarPorId(@PathVariable Long id) {
        return inscripcionService.buscarPorId(id);
    }

    /**
     * Crea una inscripción o reactiva una cancelada.
     * @param solicitud identificadores del curso y participante
     * @return inscripción creada o reactivada
     */
    @PostMapping
    public InscripcionRespuesta inscribir(
            @Valid @RequestBody InscripcionSolicitud solicitud
    ) {
        return inscripcionService.inscribir(
                solicitud.idCurso(),
                solicitud.idParticipante()
        );
    }

    /**
     * Cancela una inscripción.
     * @param id identificador de inscripción
     * @return inscripción cancelada
     */
    @PutMapping("/{id}/cancelacion")
    public InscripcionRespuesta cancelar(@PathVariable Long id) {
        return inscripcionService.cancelar(id);
    }

    /**
     * Consulta las inscripciones de un curso.
     * @param idCurso identificador del curso
     * @return inscripciones del curso
     */
    @GetMapping("/curso/{idCurso}")
    public List<InscripcionRespuesta> listarPorCurso(
            @PathVariable Long idCurso
    ) {
        return inscripcionService.listarPorCurso(idCurso);
    }
}