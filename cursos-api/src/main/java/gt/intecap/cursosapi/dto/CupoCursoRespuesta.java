package gt.intecap.cursosapi.dto;

/**
 * Resume la capacidad y ocupación de un curso.
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