CREATE DATABASE IF NOT EXISTS pokedexdb;
USE pokedexdb;

CREATE TABLE IF NOT EXISTS Tipo (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Pokemon (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    codigo_tipo_primario INT NOT NULL,
    codigo_tipo_secundario INT NULL,
    FOREIGN KEY (codigo_tipo_primario) REFERENCES Tipo(codigo),
    FOREIGN KEY (codigo_tipo_secundario) REFERENCES Tipo(codigo)
);
