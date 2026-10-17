package gt.intecap.cursosweb.dto;

/**
 * Representa la disponibilidad recibida desde la API.
 */
public record CupoCursoRespuesta(
        Long idCurso,
        String nombreCurso,
        boolean activo,
        int cupoTotal,
        long inscritosActivos,
        long cuposDisponibles
) {
}