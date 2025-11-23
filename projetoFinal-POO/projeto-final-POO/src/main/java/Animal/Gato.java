package Animal;

import java.sql.*;
import java.util.*;

public class Gato extends Animal {
    private String tutorNome;
    private String tutorCpf;
    private String tutorTelefone;

    public String getTutorNome() {
        return tutorNome;
    }
    public void setTutorNome(String tutorNome) {
        this.tutorNome = tutorNome;
    }

    public String getTutorCpf() {
        return tutorCpf;
    }
    public void setTutorCpf(String tutorCpf) {
        this.tutorCpf = tutorCpf;
    }

    public void setTutorTelefone(String tutorTelefone) {
        this.tutorTelefone = tutorTelefone;
    }

    public Gato(int id, String nome, int idade, String raca) {
        super(id, nome, idade, raca);
    }

    @Override
    public String getVacinaPrimeiraConsulta() {
        return "Recomendação: Múltipla felina (V3/V4/V5) + antirrábica, conforme protocolo do veterinário.";
    }

    public static void create(Connection conn, Gato gato, String cpfTutor) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            String sqlAnimal = "INSERT INTO Animal (nome, idade, raca, Tutor_Pessoa_cpf) VALUES (?, ?, ?, ?)";

            int idAnimalGerado;

            try (PreparedStatement st = conn.prepareStatement(sqlAnimal, Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, gato.getNome());
                st.setInt(2, gato.getIdade());
                st.setString(3, gato.getRaca());
                st.setString(4, cpfTutor);

                st.executeUpdate();

                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Falha ao obter ID do animal gerado.");
                    }
                    idAnimalGerado = rs.getInt(1);
                }
            }

            gato.setId(idAnimalGerado);

            String sqlCachorro = "INSERT INTO Gato (Animal_idAnimal) VALUES (?)";

            try (PreparedStatement st = conn.prepareStatement(sqlCachorro)) {
                st.setInt(1, idAnimalGerado);
                st.executeUpdate();
            }

            conn.commit();
            System.out.println("Gato cadastrado com sucesso! ID = " + idAnimalGerado);

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }


    public static List<Gato> readAllByName(Connection conn, String nome) throws SQLException {
        List<Gato> gatos = new ArrayList<>();

        String sql = "SELECT a.idAnimal, a.nome, .idade, a.raca, p.nome AS tutorNome, p.cpf  AS tutorCpf, p.telefone AS tutorTelefone FROM Animal a " +
                "JOIN Gato g ON g.Animal_idAnimal = a.idAnimal " +
                "JOIN Tutor t  ON t.Pessoa_cpf = a.Tutor_Pessoa_cpf " +
                "JOIN Pessoa p ON p.cpf = t.Pessoa_cpf WHERE a.nome = ? ORDER BY  a.idAnimal";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Gato gato = new Gato(
                        rs.getInt("idAnimal"),
                        rs.getString("nome"),
                        rs.getInt("idade"),
                        rs.getString("raca")
                );
                // Tutor
                gato.setTutorNome(rs.getString("tutorNome"));
                gato.setTutorCpf(rs.getString("tutorCpf"));
                gato.setTutorTelefone(rs.getString("tutorTelefone"));

                gatos.add(gato);
            }
        }

        return gatos;
    }


    public static List<Gato> readAll(Connection conn) throws SQLException {
        List<Gato> gatos = new ArrayList<>();

        String sql = "SELECT a.idAnimal, a.nome, a.idade, a.raca, p.nome AS tutorNome, p.cpf  AS tutorCpf, p.telefone AS tutorTelefone FROM Animal a " +
                "JOIN Gato g   ON g.Animal_idAnimal = a.idAnimal " +
                "JOIN Tutor t  ON t.Pessoa_cpf = a.Tutor_Pessoa_cpf " +
                "JOIN Pessoa p ON p.cpf = t.Pessoa_cpf ORDER BY p.nome, a.nome ";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Gato gato = new Gato(
                        rs.getInt("idAnimal"),
                        rs.getString("nome"),
                        rs.getInt("idade"),
                        rs.getString("raca")
                );

                gato.setTutorNome(rs.getString("tutorNome"));
                gato.setTutorCpf(rs.getString("tutorCpf"));
                gato.setTutorTelefone(rs.getString("tutorTelefone"));

                gatos.add(gato);
            }
        }

        return gatos;
    }

    public static void delete(Connection conn, int idAnimal) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            String sqlGato = "DELETE FROM Gato WHERE Animal_idAnimal = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlGato)) {
                st.setInt(1, idAnimal);
                st.executeUpdate();
            }

            String sqlAnimal = "DELETE FROM Animal WHERE idAnimal = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlAnimal)) {
                st.setInt(1, idAnimal);
                st.executeUpdate();
            }

            conn.commit();
            System.out.println("Gato deletado com sucesso!");

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

}

