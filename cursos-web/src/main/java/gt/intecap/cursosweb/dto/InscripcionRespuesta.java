package gt.intecap.cursosweb.dto;

import java.time.LocalDate;

/**
 * Representa una inscripción recibida desde la API.
 */
public record InscripcionRespuesta(
        Long id,
        Long idCurso,
        String nombreCurso,
        Long idParticipante,
        String nombreParticipante,
        LocalDate fechaInscripcion,
        String estado
) {
}