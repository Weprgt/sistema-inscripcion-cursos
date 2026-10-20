package gt.intecap.cursosapi.dao;

import gt.intecap.cursosapi.model.Participante;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementa el acceso a los participantes mediante JPA.
 */
@Repository
@Transactional(readOnly = true)
public class ParticipanteDaoJpa implements GenericDao<Participante, Long> {

    private final EntityManager entityManager;

    /**
     * Inicializa el DAO.
     * @param entityManager administrador de entidades JPA
     */
    public ParticipanteDaoJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /** {@inheritDoc} */
    @Override
    public List<Participante> listar() {
        return entityManager.createQuery(
                "SELECT p FROM Participante p ORDER BY p.id",
                Participante.class
        ).getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Participante> buscarPorId(Long id) {
        return Optional.ofNullable(
                entityManager.find(Participante.class, id)
        );
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Participante guardar(Participante participante) {
        if (participante.getId() == null) {
            entityManager.persist(participante);
            return participante;
        }

        return entityManager.merge(participante);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void eliminarPorId(Long id) {
        Participante participante =
                entityManager.find(Participante.class, id);

        if (participante != null) {
            entityManager.remove(participante);
        }
    }

    /**
     * Comprueba si otro participante utiliza el correo.
     * @param correo correo que se comprobará
     * @param idExcluir identificador excluido o null al registrar
     * @return true si el correo ya está registrado
     */
    public boolean existeCorreo(String correo, Long idExcluir) {
        String jpql = """
                SELECT COUNT(p) FROM Participante p
                WHERE LOWER(p.correo) = LOWER(:correo)
                """;

        if (idExcluir != null) {
            jpql += " AND p.id <> :idExcluir";
        }

        var consulta = entityManager.createQuery(jpql, Long.class)
                .setParameter("correo", correo);

        if (idExcluir != null) {
            consulta.setParameter("idExcluir", idExcluir);
        }

        return consulta.getSingleResult() > 0;
    }
    /**
     * Busca participantes por nombres, apellidos o correo.
     * @param texto texto normalizado a minúsculas
     * @return participantes encontrados
     */
    public List<Participante> buscar(String texto) {
        return entityManager.createQuery("""
            SELECT p FROM Participante p
            WHERE LOCATE(:texto, LOWER(p.nombres)) > 0
               OR LOCATE(:texto, LOWER(p.apellidos)) > 0
               OR LOCATE(
                    :texto,
                    LOWER(CONCAT(CONCAT(p.nombres, ' '), p.apellidos))
                  ) > 0
               OR LOCATE(:texto, LOWER(p.correo)) > 0
            ORDER BY LOWER(p.apellidos), LOWER(p.nombres), p.id
            """, Participante.class)
                .setParameter("texto", texto)
                .getResultList();
    }
}