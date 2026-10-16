package gt.intecap.cursosapi.controller;

import gt.intecap.cursosapi.model.Curso;
import gt.intecap.cursosapi.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import gt.intecap.cursosapi.dto.CupoCursoRespuesta;

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
     * Lista los cursos.
     * @return cursos registrados
     */
    @GetMapping
    public List<Curso> listar() {
        return cursoService.listar();
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
}