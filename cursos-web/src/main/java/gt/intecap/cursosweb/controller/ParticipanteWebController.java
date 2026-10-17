package gt.intecap.cursosweb.controller;

import gt.intecap.cursosweb.client.ParticipanteApiClient;
import gt.intecap.cursosweb.dto.ParticipanteSolicitud;
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
@RequestMapping("/participantes")
public class ParticipanteWebController {

    private final ParticipanteApiClient participanteApiClient;

    public ParticipanteWebController(
            ParticipanteApiClient participanteApiClient
    ) {
        this.participanteApiClient = participanteApiClient;
    }

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute(
                    "participantes",
                    participanteApiClient.listar()
            );
        } catch (RestClientException ex) {
            model.addAttribute("participantes", List.of());
            model.addAttribute(
                    "errorListado",
                    "No se pudieron cargar los participantes. "
                            + "Inténtalo de nuevo."
            );
        }

        return "participantes/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("participante", new ParticipanteSolicitud(
                "", "", "", ""
        ));

        return "participantes/formulario";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("participante")
            ParticipanteSolicitud solicitud,
            BindingResult errores,
            RedirectAttributes redirectAttributes
    ) {
        if (errores.hasErrors()) {
            return "participantes/formulario";
        }

        try {
            participanteApiClient.crear(solicitud);

        } catch (RestClientResponseException ex) {
            registrarErrorApi(ex, errores);
            return "participantes/formulario";

        } catch (RestClientException ex) {
            errores.reject(
                    "api.conexion",
                    "No se pudo conectar con la API. Inténtalo de nuevo."
            );
            return "participantes/formulario";
        }

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Participante registrado correctamente"
        );

        return "redirect:/participantes";
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            var participante = participanteApiClient.buscarPorId(id);

            model.addAttribute(
                    "participante",
                    new ParticipanteSolicitud(
                            participante.nombres(),
                            participante.apellidos(),
                            participante.correo(),
                            participante.telefono()
                    )
            );

            model.addAttribute("idParticipante", id);
            return "participantes/formulario";

        } catch (RestClientResponseException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    ex.getStatusCode().value() == 404
                            ? "El participante solicitado no existe."
                            : "No se pudo cargar el participante."
            );

        } catch (RestClientException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    "No se pudo conectar con la API."
            );
        }

        return "redirect:/participantes";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("participante")
            ParticipanteSolicitud solicitud,
            BindingResult errores,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("idParticipante", id);

        if (errores.hasErrors()) {
            return "participantes/formulario";
        }

        try {
            participanteApiClient.actualizar(id, solicitud);

        } catch (RestClientResponseException ex) {
            registrarErrorApi(ex, errores);
            return "participantes/formulario";

        } catch (RestClientException ex) {
            errores.reject(
                    "api.conexion",
                    "No se pudo conectar con la API. Inténtalo de nuevo."
            );
            return "participantes/formulario";
        }

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Participante actualizado correctamente"
        );

        return "redirect:/participantes";
    }

    private void registrarErrorApi(
            RestClientResponseException ex,
            BindingResult errores
    ) {
        switch (ex.getStatusCode().value()) {
            case 409 -> errores.rejectValue(
                    "correo",
                    "participante.correo.duplicado",
                    "Ya existe un participante con ese correo"
            );

            case 404 -> errores.reject(
                    "participante.noExiste",
                    "El participante ya no existe."
            );

            default -> errores.reject(
                    "api.error",
                    "La API no pudo guardar el participante. "
                            + "Revisa los datos o inténtalo de nuevo."
            );
        }
    }
    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            participanteApiClient.eliminar(id);

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "Participante eliminado correctamente"
            );

        } catch (RestClientResponseException ex) {
            String mensaje = switch (ex.getStatusCode().value()) {
                case 404 -> "El participante ya no existe.";
                case 409 -> "No se puede eliminar un participante "
                        + "con inscripciones, aunque estén canceladas.";
                default -> "No se pudo eliminar el participante.";
            };

            redirectAttributes.addFlashAttribute("errorListado", mensaje);

        } catch (RestClientException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorListado",
                    "No se pudo conectar con la API."
            );
        }

        return "redirect:/participantes";
    }
}