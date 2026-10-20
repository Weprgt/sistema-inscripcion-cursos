package gt.intecap.cursosweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Muestra la pantalla de inicio del sistema.
 */
@Controller
public class InicioWebController {

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }
}