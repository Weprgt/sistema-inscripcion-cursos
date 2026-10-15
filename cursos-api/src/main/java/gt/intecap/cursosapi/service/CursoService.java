package gt.intecap.cursosapi.service;

import gt.intecap.cursosapi.dao.CursoDaoJpa;
import gt.intecap.cursosapi.model.Curso;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Gestiona las operaciones y validaciones de los cursos.
 */
@Service
@Transactional(readOnly = true)
public class CursoService {

    private final CursoDaoJpa cursoDao;

    /**
     * Inicializa el servicio.
     * @param cursoDao acceso a los cursos
     */
    public CursoService(CursoDaoJpa cursoDao) {
        this.cursoDao = cursoDao;
    }

    /**
     * Lista los cursos.
     * @return cursos registrados
     */
    public List<Curso> listar() {
        return cursoDao.listar();
    }

    /**
     * Busca un curso.
     * @param id identificador del curso
     * @return curso encontrado
     */
    public Curso buscarPorId(Long id) {
        return cursoDao.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El curso no existe"
                ));
    }

    /**
     * Registra un curso con identificador generado por la base de datos.
     * @param curso datos del nuevo curso
     * @return curso registrado
     */
    @Transactional
    public Curso crear(Curso curso) {
        normalizarTextos(curso);
        validarCodigo(curso.getCodigo(), null);
        curso.setId(null);

        return cursoDao.guardar(curso);
    }

    /**
     * Actualiza un curso existente.
     * @param id identificador del curso
     * @param datos nuevos datos
     * @return curso actualizado
     */
    @Transactional
    public Curso actualizar(Long id, Curso datos) {
        Curso curso = buscarPorId(id);

        normalizarTextos(datos);
        validarCodigo(datos.getCodigo(), id);

        curso.setCodigo(datos.getCodigo());
        curso.setNombre(datos.getNombre());
        curso.setDescripcion(datos.getDescripcion());
        curso.setModalidad(datos.getModalidad());
        curso.setCupo(datos.getCupo());
        curso.setFechaInicio(datos.getFechaInicio());
        curso.setActivo(datos.getActivo());

        return cursoDao.guardar(curso);
    }

    private void normalizarTextos(Curso curso) {
        curso.setCodigo(curso.getCodigo().trim());
        curso.setNombre(curso.getNombre().trim());
        curso.setDescripcion(curso.getDescripcion().trim());
    }

    private void validarCodigo(String codigo, Long idExcluir) {
        if (cursoDao.existeCodigo(codigo, idExcluir)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un curso con ese código"
            );
        }
    }
}