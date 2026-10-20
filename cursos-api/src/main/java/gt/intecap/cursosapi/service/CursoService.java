package gt.intecap.cursosapi.service;

import gt.intecap.cursosapi.dao.CursoDaoJpa;
import gt.intecap.cursosapi.model.Curso;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import gt.intecap.cursosapi.dao.InscripcionDaoJpa;
import gt.intecap.cursosapi.dto.CupoCursoRespuesta;
import java.util.List;
import java.util.Locale;

/**
 * Gestiona las operaciones y validaciones de los cursos.
 */
@Service
@Transactional(readOnly = true)
public class CursoService {

    private final CursoDaoJpa cursoDao;
    private final InscripcionDaoJpa inscripcionDao;

    /**
     * Inicializa el servicio.
     * @param cursoDao acceso a los cursos
     */
    public CursoService(
            CursoDaoJpa cursoDao,
            InscripcionDaoJpa inscripcionDao
    ) {
        this.cursoDao = cursoDao;
        this.inscripcionDao = inscripcionDao;
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
        Curso curso = cursoDao.buscarPorIdConBloqueo(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El curso no existe"
                ));

        normalizarTextos(datos);
        validarCodigo(datos.getCodigo(), id);

        long inscritosActivos =
                inscripcionDao.contarActivasPorCurso(id);

        if (datos.getCupo() < inscritosActivos) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El cupo no puede ser menor que la cantidad "
                            + "de inscripciones activas"
            );
        }

        curso.setCodigo(datos.getCodigo());
        curso.setNombre(datos.getNombre());
        curso.setDescripcion(datos.getDescripcion());
        curso.setModalidad(datos.getModalidad());
        curso.setCupo(datos.getCupo());
        curso.setFechaInicio(datos.getFechaInicio());
        curso.setActivo(datos.getActivo());

        return cursoDao.guardar(curso);
    }

    /**
     * Consulta la capacidad y ocupación de un curso.
     * @param id identificador del curso
     * @return resumen de cupos
     */
    public CupoCursoRespuesta consultarCupos(Long id) {
        Curso curso = buscarPorId(id);

        long ocupados = inscripcionDao.contarActivasPorCurso(id);
        long disponibles = curso.getCupo() - ocupados;

        return new CupoCursoRespuesta(
                curso.getId(),
                curso.getNombre(),
                Boolean.TRUE.equals(curso.getActivo()),
                curso.getCupo(),
                ocupados,
                disponibles
        );
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
    /**
     * Elimina un curso únicamente si no tiene inscripciones.
     * @param id identificador del curso
     */
    @Transactional
    public void eliminar(Long id) {
        cursoDao.buscarPorIdConBloqueo(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El curso no existe"
                ));

        if (inscripcionDao.existePorCurso(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un curso con inscripciones"
            );
        }

        cursoDao.eliminarPorId(id);
    }

    /**
     * Busca cursos por texto y estado, con activos primero.
     */
    public List<Curso> buscar(String texto, Boolean activo) {
        String textoNormalizado = texto == null
                ? ""
                : texto.trim().toLowerCase(Locale.ROOT);

        return cursoDao.buscar(textoNormalizado, activo);
    }
}