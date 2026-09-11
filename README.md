# Auto Escola 3ESPH — API ReST

Checkpoint 4 da disciplina **SOA e WebServices** (Prof. Carlos Eduardo Machado de Oliveira).

API ReST em Spring Boot para gestão de uma auto-escola: instrutores, alunos, usuários, agendamento e cancelamento de instruções, com autenticação via token JWT.

## Integrantes

| Nome | RM |
|------|----|
| Alice Santos Bulhões | RM554499 |
| Eduardo Oliveira Cardoso Madid | RM556349 |
| Nicolas Haubricht Hainfellner | RM556259 |
| Guilherme da Cunha Melo | RM555310 |

## Tecnologias

- Java 21 · Spring Boot 4 (Web MVC, Data JPA, Validation, Security)
- MySQL + Flyway (migrações em `src/main/resources/db/migration`)
- JWT (`com.auth0:java-jwt`) · senhas criptografadas com BCrypt
- Lombok

## Como executar

1. Tenha um MySQL rodando em `localhost:3306`. O banco `autoescola3esph` é criado automaticamente na primeira
   execução (`createDatabaseIfNotExist=true`) e as tabelas são criadas pelo Flyway.
2. Por padrão a API conecta com o usuário `root` e a senha `fiap`. Se o seu MySQL usa outros, defina as variáveis de
   ambiente `DB_USERNAME` e `DB_PASSWORD` (no IntelliJ: Run → Edit Configurations → Environment variables), sem
   precisar alterar o código.
3. Execute a classe `AutoEscola3EsphApplication` ou `./mvnw spring-boot:run`.
4. A API sobe em `http://localhost:8085`.

### Primeiro administrador

Apenas administradores podem cadastrar usuários, então o primeiro `ADMIN` precisa ser definido direto no banco:

```sql
update usuarios set perfil = 'ADMIN' where login = 'seu_login';
```

Se a tabela estiver vazia, gere o hash BCrypt da senha com a classe `temp/GerarSenhaHash` e insira:

```sql
insert into usuarios (login, senha, perfil) values ('admin', '<hash gerado>', 'ADMIN');
```

## Autenticação

`POST /login` com `{"login": "...", "senha": "..."}` devolve `{"tokenJWT": "..."}`.
Nas demais requisições envie o header `Authorization: Bearer <tokenJWT>` (validade de 30 minutos).

## Endpoints

| Método | Rota | Perfil | Descrição |
|--------|------|--------|-----------|
| POST | `/login` | público | Gera o token JWT |
| POST | `/instrutores` | ADMIN | Cadastra instrutor |
| GET | `/instrutores` | ADMIN, USER | Lista instrutores ativos (10 por página, por nome) |
| GET | `/instrutores/{id}` | ADMIN | Detalha instrutor |
| PUT | `/instrutores` | ADMIN | Atualiza nome, telefone e endereço |
| DELETE | `/instrutores/{id}` | ADMIN | Inativa instrutor |
| POST | `/alunos` | ADMIN | Cadastra aluno |
| GET | `/alunos` | ADMIN, USER | Lista alunos ativos (10 por página, por nome) |
| GET | `/alunos/{id}` | ADMIN | Detalha aluno |
| PUT | `/alunos` | ADMIN | Atualiza nome, telefone e endereço |
| DELETE | `/alunos/{id}` | ADMIN | Inativa aluno |
| POST | `/usuarios` | ADMIN | Cadastra usuário (senha salva com BCrypt) |
| GET | `/usuarios` | ADMIN | Lista usuários |
| GET | `/usuarios/{id}` | ADMIN | Detalha usuário |
| PUT | `/usuarios` | ADMIN | Atualiza o perfil (`USER`/`ADMIN`) do usuário |
| DELETE | `/usuarios/{id}` | ADMIN | Exclui usuário |
| PUT | `/usuarios/senha` | qualquer usuário logado | Altera a própria senha |
| POST | `/instrucoes` | qualquer usuário logado | Agenda instrução |
| DELETE | `/instrucoes` | qualquer usuário logado | Cancela instrução |

### Exemplos de corpo das requisições

Cadastro de aluno — `POST /alunos`
```json
{
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "telefone": "11999998888",
  "cpf": "12345678901",
  "endereco": {
    "logradouro": "Av. Paulista",
    "numero": "1000",
    "complemento": "apto 12",
    "bairro": "Bela Vista",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01310-100"
  }
}
```

Cadastro de usuário — `POST /usuarios`
```json
{ "login": "joao", "senha": "123456", "perfil": "USER" }
```

Atualização de perfil — `PUT /usuarios`
```json
{ "id": 2, "perfil": "ADMIN" }
```

Alteração da própria senha — `PUT /usuarios/senha`
```json
{ "senha_atual": "123456", "nova_senha": "novaSenha@2026" }
```

Agendamento — `POST /instrucoes` (`id_instrutor` é opcional; sem ele, informe `especialidade`)
```json
{ "id_aluno": 1, "id_instrutor": 1, "data_hora": "15/09/2026 - 10:00" }
```

Cancelamento — `DELETE /instrucoes`
```json
{ "id_instrucao": 1, "motivo": "ALUNO_DESISTIU" }
```
Motivos aceitos: `ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU`, `OUTROS`.

## Regras de negócio

**Agendamento:** segunda a sábado, das 06:00 às 21:00; duração fixa de 1 hora; antecedência mínima de 30 minutos;
aluno e instrutor precisam estar ativos; no máximo duas instruções por dia para o mesmo aluno; o instrutor não pode ter
outra instrução no mesmo horário; se o instrutor não for informado, um instrutor disponível da especialidade é sorteado.

**Cancelamento:** o motivo é obrigatório (aluno desistiu, instrutor cancelou ou outros) e a instrução só pode ser
cancelada com antecedência mínima de 24 horas. A instrução cancelada não é apagada: o motivo fica registrado e o horário
volta a ficar livre para novos agendamentos.
