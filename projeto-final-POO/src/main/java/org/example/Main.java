package org.example;

import Animal.Animal;
import Animal.Cachorro;
import Animal.Gato;
import Database.ConnectionFactory;
import Pessoa.Tutor;
import Pessoa.Veterinario;
import Servicos.Consulta;
import Servicos.StatusConsulta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection();
             Scanner sc = new Scanner(System.in)) {

            int opcao;
            do {
                System.out.println("==== MENU PRINCIPAL ====");
                System.out.println("1 - Cadastro");
                System.out.println("2 - Busca");
                System.out.println("3 - Consultas");
                System.out.println("0 - Sair");
                System.out.print("Escolha: ");
                opcao = Integer.parseInt(sc.nextLine());

                switch (opcao) {
                    case 1 -> menuCadastro(conn, sc);
                    case 2 -> menuBusca(conn, sc);
                    case 3 -> menuConsultas(conn, sc);
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opção inválida!");
                }

            } while (opcao != 0);

        } catch (SQLException e) {
            System.out.println("Erro de banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void menuCadastro(Connection conn, Scanner sc) {
        int opcao;
        do {
            System.out.println("\n=== CADASTRO ===");
            System.out.println("1 - Tutor");
            System.out.println("2 - Animal");
            System.out.println("3 - Veterinário");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> menuCadastroTutor(conn, sc);
                    case 2 -> menuCadastroAnimal(conn, sc);
                    case 3 -> menuCadastroVeterinario(conn, sc);
                    case 0 -> {}
                    default -> System.out.println("Opção inválida!");
                }
            } catch (SQLException e) {
                System.out.println("Erro no cadastro: " + e.getMessage());
            }

        } while (opcao != 0);
    }

    private static void menuCadastroTutor(Connection conn, Scanner sc) throws SQLException {
        int opcao;
        do {
            System.out.println("\n=== CADASTRO TUTOR ===");
            System.out.println("1 - Cadastrar tutor");
            System.out.println("2 - Excluir tutor");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1 -> {
                    System.out.print("CPF: ");
                    String cpf = sc.nextLine();
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Telefone: ");
                    String telefone = sc.nextLine();

                    Tutor t = new Tutor(nome, cpf, telefone);
                    Tutor.create(conn, t);
                    System.out.println("Tutor cadastrado com sucesso!");
                }

                case 2 -> {
                    System.out.print("CPF do tutor a excluir: ");
                    String cpfDel = sc.nextLine();
                    Tutor.delete(conn, cpfDel);
                    System.out.println("Tutor deletado com sucesso!");
                }
                case 0 -> {}
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void menuCadastroAnimal(Connection conn, Scanner sc) throws SQLException {
        int opcao;
        do {
            System.out.println("\n=== CADASTRO ANIMAL ===");
            System.out.println("1 - Cadastrar Gato");
            System.out.println("2 - Cadastrar Cachorro");
            System.out.println("3 - Excluir Animal");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1 -> {
                    System.out.print("Nome do gato: ");
                    String nome = sc.nextLine();
                    System.out.print("Idade: ");
                    int idade = Integer.parseInt(sc.nextLine());
                    System.out.print("Raça: ");
                    String raca = sc.nextLine();
                    System.out.print("CPF do tutor: ");
                    String cpfTutor = sc.nextLine();

                    Gato g = new Gato(0, nome, idade, raca);
                    Gato.create(conn, g, cpfTutor);
                }
                case 2 -> {
                    System.out.print("Nome do cachorro: ");
                    String nome = sc.nextLine();
                    System.out.print("Idade: ");
                    int idade = Integer.parseInt(sc.nextLine());
                    System.out.print("Raça: ");
                    String raca = sc.nextLine();
                    System.out.print("CPF do tutor: ");
                    String cpfTutor = sc.nextLine();

                    Cachorro c = new Cachorro(0, nome, idade, raca);
                    Cachorro.create(conn, c, cpfTutor);
                }
                case 3 -> {
                    System.out.print("ID do animal a excluir: ");
                    int id = Integer.parseInt(sc.nextLine());
                    Animal.deleteAnimal(conn, id);
                    System.out.println("Animal excluído com sucesso!");

                }
                case 0 -> {}
                default -> System.out.println("Opção inválida!");
            }

        } while (opcao != 0);
    }

    private static void menuCadastroVeterinario(Connection conn, Scanner sc) throws SQLException {
        int opcao;
        do {
            System.out.println("\n=== CADASTRO VETERINÁRIO ===");
            System.out.println("1 - Cadastrar veterinário");
            System.out.println("2 - Excluir veterinário");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1 -> {
                    System.out.print("CPF: ");
                    String cpf = sc.nextLine();
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Telefone: ");
                    String telefone = sc.nextLine();
                    System.out.print("CRMV: ");
                    String crmv = sc.nextLine();

                    Veterinario v = new Veterinario(nome, cpf, telefone, crmv);
                    Veterinario.create(conn, v);
                }
                case 2 -> {
                    System.out.print("CPF do veterinário a excluir: ");
                    String cpfDel = sc.nextLine();
                    Veterinario.delete(conn, cpfDel);
                }
                case 0 -> {}
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void menuBusca(Connection conn, Scanner sc) {
        int opcao;
        do {
            System.out.println("\n=== BUSCA ===");
            System.out.println("1 - Animal");
            System.out.println("2 - Tutor");
            System.out.println("3 - Veterinário");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> menuBuscaAnimal(conn, sc);
                    case 2 -> menuBuscaTutor(conn, sc);
                    case 3 -> menuBuscaVeterinario(conn, sc);
                    case 0 -> {}
                    default -> System.out.println("Opção inválida!");
                }
            } catch (SQLException e) {
                System.out.println("Erro na busca: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private static void menuBuscaAnimal(Connection conn, Scanner sc) throws SQLException {
        int opcao;
        do {
            System.out.println("\n=== BUSCA ANIMAL ===");
            System.out.println("1 - Listar todos os gatos");
            System.out.println("2 - Listar todos os cachorros");
            System.out.println("3 - Buscar gato por nome");
            System.out.println("4 - Buscar cachorro por nome");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1 -> {
                    List<Gato> gatos = Gato.readAll(conn);
                    for (Gato g : gatos) {
                        System.out.println("[" + g.getId() + "] " + g.getNome() +
                                " | Tutor: " + g.getTutorNome());
                    }
                }
                case 2 -> {
                    List<Cachorro> dogs = Cachorro.readAll(conn);
                    for (Cachorro c : dogs) {
                        System.out.println("[" + c.getId() + "] " + c.getNome() +
                                " | Tutor: " + c.getTutorNome());
                    }
                }
                case 3 -> {
                    System.out.print("Nome do gato: ");
                    String nome = sc.nextLine();
                    List<Gato> gatos = Gato.readAllByName(conn, nome);
                    if (gatos.isEmpty()) {
                        System.out.println("Nenhum gato encontrado.");
                    } else {
                        for (Gato g : gatos) {
                            System.out.println("[" + g.getId() + "] " + g.getNome() +
                                    " | Tutor: " + g.getTutorNome());
                        }
                    }
                }
                case 4 -> {
                    System.out.print("Nome do cachorro: ");
                    String nome = sc.nextLine();
                    List<Cachorro> dogs = Cachorro.readByName(conn, nome);
                    if (dogs.isEmpty()) {
                        System.out.println("Nenhum cachorro encontrado.");
                    } else {
                        for (Cachorro c : dogs) {
                            System.out.println("[" + c.getId() + "] " + c.getNome() +
                                    " | Tutor: " + c.getTutorNome());
                        }
                    }
                }
                case 0 -> {}
                default -> System.out.println("Opção inválida!");
            }

        } while (opcao != 0);
    }

    private static void menuBuscaTutor(Connection conn, Scanner sc) throws SQLException {
        int opcao;
        do {
            System.out.println("\n=== BUSCA TUTOR ===");
            System.out.println("1 - Listar todos os tutores");
            System.out.println("2 - Buscar tutor por nome");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1 -> {
                    List<Tutor> tutores = Tutor.readAll(conn);
                    for (Tutor t : tutores) {
                        System.out.println("Tutor: " + t.getNome() + " | CPF: " + t.getCpf());
                        System.out.println("Animais:");
                        for (Animal a : t.getAnimais()) {
                            System.out.println(" - [" + a.getId() + "] " + a.getNome() + " (" + a.getRaca() + ")");
                        }
                        System.out.println("----------------------------");
                    }
                }
                case 2 -> {
                    System.out.print("Nome do tutor: ");
                    String nome = sc.nextLine();
                    List<Tutor> tutores = Tutor.readByName(conn, nome);
                    if (tutores.isEmpty()) {
                        System.out.println("Nenhum tutor encontrado.");
                    } else {
                        for (Tutor t : tutores) {
                            System.out.println("Tutor: " + t.getNome() + " | CPF: " + t.getCpf());
                            System.out.println("Animais:");
                            for (Animal a : t.getAnimais()) {
                                System.out.println(" - [" + a.getId() + "] " + a.getNome() + " (" + a.getRaca() + ")");
                            }
                            System.out.println("----------------------------");
                        }
                    }
                }
                case 0 -> {}
                default -> System.out.println("Opção inválida!");
            }

        } while (opcao != 0);
    }

    private static void menuBuscaVeterinario(Connection conn, Scanner sc) throws SQLException {
        int opcao;
        do {
            System.out.println("\n=== BUSCA VETERINÁRIO ===");
            System.out.println("1 - Listar todos os veterinários");
            System.out.println("2 - Buscar veterinário por nome");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1 -> {
                    List<Veterinario> vets = Veterinario.readAll(conn);
                    for (Veterinario v : vets) {
                        System.out.println("Nome: " + v.getNome() + " | CPF: " + v.getCpf() + " | CRMV: " + v.getCrmv());
                    }
                }
                case 2 -> {
                    System.out.print("Nome do veterinário: ");
                    String nome = sc.nextLine();
                    List<Veterinario> vets = Veterinario.readByName(conn, nome);
                    if (vets.isEmpty()) {
                        System.out.println("Nenhum veterinário encontrado.");
                    } else {
                        for (Veterinario v : vets) {
                            System.out.println("Nome: " + v.getNome() + " | CPF: " + v.getCpf() + " | CRMV: " + v.getCrmv());
                        }
                    }
                }
                case 0 -> {}
                default -> System.out.println("Opção inválida!");
            }

        } while (opcao != 0);
    }

    private static void menuConsultas(Connection conn, Scanner sc) {
        int opcao;
        do {
            System.out.println("\n=== CONSULTAS ===");
            System.out.println("1 - Agendar consulta");
            System.out.println("2 - Listar todas as consultas");
            System.out.println("3 - Listar consultas por animal");
            System.out.println("4 - Preencher receita e gerar TXT");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            try {
                switch (opcao) {
                    case 1 -> agendarConsulta(conn, sc);
                    case 2 -> listarConsultas(conn);
                    case 3 -> listarConsultasPorAnimal(conn, sc);
                    case 4 -> gerarReceitaTXT(conn, sc);
                    case 0 -> {}
                    default -> System.out.println("Opção inválida!");
                }
            } catch (SQLException e) {
                System.out.println("Erro em consultas: " + e.getMessage());
            }

        } while (opcao != 0);
    }

    private static void agendarConsulta(Connection conn, Scanner sc) throws SQLException {
        System.out.print("ID do animal: ");
        int idAnimal = Integer.parseInt(sc.nextLine());

        System.out.print("CPF do veterinário: ");
        String cpfVet = sc.nextLine();

        System.out.print("Data/Hora (texto): ");
        String dataHora = sc.nextLine();

        System.out.print("Motivo: ");
        String motivo = sc.nextLine();

        Consulta c = new Consulta(
                0,
                dataHora,
                StatusConsulta.AGENDADA,
                motivo,
                null,
                null,
                idAnimal,
                cpfVet
        );

        Consulta.create(conn, c);
        System.out.println("Consulta agendada! ID: " + c.getId());
    }

    private static void listarConsultas(Connection conn) throws SQLException {
        List<Consulta> consultas = Consulta.readAll(conn);
        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta cadastrada.");
        } else {
            for (Consulta c : consultas) {
                System.out.println("Consulta #" + c.getId() + " | " + c.getDataHora() +
                        " | Status: " + c.getStatus());
                System.out.println("Animal: " + c.getAnimalNome());
                System.out.println("Tutor: " + c.getTutorNome());
                System.out.println("Veterinário: " + c.getVetNome() + " (CRMV " + c.getVetCrmv() + ")");
                System.out.println("----------------------------------");
            }
        }
    }

    private static void listarConsultasPorAnimal(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Nome do animal: ");
        String nomeAnimal = sc.nextLine().trim();

        List<Consulta> consultas = Consulta.readByAnimalName(conn, nomeAnimal);

        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta para esse animal.");
            return;
        }

        for (Consulta c : consultas) {
            System.out.println("Consulta #" + c.getId()
                    + " | " + c.getDataHora()
                    + " | Status: " + c.getStatus());

            System.out.println("Paciente: " + c.getAnimalNome()
                    + " (" + c.getAnimalRaca() + ")");

            System.out.println("Veterinário: " + c.getVetNome()
                    + " (CRMV " + c.getVetCrmv() + ")");

            System.out.println("-------------------------------------------");
        }
    }

    private static void gerarReceitaTXT(Connection conn, Scanner sc) throws SQLException {
        System.out.print("ID da consulta: ");
        int idConsulta = Integer.parseInt(sc.nextLine());

        Gato gato = new Gato(0,"gatoAux", 0, "x");
        Cachorro cachorro = new Cachorro(0, "cachorroAux", 0, "x");

        Consulta consulta = Consulta.readById(conn, idConsulta);

        if (consulta == null) {
            System.out.println("Consulta não encontrada!");
            return;
        }
        if (Consulta.isPrimeiraConsulta(conn, consulta.getAnimalId())) {
            System.out.println("\n*** Primeira consulta deste animal! ***");

            boolean ehGato = false;

            String sqlGato = "SELECT 1 FROM Gato WHERE Animal_idAnimal = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlGato)) {
                st.setInt(1, consulta.getAnimalId());
                ResultSet rs = st.executeQuery();
                ehGato = rs.next();
            }

            if (ehGato) {
                System.out.println(gato.getVacinaPrimeiraConsulta());
            } else {
                System.out.println(cachorro.getVacinaPrimeiraConsulta());
            }
        }

        if (consulta.getPrescricao() == null || consulta.getPrescricao().isBlank()) {
            consulta.preencherReceita(sc);
            Consulta.updateDiagnosticoEPrescricao(
                    conn,
                    idConsulta,
                    consulta.getDiagnostico(),
                    consulta.getPrescricao()
            );
        }
        String caminho = "receita_" + idConsulta + ".txt";
        new Thread(() -> {
            try {
                consulta.exportarTXT(caminho);
                System.out.println("Receita gerada em background: " + caminho);
            } catch (Exception e) {
                System.out.println("Erro ao gerar arquivo TXT (thread): " + e.getMessage());
            }
        }).start();
    }
}

