package gt.intecap.cursosapi.controller;

import gt.intecap.cursosapi.model.Curso;
import gt.intecap.cursosapi.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import gt.intecap.cursosapi.dto.CupoCursoRespuesta;
import gt.intecap.cursosapi.model.Modalidad;

import java.util.List;

/**
 * Expone las operaciones REST de cursos.
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    /**
     * Inicializa el controlador.
     * @param cursoService servicio de cursos
     */
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    /**
     * Lista y busca cursos con filtros opcionales por estado y modalidad.
     */
    @GetMapping
    public List<Curso> listar(
            @RequestParam(name = "texto", defaultValue = "") String texto,
            @RequestParam(name = "activo", required = false) Boolean activo,
            @RequestParam(name = "modalidad", required = false) Modalidad modalidad
    ) {
        return cursoService.buscar(texto, activo, modalidad);
    }

    /**
     * Consulta un curso por su identificador.
     * @param id identificador del curso
     * @return curso encontrado
     */
    @GetMapping("/{id}")
    public Curso buscarPorId(@PathVariable Long id) {
        return cursoService.buscarPorId(id);
    }

    /**
     * Registra un curso.
     * @param curso datos que se validarán y guardarán
     * @return curso registrado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Curso crear(@Valid @RequestBody Curso curso) {
        return cursoService.crear(curso);
    }

    /**
     * Actualiza un curso.
     * @param id identificador del curso
     * @param curso nuevos datos
     * @return curso actualizado
     */
    @PutMapping("/{id}")
    public Curso actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Curso curso
    ) {
        return cursoService.actualizar(id, curso);
    }

    /**
     * Consulta los cupos de un curso.
     * @param id identificador del curso
     * @return capacidad y ocupación actual
     */
    @GetMapping("/{id}/cupos")
    public CupoCursoRespuesta consultarCupos(@PathVariable Long id) {
        return cursoService.consultarCupos(id);
    }
    /**
     * Elimina un curso sin inscripciones.
     * @param id identificador del curso
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
    }
}