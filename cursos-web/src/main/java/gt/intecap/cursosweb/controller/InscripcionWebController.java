package gt.intecap.cursosweb.controller;

import gt.intecap.cursosweb.client.CursoApiClient;
import gt.intecap.cursosweb.client.InscripcionApiClient;
import gt.intecap.cursosweb.client.ParticipanteApiClient;
import gt.intecap.cursosweb.dto.InscripcionSolicitud;
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
@RequestMapping("/inscripciones")
public class InscripcionWebController {

    private final InscripcionApiClient inscripcionApiClient;
    private final CursoApiClient cursoApiClient;
    private final ParticipanteApiClient participanteApiClient;

    public InscripcionWebController(
            InscripcionApiClient inscripcionApiClient,
            CursoApiClient cursoApiClient,
            ParticipanteApiClient participanteApiClient
    ) {
        this.inscripcionApiClient = inscripcionApiClient;
        this.cursoApiClient = cursoApiClient;
        this.participanteApiClient = participanteApiClient;
    }

    @GetMapping
    public String listar(
            @RequestParam(name = "texto", defaultValue = "") String texto,
            Model model
    ) {
        model.addAttribute("texto", texto);

        try {
            model.addAttribute(
                    "inscripciones",
                    inscripcionApiClient.buscar(texto)
            );
        } catch (RestClientException ex) {
            model.addAttribute("inscripciones", List.of());
            model.addAttribute(
                    "errorListado",
                    "No se pudieron cargar las inscripciones."
            );
        }

        return "inscripciones/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute(
                "inscripcion",
                new InscripcionSolicitud(null, null)
        );

        cargarOpciones(model);
        return "inscripciones/formulario";
    }

    @PostMapping
    public String registrar(
            @Valid @ModelAttribute("inscripcion")
            InscripcionSolicitud solicitud,
            BindingResult errores,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (!errores.hasErrors()) {
            try {
                inscripcionApiClient.inscribir(solicitud);

                redirectAttributes.addFlashAttribute(
                        "mensajeExito",
                        "Inscripción registrada o reactivada correctamente"
                );

                return "redirect:/inscripciones";

            } catch (RestClientResponseException ex) {
                String mensaje = switch (ex.getStatusCode().value()) {
                    case 409 -> "No se pudo inscribir: comprueba que el "
                            + "curso esté activo, tenga cupo y que el "
                            + "participante no esté inscrito.";
                    case 404 -> "El curso o participante ya no existe.";
                    default -> "La API no pudo registrar la inscripción.";
                };

                errores.reject("inscripcion.error", mensaje);

            } catch (RestClientException ex) {
                errores.reject(
                        "api.conexion",
                        "No se pudo conectar con la API."
                );
            }
        }

        cargarOpciones(model);
        return "inscripciones/formulario";
    }

    /**
     * Carga los cursos activos y los participantes del formulario.
     */
    private void cargarOpciones(Model model) {
        try {
            var cursos = cursoApiClient.listar().stream()
                    .filter(curso -> Boolean.TRUE.equals(curso.activo()))
                    .toList();

            var participantes = participanteApiClient.listar();

            model.addAttribute("cursos", cursos);
            model.addAttribute("participantes", participantes);
            model.addAttribute("opcionesDisponibles", true);

        } catch (RestClientException ex) {
            model.addAttribute("cursos", List.of());
            model.addAttribute("participantes", List.of());
            model.addAttribute("opcionesDisponibles", false);
            model.addAttribute(
                    "errorOpciones",
                    "No se pudieron cargar los cursos y participantes. "
                            + "Inténtalo de nuevo."
            );
        }
    }
    @PostMapping("/{id}/cancelar")
    public String cancelar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inscripcionApiClient.cancelar(id);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Inscripción cancelada correctamente"
            );

        } catch (RestClientResponseException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    ex.getStatusCode().value() == 404
                            ? "La inscripción ya no existe."
                            : "No se pudo cancelar la inscripción."
            );

        } catch (RestClientException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    "No se pudo conectar con la API."
            );
        }

        return "redirect:/inscripciones";
    }
    @PostMapping("/reactivar")
    public String reactivar(
            @Valid @ModelAttribute InscripcionSolicitud solicitud,
            BindingResult errores,
            RedirectAttributes redirectAttributes
    ) {
        if (errores.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    "Selecciona un curso y un participante válidos."
            );
            return "redirect:/inscripciones";
        }

        try {
            inscripcionApiClient.inscribir(solicitud);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Inscripción reactivada correctamente"
            );

        } catch (RestClientResponseException ex) {
            String mensaje = switch (ex.getStatusCode().value()) {
                case 409 -> "No se pudo reactivar: el curso puede estar "
                        + "inactivo, sin cupo o la inscripción ya está activa.";
                case 404 -> "El curso o participante ya no existe.";
                default -> "No se pudo reactivar la inscripción.";
            };

            redirectAttributes.addFlashAttribute("errorListado", mensaje);

        } catch (RestClientException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    "No se pudo conectar con la API."
            );
        }

        return "redirect:/inscripciones";
    }
}