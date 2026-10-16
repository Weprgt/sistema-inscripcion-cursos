package gt.intecap.cursosapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Define los datos personales comunes de los participantes.
 */
@MappedSuperclass
public abstract class Persona extends EntidadBase {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres admiten hasta 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos admiten hasta 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 150, message = "El correo admite hasta 150 caracteres")
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "[0-9]{8}",
            message = "El teléfono debe contener exactamente 8 dígitos"
    )
    @Column(nullable = false, length = 8)
    private String telefono;

    /** @return nombres de la persona */
    public String getNombres() {
        return nombres;
    }

    /** @param nombres nombres de la persona */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /** @return apellidos de la persona */
    public String getApellidos() {
        return apellidos;
    }

    /** @param apellidos apellidos de la persona */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /** @return correo de la persona */
    public String getCorreo() {
        return correo;
    }

    /** @param correo correo de la persona */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /** @return teléfono de la persona */
    public String getTelefono() {
        return telefono;
    }

    /** @param telefono teléfono de la persona */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /** @return nombres y apellidos de la persona */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}