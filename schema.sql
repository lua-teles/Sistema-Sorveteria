DROP TABLE IF EXISTS item_adicional, item_pedido, pedido, adicional, produto, ingrediente, funcionario CASCADE;

-- Tabela de ingredientes/ estoque

Create Table if not exists ingrediente(
                                          id			Serial Primary 	Key,
                                          nome 		Varchar(100)	not NULL,
    quantidade 	Integer		not NULL DEFAULT 0
    );

--Tabela de produtos(sorvetes base)
Create Table if not exists produto(
                                      id	Serial Primary  Key,
                                      nome Varchar(100)	not Null,
    tipo Varchar(100)	not NULL,
    preco Numeric(10, 2) not Null
    );

--tabela de extras/ adicionais
Create table if not exists adicional(
                                        id 		serial primary key,
                                        nome	varchar(100)	not null,
    preco	numeric(10, 2)	not null
    );

--tabela de funcionários
create table if not exists funcionario(
                                          id serial primary key,
                                          nome varchar(100) not null,
    cargo varchar(50) not null
    );

--tabela de pedidos
create table if not exists pedido(
                                     id		serial primary key,
                                     status 	varchar(30) not null default 'ABERTO',
    pagamento varchar(20),
    total	numeric(10,2),
    criado_em timestamp	not null default now()
    );

create table if not exists item_pedido(
                                          id		serial primary key,
                                          pedido_id integer	not null references pedido(id),
    produto_id integer	not null references produto(id),
    quantidade integer 	not null default 1,
    preco_unit numeric(10, 2) not null
    );

--tabela de extras
create table if not exists item_adicional (
                                              id		serial primary key,
                                              item_pedido_id integer not null references item_pedido(id),
    adicional_id		integer not null references adicional(id)
    );


-- Ingredientes do estoque
INSERT INTO ingrediente (nome, quantidade) VALUES
                                               ('Chocolate', 100),
                                               ('Morango',   100),
                                               ('Baunilha',  100),
                                               ('Granulado',  50),
                                               ('Calda',      50),
                                               ('Kit-Kat',    30),
                                               ('Frutas',     40);

-- Produtos base
INSERT INTO produto (nome, tipo, preco) VALUES
                                            ('Sorvete Casquinha',    'CASQUINHA',    5.00),
                                            ('Sorvete Copo Pequeno', 'COPO_PEQUENO', 8.00),
                                            ('Sorvete Copo Grande',  'COPO_GRANDE',  12.00);

-- Extras disponíveis
INSERT INTO adicional (nome, preco) VALUES
                                        ('Calda de Chocolate', 2.00),
                                        ('Granulado',          1.00),
                                        ('Kit-Kat',            3.00),
                                        ('Frutas',             2.50);

-- Funcionários
INSERT INTO funcionario (nome, cargo) VALUES
                                          ('Maria', 'Atendente'),
                                          ('João',  'Gerente');

ALTER TABLE pedido ADD COLUMN IF NOT EXISTS descricao TEXT DEFAULT '';