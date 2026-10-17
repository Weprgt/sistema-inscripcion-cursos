package gt.intecap.cursosweb.dto;

import jakarta.validation.constraints.*;

/**
 * Datos del formulario de registro y edición de participantes.
 */
public record ParticipanteSolicitud(
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres admiten hasta 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos admiten hasta 100 caracteres")
        String apellidos,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Ingresa un correo válido")
        @Size(max = 150, message = "El correo admite hasta 150 caracteres")
        String correo,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(
                regexp = "[0-9]{8}",
                message = "El teléfono debe contener exactamente 8 dígitos"
        )
        String telefono
) {
}