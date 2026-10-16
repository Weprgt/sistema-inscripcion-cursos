package gt.intecap.cursosweb.dto;

import java.time.LocalDate;

/**
 * Representa un curso recibido desde cursos-api.
 */
public record CursoRespuesta(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        String modalidad,
        Integer cupo,
        LocalDate fechaInicio,
        Boolean activo
) {
}