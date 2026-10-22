package gt.intecap.cursosapi.dao;

import gt.intecap.cursosapi.model.Curso;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import gt.intecap.cursosapi.model.Modalidad;

/**
 * Implementa el acceso a los cursos mediante JPA.
 */
@Repository
@Transactional(readOnly = true)
public class CursoDaoJpa implements GenericDao<Curso, Long> {

    private final EntityManager entityManager;

    /**
     * Recibe el administrador de entidades proporcionado por Spring.
     * @param entityManager administrador de entidades JPA
     */
    public CursoDaoJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /** {@inheritDoc} */
    @Override
    public List<Curso> listar() {
        return entityManager.createQuery(
                "SELECT c FROM Curso c ORDER BY c.id", Curso.class
        ).getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Curso> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(Curso.class, id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        if (curso.getId() == null) {
            entityManager.persist(curso);
            return curso;
        }

        return entityManager.merge(curso);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void eliminarPorId(Long id) {
        Curso curso = entityManager.find(Curso.class, id);

        if (curso != null) {
            entityManager.remove(curso);
        }
    }

    /**
     * Comprueba si otro curso utiliza el código indicado.
     * @param codigo código que se comprobará
     * @param idExcluir identificador que se excluirá o null al registrar
     * @return true si otro curso utiliza el código
     */
    public boolean existeCodigo(String codigo, Long idExcluir) {
        String jpql = """
                SELECT COUNT(c) FROM Curso c
                WHERE LOWER(c.codigo) = LOWER(:codigo)
                """;

        if (idExcluir != null) {
            jpql += " AND c.id <> :idExcluir";
        }

        var consulta = entityManager.createQuery(jpql, Long.class)
                .setParameter("codigo", codigo);

        if (idExcluir != null) {
            consulta.setParameter("idExcluir", idExcluir);
        }

        return consulta.getSingleResult() > 0;
    }
    /**
     * Busca y bloquea el curso durante una operación de inscripción.
     * @param id identificador del curso
     * @return curso encontrado o un Optional vacío
     */
    @Transactional
    public Optional<Curso> buscarPorIdConBloqueo(Long id) {
        return Optional.ofNullable(entityManager.find(
                Curso.class,
                id,
                jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
        ));
    }
    public List<Curso> buscar(String texto, Boolean activo) {
        return buscar(texto, activo, null);
    }

    /**
     * Busca cursos por código o nombre, estado y modalidad.
     * Los filtros de estado y modalidad son opcionales.
     */
    public List<Curso> buscar(
            String texto,
            Boolean activo,
            Modalidad modalidad
    ) {
        String jpql = """
            SELECT c FROM Curso c
            WHERE (
                LOCATE(:texto, LOWER(c.codigo)) > 0
                OR LOCATE(:texto, LOWER(c.nombre)) > 0
            )
            """;

        if (activo != null) {
            jpql += " AND c.activo = :activo";
        }

        if (modalidad != null) {
            jpql += " AND c.modalidad = :modalidad";
        }

        jpql += """
             ORDER BY
                 CASE WHEN c.activo = true THEN 0 ELSE 1 END,
                 LOWER(c.nombre),
                 c.id
            """;

        var consulta = entityManager.createQuery(jpql, Curso.class)
                .setParameter("texto", texto);

        if (activo != null) {
            consulta.setParameter("activo", activo);
        }

        if (modalidad != null) {
            consulta.setParameter("modalidad", modalidad);
        }

        return consulta.getResultList();
    }
}