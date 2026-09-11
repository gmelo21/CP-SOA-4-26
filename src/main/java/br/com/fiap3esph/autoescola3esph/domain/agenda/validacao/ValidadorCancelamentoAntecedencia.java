package br.com.fiap3esph.autoescola3esph.domain.agenda.validacao;

import br.com.fiap3esph.autoescola3esph.domain.agenda.DadosCancelamento;
import br.com.fiap3esph.autoescola3esph.domain.agenda.InstrucaoRepository;
import br.com.fiap3esph.autoescola3esph.domain.agenda.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ValidadorCancelamentoAntecedencia implements ValidadorCancelamento {
    private final InstrucaoRepository repository;

    @Override
    public void validar(DadosCancelamento dados) {
        LocalDateTime dataInstrucao = repository.getReferenceById(dados.idInstrucao()).getDataHora();
        LocalDateTime agora = LocalDateTime.now();

        long antecedencia = Duration.between(agora, dataInstrucao).toHours();

        if (antecedencia < 24) {
            throw new ValidacaoException("A instrução só pode ser cancelada com antecedência mínima de 24 horas!");
        }
    }
}
