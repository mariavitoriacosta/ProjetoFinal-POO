# 🐾 Sistema de Gerenciamento de Clínica Veterinária

## Descrição

Este projeto foi desenvolvido como trabalho final da disciplina de Programação Orientada a Objetos (POO). A proposta foi criar um sistema que resolvesse um problema real, utilizando os conceitos aprendidos ao longo da matéria.

A aplicação consiste em um sistema de gerenciamento de clínica veterinária, permitindo o cadastro de animais, tutores e veterinários, além do controle de consultas realizadas na clínica.

---

## Objetivo

O objetivo principal do projeto é simular o funcionamento básico de uma clínica veterinária, aplicando na prática conceitos de orientação a objetos como encapsulamento, herança, polimorfismo e organização do código.

---

## Funcionalidades

O sistema possui as seguintes funcionalidades:

* Cadastro de animais
* Cadastro de tutores
* Cadastro de veterinários
* Listagem de animais, tutores e veterinários
* Agendamento de consultas
* Listagem de consultas
* Finalização de consultas
* Geração de receita em arquivo `.txt` após a consulta

---

## Tecnologias Utilizadas

* Java
* Maven
* MySQL
* Paradigma de Programação Orientada a Objetos

---

## Estrutura do Projeto

O projeto foi organizado em pacotes para facilitar a divisão de responsabilidades:

```
src/
 └── main/
     └── java/
         ├── Animal/
         ├── Pessoa/
         ├── Servicos/
         ├── Database/
         ├── Menu/
         └── org.example/
```

* **Animal** → classes relacionadas aos animais
* **Pessoa** → classes de tutores e veterinários
* **Servicos** → regras de negócio do sistema
* **Database** → conexão com banco de dados
* **Menu** → interação com o usuário (terminal)
* **org.example** → classe principal

---

## Configuração do Banco de Dados (MySQL)

Para executar o sistema corretamente, é necessário configurar o banco de dados MySQL.

### Passo a passo

1. Instale o MySQL (caso ainda não tenha)

2. Crie o banco de dados:

```sql
CREATE DATABASE clinica_veterinaria;
```

3. Acesse o banco:

```sql
USE clinica_veterinaria;
```

4. Configure as credenciais no projeto

No código, localize a classe de conexão no pacote:

```
Database/
```

E ajuste:

```java
String url = "jdbc:mysql://localhost:3306/clinica_veterinaria";
String user = "root";
String password = "123456";
```

---

### Observações

* O MySQL precisa estar rodando
* Verifique usuário, senha e porta (padrão: 3306)
* Dependendo do projeto, pode ser necessário criar as tabelas manualmente

---

## Como Executar

1. Clonar o repositório:

```bash
git clone <URL_DO_REPOSITORIO>
```

2. Abrir o projeto em uma IDE (IntelliJ ou Eclipse)

3. Executar a classe principal dentro de `org.example`

4. O sistema será executado no terminal com um menu interativo

---

## Geração de Receita

Após a finalização de uma consulta, o sistema gera automaticamente um arquivo `.txt` (ex: `receita_1.txt`), contendo as informações da consulta realizada.

---

## Conceitos de POO Aplicados

Durante o desenvolvimento foram utilizados diversos conceitos importantes da disciplina:

* Criação de classes, atributos e métodos
* Encapsulamento
* Herança
* Polimorfismo
* Uso de interfaces e/ou classes abstratas
* Tratamento de exceções
* Organização do código em pacotes
* Persistência de dados (banco de dados e arquivos)

Esses requisitos seguem as orientações da disciplina .

---

## 📌 Observações

Este projeto foi desenvolvido com fins acadêmicos, buscando aplicar na prática os conceitos vistos em sala de aula. Além disso, foi uma oportunidade de entender melhor como organizar um sistema real utilizando orientação a objetos.

---
