package gt.intecap.cursosapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Relaciona un participante con un curso y conserva su inscripción.
 */
@Entity
@Table(
        name = "inscripciones",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inscripcion_curso_participante",
                columnNames = {"id_curso", "id_participante"}
        )
)
@AttributeOverride(
        name = "id",
        column = @Column(name = "id_inscripcion")
)
public class Inscripcion extends EntidadBase {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_participante", nullable = false)
    private Participante participante;

    @NotNull
    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoInscripcion estado;

    /** @return curso asociado */
    public Curso getCurso() {
        return curso;
    }

    /** @param curso curso asociado */
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    /** @return participante asociado */
    public Participante getParticipante() {
        return participante;
    }

    /** @param participante participante asociado */
    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    /** @return fecha original de inscripción */
    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    /** @param fechaInscripcion fecha original de inscripción */
    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    /** @return estado de la inscripción */
    public EstadoInscripcion getEstado() {
        return estado;
    }

    /** @param estado estado de la inscripción */
    public void setEstado(EstadoInscripcion estado) {
        this.estado = estado;
    }

    /**
     * Cancela la inscripción conservando su historial.
     */
    public void cancelar() {
        this.estado = EstadoInscripcion.CANCELADA;
    }

    /**
     * Reactiva la inscripción tras validar disponibilidad en el servicio.
     */
    public void reactivar() {
        this.estado = EstadoInscripcion.ACTIVA;
    }
}