package gt.intecap.cursosapi.service;

import gt.intecap.cursosapi.dao.ParticipanteDaoJpa;
import gt.intecap.cursosapi.model.Participante;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import gt.intecap.cursosapi.dao.InscripcionDaoJpa;

import java.util.List;
import java.util.Locale;

/**
 * Gestiona las operaciones y reglas de los participantes.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class ParticipanteService {

    private final ParticipanteDaoJpa participanteDao;
    private final InscripcionDaoJpa inscripcionDao;


    /**
     * Inicializa el servicio.
     * @param participanteDao acceso a los participantes
     */
    public ParticipanteService(
            ParticipanteDaoJpa participanteDao,
            InscripcionDaoJpa inscripcionDao
    ) {
        this.participanteDao = participanteDao;
        this.inscripcionDao = inscripcionDao;
    }

    /**
     * Lista los participantes.
     * @return participantes registrados
     */
    public List<Participante> listar() {
        return participanteDao.listar();
    }

    /**
     * Busca un participante por su identificador.
     * @param id identificador buscado
     * @return participante encontrado
     */
    public Participante buscarPorId(Long id) {
        return participanteDao.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El participante no existe"
                ));
    }

    /**
     * Registra un participante.
     * @param participante datos que se validarán y guardarán
     * @return participante registrado
     */
    @Transactional
    public Participante crear(@NotNull @Valid Participante participante) {
        normalizarTextos(participante);
        validarCorreo(participante.getCorreo(), null);
        participante.setId(null);

        return participanteDao.guardar(participante);
    }

    /**
     * Actualiza un participante existente.
     * @param id identificador del participante
     * @param datos nuevos datos que se validarán
     * @return participante actualizado
     */
    @Transactional
    public Participante actualizar(
            Long id,
            @NotNull @Valid Participante datos
    ) {
        Participante participante = buscarPorId(id);

        normalizarTextos(datos);
        validarCorreo(datos.getCorreo(), id);

        participante.setNombres(datos.getNombres());
        participante.setApellidos(datos.getApellidos());
        participante.setCorreo(datos.getCorreo());
        participante.setTelefono(datos.getTelefono());

        return participanteDao.guardar(participante);
    }
    /**
     * Elimina un participante únicamente si no tiene inscripciones.
     * @param id identificador del participante
     */
    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);

        if (inscripcionDao.existePorParticipante(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un participante con inscripciones"
            );
        }

        participanteDao.eliminarPorId(id);
    }

    private void normalizarTextos(Participante participante) {
        participante.setNombres(participante.getNombres().trim());
        participante.setApellidos(participante.getApellidos().trim());
        participante.setCorreo(
                participante.getCorreo().trim().toLowerCase(Locale.ROOT)
        );
    }

    private void validarCorreo(String correo, Long idExcluir) {
        if (participanteDao.existeCorreo(correo, idExcluir)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un participante con ese correo"
            );
        }
    }
}