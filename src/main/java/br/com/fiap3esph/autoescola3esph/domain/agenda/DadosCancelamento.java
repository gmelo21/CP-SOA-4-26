package br.com.fiap3esph.autoescola3esph.domain.agenda;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record DadosCancelamento(
        @NotNull
        @JsonProperty("id_instrucao")
        Long idInstrucao,

        @NotNull
        MotivoCancelamento motivo) {
}
