package gt.intecap.cursosapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Datos necesarios para inscribir a un participante.
 *
 * @param idCurso identificador del curso
 * @param idParticipante identificador del participante
 */
public record InscripcionSolicitud(
        @NotNull(message = "El curso es obligatorio")
        @Positive(message = "El identificador del curso debe ser positivo")
        Long idCurso,

        @NotNull(message = "El participante es obligatorio")
        @Positive(message = "El identificador del participante debe ser positivo")
        Long idParticipante
) {
}