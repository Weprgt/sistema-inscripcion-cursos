package gt.intecap.cursosapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Representa un curso de capacitación.
 */
@Entity
@Table(name = "cursos")
@AttributeOverride(
        name = "id",
        column = @Column(name = "id_curso")
)
public class Curso extends EntidadBase {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código admite hasta 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre admite hasta 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción admite hasta 500 caracteres")
    @Column(nullable = false, length = 500)
    private String descripcion;

    @NotNull(message = "La modalidad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Modalidad modalidad;

    @NotNull(message = "El cupo es obligatorio")
    @Min(value = 1, message = "El cupo mínimo es 1")
    @Max(value = 40, message = "El cupo máximo es 40")
    @Column(nullable = false)
    private Integer cupo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "El estado activo es obligatorio")
    @Column(nullable = false)
    private Boolean activo;

    /** @return código del curso */
    public String getCodigo() {
        return codigo;
    }

    /** @param codigo código del curso */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /** @return nombre del curso */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre nombre del curso */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return descripción del curso */
    public String getDescripcion() {
        return descripcion;
    }

    /** @param descripcion descripción del curso */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /** @return modalidad del curso */
    public Modalidad getModalidad() {
        return modalidad;
    }

    /** @param modalidad modalidad del curso */
    public void setModalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
    }

    /** @return capacidad máxima del curso */
    public Integer getCupo() {
        return cupo;
    }

    /** @param cupo capacidad máxima del curso */
    public void setCupo(Integer cupo) {
        this.cupo = cupo;
    }

    /** @return fecha de inicio del curso */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /** @param fechaInicio fecha de inicio del curso */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /** @return estado activo del curso */
    public Boolean getActivo() {
        return activo;
    }

    /** @param activo estado activo del curso */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}