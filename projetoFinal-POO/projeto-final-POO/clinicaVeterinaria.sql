DROP DATABASE IF EXISTS clinicaveterinaria;
CREATE DATABASE clinicaveterinaria DEFAULT CHARACTER SET utf8;
USE clinicaveterinaria;

-- Pessoa
CREATE TABLE Pessoa (
  cpf CHAR(11) NOT NULL,
  nome VARCHAR(100) NOT NULL,
  telefone VARCHAR(20) NOT NULL,
  PRIMARY KEY (cpf)
);

-- Tutor
CREATE TABLE Tutor (
  Pessoa_cpf CHAR(11) NOT NULL,
  PRIMARY KEY (Pessoa_cpf),
  FOREIGN KEY (Pessoa_cpf) REFERENCES Pessoa (cpf)
);

-- Veterinario
CREATE TABLE Veterinario (
  Pessoa_cpf CHAR(11) NOT NULL,
  crmv VARCHAR(45) NOT NULL,
  PRIMARY KEY (Pessoa_cpf),
  FOREIGN KEY (Pessoa_cpf) REFERENCES Pessoa (cpf)
);

-- Animal
CREATE TABLE Animal (
  idAnimal INT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(45),
  idade INT,
  raca VARCHAR(45),
  Tutor_Pessoa_cpf CHAR(11) NOT NULL,
  PRIMARY KEY (idAnimal),
  FOREIGN KEY (Tutor_Pessoa_cpf) REFERENCES Tutor (Pessoa_cpf)
);

-- Gato
CREATE TABLE Gato (
  Animal_idAnimal INT NOT NULL,
  PRIMARY KEY (Animal_idAnimal),
  FOREIGN KEY (Animal_idAnimal) REFERENCES Animal (idAnimal)
  ON DELETE CASCADE
);

-- Cachorro
CREATE TABLE Cachorro (
  Animal_idAnimal INT NOT NULL,
  PRIMARY KEY (Animal_idAnimal),
  FOREIGN KEY (Animal_idAnimal) REFERENCES Animal (idAnimal)
  ON DELETE CASCADE
);

-- Consulta
CREATE TABLE Consulta (
  idConsulta INT NOT NULL AUTO_INCREMENT,
  dataHora DATETIME NOT NULL,
  status ENUM('AGENDADA','CANCELADA','REALIZADA') NOT NULL,
  motivo VARCHAR(45),
  diagnostico TEXT,
  prescricao TEXT,
  Veterinario_Pessoa_cpf CHAR(11) NOT NULL,
  Animal_idAnimal INT NOT NULL,
  PRIMARY KEY (idConsulta),
  FOREIGN KEY (Veterinario_Pessoa_cpf) REFERENCES Veterinario (Pessoa_cpf),
  FOREIGN KEY (Animal_idAnimal) REFERENCES Animal (idAnimal)
);

