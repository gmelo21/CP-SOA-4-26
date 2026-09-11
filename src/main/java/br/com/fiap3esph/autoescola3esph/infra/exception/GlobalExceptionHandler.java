package br.com.fiap3esph.autoescola3esph.infra.exception;

import br.com.fiap3esph.autoescola3esph.domain.agenda.InstrucaoNotFoundException;
import br.com.fiap3esph.autoescola3esph.domain.agenda.ValidacaoException;
import br.com.fiap3esph.autoescola3esph.domain.aluno.AlunoNotFoundException;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.InstrutorNotFoundException;
import br.com.fiap3esph.autoescola3esph.domain.usuario.UsuarioNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarGenericNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Stream<DadosBadRequest>> tratarBadRequest(MethodArgumentNotValidException e) {
        List<FieldError> erros = e.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosBadRequest::new));
    }

    @ExceptionHandler({
            InstrutorNotFoundException.class,
            AlunoNotFoundException.class,
            UsuarioNotFoundException.class,
            InstrucaoNotFoundException.class
    })
    public ResponseEntity<DadosMessage> tratarNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosMessage(e.getMessage()));
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<DadosMessage> tratarRegraDeNegocio(ValidacaoException e) {
        return ResponseEntity.badRequest().body(new DadosMessage(e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosMessage> tratarJsonInvalido(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException erro
                && erro.getTargetType() != null
                && erro.getTargetType().isEnum()
                && !erro.getPath().isEmpty()) {
            String campo = erro.getPath().getLast().getPropertyName();
            String opcoes = Arrays.stream(erro.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new DadosMessage(
                    "Valor inválido para o campo '" + campo + "'. Opções aceitas: " + opcoes));
        }
        return ResponseEntity.badRequest().body(new DadosMessage(e.getMessage()));
    }

    private record DadosBadRequest(String campo, String mensagem) {
        public DadosBadRequest(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

    private record DadosMessage(String mensagem) {
    }
}
