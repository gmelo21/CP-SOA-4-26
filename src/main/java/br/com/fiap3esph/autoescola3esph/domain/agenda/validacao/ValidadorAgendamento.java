package br.com.fiap3esph.autoescola3esph.domain.agenda.validacao;

import br.com.fiap3esph.autoescola3esph.domain.agenda.DadosAgendamento;

public interface ValidadorAgendamento {
    void validar(DadosAgendamento dados);
}