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

@Controller
@RequestMapping("/cursos")
public class CursoWebController {

    private final CursoApiClient cursoApiClient;

    public CursoWebController(CursoApiClient cursoApiClient) {
        this.cursoApiClient = cursoApiClient;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cursos", cursoApiClient.listar());
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
}