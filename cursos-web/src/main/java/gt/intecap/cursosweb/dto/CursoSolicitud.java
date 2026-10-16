package gt.intecap.cursosweb.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Datos para registrar un curso desde el formulario web.
 */
public record CursoSolicitud(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 20, message = "El código admite hasta 20 caracteres")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite hasta 100 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 500, message = "La descripción admite hasta 500 caracteres")
        String descripcion,

        @NotBlank(message = "Selecciona una modalidad")
        @Pattern(
                regexp = "PRESENCIAL|VIRTUAL|MIXTA",
                message = "Selecciona una modalidad válida"
        )
        String modalidad,

        @NotNull(message = "El cupo es obligatorio")
        @Min(value = 1, message = "El cupo mínimo es 1")
        @Max(value = 40, message = "El cupo máximo es 40")
        Integer cupo,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fechaInicio,

        @NotNull(message = "Selecciona el estado")
        Boolean activo
) {
}