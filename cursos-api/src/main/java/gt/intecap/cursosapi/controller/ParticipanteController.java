package gt.intecap.cursosapi.controller;

import gt.intecap.cursosapi.model.Participante;
import gt.intecap.cursosapi.service.ParticipanteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expone las operaciones REST de participantes.
 */
@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    /**
     * Inicializa el controlador.
     * @param participanteService servicio de participantes
     */
    public ParticipanteController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    /**
     * Lista los participantes.
     * @return participantes registrados
     */
    @GetMapping
    public List<Participante> listar() {
        return participanteService.listar();
    }

    /**
     * Consulta un participante.
     * @param id identificador buscado
     * @return participante encontrado
     */
    @GetMapping("/{id}")
    public Participante buscarPorId(@PathVariable Long id) {
        return participanteService.buscarPorId(id);
    }

    /**
     * Registra un participante.
     * @param participante datos recibidos
     * @return participante registrado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Participante crear(@Valid @RequestBody Participante participante) {
        return participanteService.crear(participante);
    }

    /**
     * Actualiza un participante.
     * @param id identificador del participante
     * @param participante nuevos datos
     * @return participante actualizado
     */
    @PutMapping("/{id}")
    public Participante actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Participante participante
    ) {
        return participanteService.actualizar(id, participante);
    }
}