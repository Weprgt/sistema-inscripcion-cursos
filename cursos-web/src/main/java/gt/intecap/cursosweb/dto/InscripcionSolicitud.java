package gt.intecap.cursosweb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Datos para registrar o reactivar una inscripción mediante la API.
 */
public record InscripcionSolicitud(
        @NotNull(message = "Selecciona un curso")
        @Positive(message = "El identificador del curso debe ser positivo")
        Long idCurso,

        @NotNull(message = "Selecciona un participante")
        @Positive(message = "El identificador del participante debe ser positivo")
        Long idParticipante
) {
}