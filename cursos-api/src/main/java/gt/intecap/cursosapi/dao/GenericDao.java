package gt.intecap.cursosapi.dao;

import java.util.List;
import java.util.Optional;

/**
 * Define las operaciones básicas de acceso a datos.
 *
 * @param <T> tipo de entidad
 * @param <ID> tipo del identificador
 */
public interface GenericDao<T, ID> {

    /**
     * Lista las entidades.
     * @return entidades encontradas
     */
    List<T> listar();

    /**
     * Busca una entidad por su identificador.
     * @param id identificador buscado
     * @return entidad encontrada o un Optional vacío
     */
    Optional<T> buscarPorId(ID id);

    /**
     * Guarda una entidad nueva o actualiza una existente.
     * @param entidad entidad que se guardará
     * @return entidad guardada
     */
    T guardar(T entidad);

    /**
     * Elimina una entidad por su identificador.
     * @param id identificador de la entidad
     */
    void eliminarPorId(ID id);
}