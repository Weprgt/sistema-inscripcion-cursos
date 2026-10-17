package gt.intecap.cursosweb.controller;

import gt.intecap.cursosweb.client.CursoApiClient;
import gt.intecap.cursosweb.dto.CursoSolicitud;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/cursos")
public class CursoWebController {

    private final CursoApiClient cursoApiClient;

    public CursoWebController(CursoApiClient cursoApiClient) {
        this.cursoApiClient = cursoApiClient;
    }

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("cursos", cursoApiClient.listar());
        } catch (RestClientException ex) {
            model.addAttribute("cursos", List.of());
            model.addAttribute(
                    "errorListado",
                    "No se pudieron cargar los cursos. Inténtalo de nuevo."
            );
        }

        return "cursos/listado";
    }

    /**
     * Muestra el formulario de registro.
     */
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("curso", new CursoSolicitud(
                "", "", "", "", null, null, true
        ));
        return "cursos/formulario";
    }

    /**
     * Valida el formulario y solicita el registro a la API.
     */
    @PostMapping
    public String crear(
            @Valid @ModelAttribute("curso") CursoSolicitud solicitud,
            BindingResult errores,
            RedirectAttributes redirectAttributes
    ) {
        if (errores.hasErrors()) {
            return "cursos/formulario";
        }

        try {
            cursoApiClient.crear(solicitud);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 409) {
                errores.rejectValue(
                        "codigo",
                        "curso.codigo.duplicado",
                        "Ya existe un curso con ese código"
                );
            } else {
                errores.reject(
                        "api.error",
                        "La API no pudo registrar el curso. Revisa los datos "
                                + "o inténtalo de nuevo."
                );
            }
            return "cursos/formulario";
        } catch (RestClientException ex) {
            errores.reject(
                    "api.conexion",
                    "No se pudo conectar con la API. Inténtalo de nuevo."
            );
            return "cursos/formulario";
        }

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Curso registrado correctamente"
        );

        return "redirect:/cursos";
    }
    /**
     * Carga los datos del curso en el formulario de edición.
     */
    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            var curso = cursoApiClient.buscarPorId(id);

            model.addAttribute("curso", new CursoSolicitud(
                    curso.codigo(),
                    curso.nombre(),
                    curso.descripcion(),
                    curso.modalidad(),
                    curso.cupo(),
                    curso.fechaInicio(),
                    curso.activo()
            ));

            model.addAttribute("idCurso", id);
            return "cursos/formulario";

        } catch (RestClientResponseException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    ex.getStatusCode().value() == 404
                            ? "El curso solicitado no existe."
                            : "No se pudo cargar el curso para editarlo."
            );
        } catch (RestClientException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    "No se pudo conectar con la API."
            );
        }

        return "redirect:/cursos";
    }

    /**
     * Valida el formulario y envía los cambios a la API.
     */
    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("curso") CursoSolicitud solicitud,
            BindingResult errores,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("idCurso", id);

        if (errores.hasErrors()) {
            return "cursos/formulario";
        }

        try {
            cursoApiClient.actualizar(id, solicitud);

        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 409) {
                errores.reject(
                        "curso.conflicto",
                        "No se pudo actualizar: el código puede estar en uso "
                                + "o el cupo es menor que los inscritos activos."
                );
            } else if (ex.getStatusCode().value() == 404) {
                errores.reject(
                        "curso.noExiste",
                        "El curso ya no existe."
                );
            } else {
                errores.reject(
                        "api.error",
                        "La API no pudo actualizar el curso. Revisa los datos "
                                + "o inténtalo de nuevo."
                );
            }

            return "cursos/formulario";

        } catch (RestClientException ex) {
            errores.reject(
                    "api.conexion",
                    "No se pudo conectar con la API. Inténtalo de nuevo."
            );
            return "cursos/formulario";
        }

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Curso actualizado correctamente"
        );

        return "redirect:/cursos";
    }
    /**
     * Muestra los datos, cupos e inscripciones de un curso.
     */
    @GetMapping("/{id}")
    public String detalle(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            var curso = cursoApiClient.buscarPorId(id);
            var cupos = cursoApiClient.consultarCupos(id);
            var inscripciones = cursoApiClient.listarInscripciones(id);

            model.addAttribute("curso", curso);
            model.addAttribute("cupos", cupos);
            model.addAttribute("inscripciones", inscripciones);

            return "cursos/detalle";

        } catch (RestClientResponseException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    ex.getStatusCode().value() == 404
                            ? "El curso solicitado no existe."
                            : "No se pudo cargar el detalle del curso."
            );
        } catch (RestClientException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    "No se pudo conectar con la API."
            );
        }

        return "redirect:/cursos";
    }
}