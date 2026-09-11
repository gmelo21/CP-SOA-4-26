package br.com.fiap3esph.autoescola3esph.domain.usuario;

public record DadosDetalhamentoUsuario(
        Long id,
        String login,
        Role perfil) {
    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getPerfil()
        );
    }
}
