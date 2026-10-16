package gt.intecap.cursosapi.dao;

import gt.intecap.cursosapi.model.EstadoInscripcion;
import gt.intecap.cursosapi.model.Inscripcion;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementa el acceso a las inscripciones mediante JPA.
 */
@Repository
@Transactional(readOnly = true)
public class InscripcionDaoJpa implements GenericDao<Inscripcion, Long> {

    private final EntityManager entityManager;

    /**
     * Inicializa el DAO.
     * @param entityManager administrador de entidades JPA
     */
    public InscripcionDaoJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /** {@inheritDoc} */
    @Override
    public List<Inscripcion> listar() {
        return entityManager.createQuery(
                "SELECT i FROM Inscripcion i ORDER BY i.id",
                Inscripcion.class
        ).getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Inscripcion> buscarPorId(Long id) {
        return Optional.ofNullable(
                entityManager.find(Inscripcion.class, id)
        );
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Inscripcion guardar(Inscripcion inscripcion) {
        if (inscripcion.getId() == null) {
            entityManager.persist(inscripcion);
            return inscripcion;
        }

        return entityManager.merge(inscripcion);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void eliminarPorId(Long id) {
        Inscripcion inscripcion =
                entityManager.find(Inscripcion.class, id);

        if (inscripcion != null) {
            entityManager.remove(inscripcion);
        }
    }

    /**
     * Busca la inscripción de un participante en un curso,
     * independientemente de su estado.
     * @param idCurso identificador del curso
     * @param idParticipante identificador del participante
     * @return inscripción encontrada o un Optional vacío
     */
    public Optional<Inscripcion> buscarPorCursoYParticipante(
            Long idCurso,
            Long idParticipante
    ) {
        return entityManager.createQuery("""
                SELECT i FROM Inscripcion i
                WHERE i.curso.id = :idCurso
                  AND i.participante.id = :idParticipante
                """, Inscripcion.class)
                .setParameter("idCurso", idCurso)
                .setParameter("idParticipante", idParticipante)
                .getResultStream()
                .findFirst();
    }

    /**
     * Cuenta las inscripciones activas de un curso.
     * @param idCurso identificador del curso
     * @return cantidad de inscripciones activas
     */
    public long contarActivasPorCurso(Long idCurso) {
        return entityManager.createQuery("""
                SELECT COUNT(i) FROM Inscripcion i
                WHERE i.curso.id = :idCurso
                  AND i.estado = :estado
                """, Long.class)
                .setParameter("idCurso", idCurso)
                .setParameter("estado", EstadoInscripcion.ACTIVA)
                .getSingleResult();
    }

    /**
     * Lista las inscripciones de un curso, incluidas las canceladas.
     * @param idCurso identificador del curso
     * @return inscripciones del curso
     */
    public List<Inscripcion> listarPorCurso(Long idCurso) {
        return entityManager.createQuery("""
                SELECT i FROM Inscripcion i
                WHERE i.curso.id = :idCurso
                ORDER BY i.id
                """, Inscripcion.class)
                .setParameter("idCurso", idCurso)
                .getResultList();
    }

    /**
     * Comprueba si un curso tiene cualquier inscripción.
     * @param idCurso identificador del curso
     * @return true si tiene inscripciones activas o canceladas
     */
    public boolean existePorCurso(Long idCurso) {
        return entityManager.createQuery("""
                SELECT COUNT(i) FROM Inscripcion i
                WHERE i.curso.id = :idCurso
                """, Long.class)
                .setParameter("idCurso", idCurso)
                .getSingleResult() > 0;
    }

    /**
     * Comprueba si un participante tiene cualquier inscripción.
     * @param idParticipante identificador del participante
     * @return true si tiene inscripciones activas o canceladas
     */
    public boolean existePorParticipante(Long idParticipante) {
        return entityManager.createQuery("""
                SELECT COUNT(i) FROM Inscripcion i
                WHERE i.participante.id = :idParticipante
                """, Long.class)
                .setParameter("idParticipante", idParticipante)
                .getSingleResult() > 0;
    }
}