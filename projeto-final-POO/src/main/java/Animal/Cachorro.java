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

    public void setTutorCpf(String tutorCpf) {
        this.tutorCpf = tutorCpf;
    }

    public void setTutorTelefone(String tutorTelefone) {
        this.tutorTelefone = tutorTelefone;
    }

    public Cachorro(int id, String nome, int idade, String raca) {
        super(id, nome, idade, raca);
    }

    @Override
    public String getVacinaPrimeiraConsulta() {
        return "Recomendação: aplicar vacinas V8/V10 + antirrábica, conforme protocolo do veterinário.";
    }

    public static void create(Connection conn, Cachorro cachorro, String cpfTutor) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
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

            cachorro.setId(idAnimalGerado);

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

        String sql = "SELECT a.idAnimal, a.nome AS animalNome, a.idade, a.raca, p.nome AS tutorNome,p.cpf AS tutorCpf, p.telefone AS tutorTelefone FROM Animal a " +
                "JOIN Cachorro c ON c.Animal_idAnimal = a.idAnimal " +
                "JOIN Tutor t ON t.Pessoa_cpf = a.Tutor_Pessoa_cpf " +
                "JOIN Pessoa p ON p.cpf = t.Pessoa_cpf WHERE a.nome = ?ORDER BY a.nome";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Cachorro cachorro = new Cachorro(
                        rs.getInt("idAnimal"),
                        rs.getString("animalNome"),
                        rs.getInt("idade"),
                        rs.getString("raca")
                );

                cachorro.setTutorNome(rs.getString("tutorNome"));
                cachorro.setTutorCpf(rs.getString("tutorCpf"));
                cachorro.setTutorTelefone(rs.getString("tutorTelefone"));

                lista.add(cachorro);
            }
        }
        return lista;
    }

    public static List<Cachorro> readAll(Connection conn) throws SQLException {
        List<Cachorro> cachorros = new ArrayList<>();

        String sql = "SELECT a.idAnimal, a.nome, a.idade, a.raca, p.nome AS tutorNome, p.cpf  AS tutorCpf FROM   Animal a JOIN Cachorro c ON a.idAnimal = c.Animal_idAnimal " +
                "JOIN Tutor t ON t.Pessoa_cpf = a.Tutor_Pessoa_cpf " +
                "JOIN Pessoa p ON p.cpf = t.Pessoa_cpf ORDER BY p.nome, a.nome";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Cachorro cachorro = new Cachorro(
                        rs.getInt("idAnimal"),
                        rs.getString("nome"),
                        rs.getInt("idade"),
                        rs.getString("raca")
                );

                cachorro.setTutorNome(rs.getString("tutorNome"));
                cachorros.add(cachorro);
            }
        }

        return cachorros;
    }
}
