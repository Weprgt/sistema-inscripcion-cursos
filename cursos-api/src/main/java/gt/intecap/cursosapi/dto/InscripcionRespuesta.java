package gt.intecap.cursosapi.dto;

import gt.intecap.cursosapi.model.EstadoInscripcion;

import java.time.LocalDate;

/**
 * Datos de una inscripción que devuelve la API.
 *
 * @param id identificador de inscripción
 * @param idCurso identificador del curso
 * @param nombreCurso nombre del curso
 * @param idParticipante identificador del participante
 * @param nombreParticipante nombre completo del participante
 * @param fechaInscripcion fecha original de inscripción
 * @param estado estado actual
 */
public record InscripcionRespuesta(
        Long id,
        Long idCurso,
        String nombreCurso,
        Long idParticipante,
        String nombreParticipante,
        LocalDate fechaInscripcion,
        EstadoInscripcion estado
) {
}