package gt.intecap.cursosapi.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Representa una persona que puede inscribirse en cursos.
 */
@Entity
@Table(name = "participantes")
@AttributeOverride(
        name = "id",
        column = @Column(name = "id_participante")
)
public class Participante extends Persona {

}