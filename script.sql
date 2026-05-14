DROP TABLE IF EXISTS venta    CASCADE;
DROP TABLE IF EXISTS vehiculo CASCADE;
DROP TABLE IF EXISTS cliente  CASCADE;
DROP TABLE IF EXISTS marca    CASCADE;


CREATE TABLE marca (
    id          SERIAL       PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    pais_origen VARCHAR(50)
);


CREATE TABLE vehiculo (
    id_marca        INTEGER         NOT NULL,
    num_bastidor    CHAR(17)        NOT NULL,
    matricula       VARCHAR(10)     UNIQUE,
    modelo          VARCHAR(100)    NOT NULL,
    anio            SMALLINT        NOT NULL,
    precio          NUMERIC(10,2)   NOT NULL,
    km              INTEGER         NOT NULL DEFAULT 0,
    color           VARCHAR(30),
    combustible     VARCHAR(10)     NOT NULL,
    estado          VARCHAR(10)     NOT NULL DEFAULT 'EN_VENTA',
    descripcion     TEXT,
    CONSTRAINT pk_vehiculo
        PRIMARY KEY (id_marca, num_bastidor),
    CONSTRAINT fk_vehiculo_marca
        FOREIGN KEY (id_marca) REFERENCES marca(id)
);


CREATE TABLE cliente (
    id          SERIAL          PRIMARY KEY,
    nombre      VARCHAR(50)     NOT NULL,
    apellidos   VARCHAR(100)    NOT NULL,
    dni         VARCHAR(9)      NOT NULL UNIQUE,
    email       VARCHAR(100)    UNIQUE,
    telefono    VARCHAR(15)
);


CREATE TABLE venta (
    id              SERIAL          PRIMARY KEY,
    fecha           TIMESTAMP       NOT NULL DEFAULT NOW(),
    precio_final    NUMERIC(10,2)   NOT NULL,
    id_marca        INTEGER         NOT NULL,
    num_bastidor    CHAR(17)        NOT NULL,
    id_cliente      INTEGER         NOT NULL,
    CONSTRAINT fk_venta_vehiculo
        FOREIGN KEY (id_marca, num_bastidor)
        REFERENCES vehiculo(id_marca, num_bastidor),
    CONSTRAINT fk_venta_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id),
    CONSTRAINT uq_venta_vehiculo
        UNIQUE (id_marca, num_bastidor)
);

INSERT INTO marca (nombre, pais_origen) VALUES
    ('Volkswagen', 'Alemania'),
    ('Toyota',     'Japón'),
    ('Renault',    'Francia'),
    ('SEAT',       'España'),
    ('BMW',        'Alemania');


INSERT INTO vehiculo
    (id_marca, num_bastidor,       matricula,  modelo,  anio,  precio,    km,    color,   combustible, estado,     descripcion)
VALUES
    (1, 'WVWZZZ1JZYW123456', '1234ABC', 'Golf',     2019, 18500.00, 45000, 'Blanco', 'GASOLINA',  'EN_VENTA',  'Un solo propietario, libro de revisiones completo.'),
    (1, 'WVWZZZ1JZYW789012', '5678DEF', 'Passat',   2018, 21000.00, 67000, 'Gris',   'DIESEL',    'EN_VENTA',  'Ideal para trayectos largos, consumo medio 5,2 l/100.'),
    (2, 'JTDKN3DUA0J123456', '9012GHI', 'Corolla',  2020, 22500.00, 30000, 'Rojo',   'HIBRIDO',   'EN_VENTA',  'Híbrido sin enchufe, bajo consumo en ciudad.'),
    (3, 'VF1BJ0J0539456789', '3456JKL', 'Megane',   2017, 12000.00, 95000, 'Azul',   'DIESEL',    'EN_VENTA',  'Revisiones al día, neumáticos nuevos.'),
    (4, 'VSSZZZ5FZK1234567', '7890MNO', 'Ibiza',    2021, 16000.00, 15000, 'Negro',  'GASOLINA',  'EN_VENTA',  'Casi nuevo, primera matrícula 2021.'),
    (5, 'WBA3A5G53DNP12345', '2345PQR', '320d',     2016, 27000.00, 88000, 'Blanco', 'DIESEL',    'RESERVADO', 'Serie 3 diésel, paquete deportivo M-Sport.');


INSERT INTO cliente (nombre, apellidos, dni, email, telefono) VALUES
    ('Carlos', 'García López',      '12345678Z', 'carlos.garcia@email.com', '600111222'),
    ('Ana',    'Martínez Ruiz',     '87654321X', 'ana.martinez@email.com',  '611333444'),
    ('Pedro',  'Sánchez Fernández', '11223344M', 'pedro.sanchez@email.com', '622555666');


INSERT INTO venta (fecha, precio_final, id_marca, num_bastidor, id_cliente)
    VALUES ('2024-03-15 10:30:00', 11500.00, 3, 'VF1BJ0J0539456789', 1);

UPDATE vehiculo
    SET estado = 'VENDIDO'
    WHERE id_marca = 3 AND num_bastidor = 'VF1BJ0J0539456789';