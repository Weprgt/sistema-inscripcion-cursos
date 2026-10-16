package gt.intecap.cursosapi.service;

import gt.intecap.cursosapi.dao.CursoDaoJpa;
import gt.intecap.cursosapi.dao.InscripcionDaoJpa;
import gt.intecap.cursosapi.dao.ParticipanteDaoJpa;
import gt.intecap.cursosapi.dto.InscripcionRespuesta;
import gt.intecap.cursosapi.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Gestiona las inscripciones y sus reglas de negocio.
 */
@Service
@Transactional(readOnly = true)
public class InscripcionService {

    private final InscripcionDaoJpa inscripcionDao;
    private final CursoDaoJpa cursoDao;
    private final ParticipanteDaoJpa participanteDao;

    /**
     * Inicializa el servicio con sus dependencias.
     * @param inscripcionDao acceso a inscripciones
     * @param cursoDao acceso a cursos
     * @param participanteDao acceso a participantes
     */
    public InscripcionService(
            InscripcionDaoJpa inscripcionDao,
            CursoDaoJpa cursoDao,
            ParticipanteDaoJpa participanteDao
    ) {
        this.inscripcionDao = inscripcionDao;
        this.cursoDao = cursoDao;
        this.participanteDao = participanteDao;
    }

    /**
     * Lista las inscripciones.
     * @return inscripciones registradas
     */
    public List<InscripcionRespuesta> listar() {
        return inscripcionDao.listar().stream()
                .map(this::convertir)
                .toList();
    }

    /**
     * Consulta una inscripción.
     * @param id identificador de inscripción
     * @return inscripción encontrada
     */
    public InscripcionRespuesta buscarPorId(Long id) {
        return convertir(obtenerInscripcion(id));
    }

    /**
     * Inscribe un participante o reactiva su inscripción cancelada.
     * @param idCurso identificador del curso
     * @param idParticipante identificador del participante
     * @return inscripción creada o reactivada
     */
    @Transactional
    public InscripcionRespuesta inscribir(
            Long idCurso,
            Long idParticipante
    ) {
        if (idCurso == null || idParticipante == null
                || idCurso <= 0 || idParticipante <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los identificadores deben ser positivos"
            );
        }

        Curso curso = cursoDao.buscarPorIdConBloqueo(idCurso)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El curso no existe"
                ));

        Participante participante =
                participanteDao.buscarPorId(idParticipante)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "El participante no existe"
                        ));

        var existente = inscripcionDao.buscarPorCursoYParticipante(
                idCurso, idParticipante
        );

        if (existente.isPresent()
                && existente.get().getEstado() == EstadoInscripcion.ACTIVA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El participante ya tiene una inscripción activa"
            );
        }

        validarDisponibilidad(curso);

        Inscripcion inscripcion;

        if (existente.isPresent()) {
            inscripcion = existente.get();
            inscripcion.reactivar();
        } else {
            inscripcion = new Inscripcion();
            inscripcion.setCurso(curso);
            inscripcion.setParticipante(participante);
            inscripcion.setFechaInscripcion(LocalDate.now());
            inscripcion.setEstado(EstadoInscripcion.ACTIVA);
        }

        return convertir(inscripcionDao.guardar(inscripcion));
    }

    /**
     * Cancela una inscripción y libera su cupo.
     * @param id identificador de inscripción
     * @return inscripción cancelada
     */
    @Transactional
    public InscripcionRespuesta cancelar(Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);

        // Serializa los cambios de cupo del mismo curso.
        cursoDao.buscarPorIdConBloqueo(inscripcion.getCurso().getId());

        inscripcion.cancelar();
        return convertir(inscripcionDao.guardar(inscripcion));
    }

    /**
     * Lista las inscripciones de un curso.
     * @param idCurso identificador del curso
     * @return inscripciones activas y canceladas
     */
    public List<InscripcionRespuesta> listarPorCurso(Long idCurso) {
        if (cursoDao.buscarPorId(idCurso).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El curso no existe"
            );
        }

        return inscripcionDao.listarPorCurso(idCurso).stream()
                .map(this::convertir)
                .toList();
    }

    private Inscripcion obtenerInscripcion(Long id) {
        return inscripcionDao.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La inscripción no existe"
                ));
    }

    private void validarDisponibilidad(Curso curso) {
        if (!Boolean.TRUE.equals(curso.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El curso está inactivo"
            );
        }

        long ocupados = inscripcionDao.contarActivasPorCurso(curso.getId());

        if (ocupados >= curso.getCupo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El curso no tiene cupos disponibles"
            );
        }
    }

    private InscripcionRespuesta convertir(Inscripcion inscripcion) {
        return new InscripcionRespuesta(
                inscripcion.getId(),
                inscripcion.getCurso().getId(),
                inscripcion.getCurso().getNombre(),
                inscripcion.getParticipante().getId(),
                inscripcion.getParticipante().getNombreCompleto(),
                inscripcion.getFechaInscripcion(),
                inscripcion.getEstado()
        );
    }
}