package br.com.fiap3esph.autoescola3esph.domain.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record DadosAlteracaoSenha(
        @NotBlank
        @JsonProperty("senha_atual")
        String senhaAtual,

        @NotBlank
        @JsonProperty("nova_senha")
        String novaSenha) {
}
