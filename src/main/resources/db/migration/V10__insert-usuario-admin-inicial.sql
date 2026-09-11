-- Cria o primeiro administrador (login: admin / senha: admin123) apenas se o banco ainda não tiver nenhum ADMIN
insert into usuarios (login, senha, perfil)
select 'admin', '$2a$10$jzYQoE./w3/97YPTZAUGWeUHpaHSrYNpnY85R93QpO3WCXZcHqODq', 'ADMIN'
from dual
where not exists (
    select 1 from usuarios where login = 'admin' or perfil = 'ADMIN'
);
