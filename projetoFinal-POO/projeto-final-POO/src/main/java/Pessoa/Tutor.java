package Pessoa;

import Animal.Animal;
import Animal.Gato;
import Animal.Cachorro;
import java.sql.*;
import java.util.*;

public class Tutor extends Pessoa {
    private List<Animal> animais = new ArrayList<>();

    public List<Animal> getAnimais() {
        return animais;
    }

    public Tutor(String nome, String cpf, String telefone) {
        super(nome, cpf, telefone);
    }

    public static void create(Connection conn, Tutor tutor) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            // 0) Verificar se já existe tutor com esse CPF
            String sqlCheck = "SELECT 1 FROM Tutor WHERE Pessoa_cpf = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlCheck)) {
                st.setString(1, tutor.getCpf());
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    throw new SQLException("Erro: Já existe um tutor cadastrado com este CPF.");
                }
            }

            // 1) Inserir na tabela Pessoa
            String sqlPessoa = "INSERT INTO Pessoa (cpf, nome, telefone) VALUES (?, ?, ?)";
            try (PreparedStatement st = conn.prepareStatement(sqlPessoa)) {
                st.setString(1, tutor.getCpf());
                st.setString(2, tutor.getNome());
                st.setString(3, tutor.getTelefone());
                st.executeUpdate();
            }

            // 2) Inserir na tabela Tutor
            String sqlTutor = "INSERT INTO Tutor (Pessoa_cpf) VALUES (?)";
            try (PreparedStatement st = conn.prepareStatement(sqlTutor)) {
                st.setString(1, tutor.getCpf());
                st.executeUpdate();
            }

            conn.commit();
            System.out.println("Tutor cadastrado com sucesso!");

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

    public static List<Tutor> readByName(Connection conn, String nomeTutor) throws SQLException {
        Map<String, Tutor> mapa = new HashMap<>();
        List<Tutor> lista = new ArrayList<>();

        String sql = """
        SELECT
            p.cpf,
            p.nome AS tutorNome,
            p.telefone,
            a.idAnimal,
            a.nome AS animalNome,
            a.idade,
            a.raca,

            CASE
                WHEN g.Animal_idAnimal IS NOT NULL THEN 'GATO'
                WHEN c.Animal_idAnimal IS NOT NULL THEN 'CACHORRO'
                ELSE 'DESCONHECIDO'
            END AS tipoAnimal

        FROM Pessoa p
        JOIN Tutor t      ON t.Pessoa_cpf = p.cpf
        LEFT JOIN Animal a ON a.Tutor_Pessoa_cpf = p.cpf
        LEFT JOIN Gato g    ON g.Animal_idAnimal = a.idAnimal
        LEFT JOIN Cachorro c ON c.Animal_idAnimal = a.idAnimal

        WHERE p.nome = ?
        ORDER BY p.nome, a.nome
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeTutor);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String cpf = rs.getString("cpf");

                // Se ainda não existe o Tutor no mapa, cria
                if (!mapa.containsKey(cpf)) {
                    Tutor tutor = new Tutor(
                            rs.getString("tutorNome"),
                            cpf,
                            rs.getString("telefone")
                    );
                    mapa.put(cpf, tutor);
                }

                Tutor tutor = mapa.get(cpf);

                int idAnimal = rs.getInt("idAnimal");
                if (!rs.wasNull()) {

                    String tipo = rs.getString("tipoAnimal");
                    Animal animal = null;

                    if ("GATO".equals(tipo)) {
                        animal = new Gato(
                                idAnimal,
                                rs.getString("animalNome"),
                                rs.getInt("idade"),
                                rs.getString("raca")
                        );
                    }
                    else if ("CACHORRO".equals(tipo)) {
                        animal = new Cachorro(
                                idAnimal,
                                rs.getString("animalNome"),
                                rs.getInt("idade"),
                                rs.getString("raca")
                        );
                    }

                    tutor.getAnimais().add(animal);
                }
            }
        }

        lista.addAll(mapa.values());
        return lista;
    }

    public static List<Tutor> readAll(Connection conn) throws SQLException {
        Map<String, Tutor> mapa = new HashMap<>(); // CPF é String (correto no seu modelo)

        String sql = """
        SELECT
            p.cpf,
            p.nome      AS tutorNome,
            p.telefone  AS telefone,
            a.idAnimal  AS animalId,
            a.nome      AS animalNome,
            a.idade     AS animalIdade,
            a.raca      AS animalRaca,

            CASE
                WHEN g.Animal_idAnimal IS NOT NULL THEN 'GATO'
                WHEN c.Animal_idAnimal IS NOT NULL THEN 'CACHORRO'
                ELSE NULL
            END AS tipoAnimal

        FROM Tutor t
        JOIN Pessoa p   ON p.cpf = t.Pessoa_cpf
        LEFT JOIN Animal   a ON a.Tutor_Pessoa_cpf = t.Pessoa_cpf
        LEFT JOIN Gato     g ON g.Animal_idAnimal = a.idAnimal
        LEFT JOIN Cachorro c ON c.Animal_idAnimal = a.idAnimal

        ORDER BY p.nome, a.nome
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                String cpf = rs.getString("cpf");

                // cria o Tutor apenas uma vez
                mapa.putIfAbsent(cpf, new Tutor(
                        rs.getString("tutorNome"),
                        cpf,                               // CPF como String
                        rs.getString("telefone")
                ));

                Tutor tutor = mapa.get(cpf);

                // Verificar se existe animal (por causa do LEFT JOIN)
                int animalId = rs.getInt("animalId");
                if (!rs.wasNull()) {

                    String tipo = rs.getString("tipoAnimal");
                    Animal animal = null;

                    if ("GATO".equals(tipo)) {
                        animal = new Gato(
                                animalId,
                                rs.getString("animalNome"),
                                rs.getInt("animalIdade"),
                                rs.getString("animalRaca")
                        );
                    } else if ("CACHORRO".equals(tipo)) {
                        animal = new Cachorro(
                                animalId,
                                rs.getString("animalNome"),
                                rs.getInt("animalIdade"),
                                rs.getString("animalRaca")
                        );
                    }

                    if (animal != null) {
                        tutor.getAnimais().add(animal);
                    }
                }
            }
        }

        return new ArrayList<>(mapa.values());
    }

    public static void delete(Connection conn, String cpf) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            // 1) Deletar Gatos vinculados ao tutor
            String sqlGato = """
            DELETE FROM Gato 
            WHERE Animal_idAnimal IN (
                SELECT idAnimal FROM Animal WHERE Tutor_Pessoa_cpf = ?
            )
        """;
            try (PreparedStatement st = conn.prepareStatement(sqlGato)) {
                st.setString(1, cpf);
                st.executeUpdate();
            }

            // 2) Deletar Cachorros vinculados ao tutor
            String sqlCachorro = """
            DELETE FROM Cachorro 
            WHERE Animal_idAnimal IN (
                SELECT idAnimal FROM Animal WHERE Tutor_Pessoa_cpf = ?
            )
        """;
            try (PreparedStatement st = conn.prepareStatement(sqlCachorro)) {
                st.setString(1, cpf);
                st.executeUpdate();
            }

            // 3) Deletar os Animais
            String sqlAnimal = "DELETE FROM Animal WHERE Tutor_Pessoa_cpf = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlAnimal)) {
                st.setString(1, cpf);
                st.executeUpdate();
            }

            // 4) Deletar Tutor
            String sqlTutor = "DELETE FROM Tutor WHERE Pessoa_cpf = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlTutor)) {
                st.setString(1, cpf);
                st.executeUpdate();
            }

            // 5) Deletar Pessoa
            String sqlPessoa = "DELETE FROM Pessoa WHERE cpf = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlPessoa)) {
                st.setString(1, cpf);
                st.executeUpdate();
            }

            conn.commit();
            System.out.println("Tutor deletado com sucesso!");

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }
}

