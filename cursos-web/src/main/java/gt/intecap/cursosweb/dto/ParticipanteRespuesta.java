package gt.intecap.cursosweb.dto;

/**
 * Representa un participante recibido desde la API.
 */
public record ParticipanteRespuesta(
        Long id,
        String nombres,
        String apellidos,
        String correo,
        String telefono,
        String nombreCompleto
) {
}