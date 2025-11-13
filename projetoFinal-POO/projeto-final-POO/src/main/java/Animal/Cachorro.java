package Animal;


import java.sql.*;
import java.util.*;

public class Cachorro extends Animal {
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

    public String getTutorTelefone() {
        return tutorTelefone;
    }

    public void setTutorTelefone(String tutorTelefone) {
        this.tutorTelefone = tutorTelefone;
    }

    public Cachorro(int id, String nome, int idade, String raca) {
        super(id, nome, idade, raca);
    }

    public static void create(Connection conn, Cachorro cachorro, String cpfTutor) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            // 1) INSERIR ANIMAL
            String sqlAnimal = "INSERT INTO Animal (nome, idade, raca, Tutor_Pessoa_cpf) VALUES (?, ?, ?, ?)";

            int idAnimalGerado;

            try (PreparedStatement st = conn.prepareStatement(sqlAnimal, Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, cachorro.getNome());
                st.setInt(2, cachorro.getIdade());
                st.setString(3, cachorro.getRaca());
                st.setString(4, cpfTutor);

                st.executeUpdate();

                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Falha ao obter ID do animal gerado.");
                    }
                    idAnimalGerado = rs.getInt(1);
                }
            }

            // Atualiza o objeto em memória
            cachorro.setId(idAnimalGerado);

            // 2) INSERIR CACHORRO
            String sqlCachorro = "INSERT INTO Cachorro (Animal_idAnimal) VALUES (?)";

            try (PreparedStatement st = conn.prepareStatement(sqlCachorro)) {
                st.setInt(1, idAnimalGerado);
                st.executeUpdate();
            }

            conn.commit();
            System.out.println("Cachorro cadastrado com sucesso! ID = " + idAnimalGerado);

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }


    public static List<Cachorro> readByName(Connection conn, String nome) throws SQLException {
        List<Cachorro> lista = new ArrayList<>();

        String sql = """
        SELECT
            a.idAnimal,
            a.nome AS animalNome,
            a.idade,
            a.raca,

            p.nome AS tutorNome,
            p.cpf  AS tutorCpf,
            p.telefone AS tutorTelefone

        FROM Animal a
        JOIN Cachorro c ON c.Animal_idAnimal = a.idAnimal
        JOIN Tutor t    ON t.Pessoa_cpf = a.Tutor_Pessoa_cpf
        JOIN Pessoa p   ON p.cpf = t.Pessoa_cpf

        WHERE a.nome = ?
        ORDER BY a.nome
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Cachorro dog = new Cachorro(
                        rs.getInt("idAnimal"),
                        rs.getString("animalNome"),
                        rs.getInt("idade"),
                        rs.getString("raca")
                );

                // Dados do tutor (caso você tenha os campos na classe Cachorro)
                dog.setTutorNome(rs.getString("tutorNome"));
                dog.setTutorCpf(rs.getString("tutorCpf"));
                dog.setTutorTelefone(rs.getString("tutorTelefone"));

                lista.add(dog);
            }
        }

        return lista;
    }



    public static List<Cachorro> readAll(Connection conn) throws SQLException {
        List<Cachorro> cachorros = new ArrayList<>();

        String sql = """
        SELECT\s
            a.idAnimal,
            a.nome,
            a.idade,
            a.raca,
            p.nome AS tutorNome,
            p.cpf  AS tutorCpf
        FROM Animal a
        JOIN Cachorro c ON a.idAnimal = c.Animal_idAnimal
        JOIN Tutor t    ON t.Pessoa_cpf = a.Tutor_Pessoa_cpf
        JOIN Pessoa p   ON p.cpf = t.Pessoa_cpf
        ORDER BY p.nome, a.nome
       \s""";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                // cria o cachorro com base no idAnimal (int)
                Cachorro cachorro = new Cachorro(
                        rs.getInt("idAnimal"),
                        rs.getString("nome"),
                        rs.getInt("idade"),
                        rs.getString("raca")
                );

                // atributos extras do tutor (caso você tenha)
                cachorro.setTutorNome(rs.getString("tutorNome"));

                cachorros.add(cachorro);
            }
        }

        return cachorros;
    }

    public static void delete(Connection conn, int idAnimal) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            // 1) Apaga primeiro da tabela Cachorro
            String sqlCachorro = "DELETE FROM Cachorro WHERE Animal_idAnimal = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlCachorro)) {
                st.setInt(1, idAnimal);
                st.executeUpdate();
            }

            // 2) Depois apaga da tabela Animal
            String sqlAnimal = "DELETE FROM Animal WHERE idAnimal = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlAnimal)) {
                st.setInt(1, idAnimal);
                st.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }



}
