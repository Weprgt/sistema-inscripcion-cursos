package gt.intecap.cursosapi.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Clase base que proporciona el identificador de las entidades.
 */
@MappedSuperclass
public abstract class EntidadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Obtiene el identificador de la entidad.
     *
     * @return identificador de la entidad
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador de la entidad.
     *
     * @param id identificador de la entidad
     */
    public void setId(Long id) {
        this.id = id;
    }
}