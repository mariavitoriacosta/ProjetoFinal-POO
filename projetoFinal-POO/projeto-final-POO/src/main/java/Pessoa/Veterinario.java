package Pessoa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Veterinario extends Pessoa {
    private String crmv;

    public Veterinario(String nome, String cpf, String telefone, String crmv) {
        super(nome, cpf, telefone);
        this.crmv = crmv;
    }

    public String getCrmv() {
        return crmv;
    }

    public static void create(Connection conn, Veterinario vet) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            // 0) Verificar se já existe veterinário com este CPF
            String sqlCheck = "SELECT 1 FROM Veterinario WHERE Pessoa_cpf = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlCheck)) {
                st.setString(1, vet.getCpf());
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    throw new SQLException("Erro: Já existe um veterinário com esse CPF.");
                }
            }

            // 1) Inserir Pessoa
            String sqlPessoa = "INSERT INTO Pessoa (cpf, nome, telefone) VALUES (?, ?, ?)";
            try (PreparedStatement st = conn.prepareStatement(sqlPessoa)) {
                st.setString(1, vet.getCpf());
                st.setString(2, vet.getNome());
                st.setString(3, vet.getTelefone());
                st.executeUpdate();
            }

            // 2) Inserir Veterinário
            String sqlVet = "INSERT INTO Veterinario (Pessoa_cpf, crmv) VALUES (?, ?)";
            try (PreparedStatement st = conn.prepareStatement(sqlVet)) {
                st.setString(1, vet.getCpf());
                st.setString(2, vet.getCrmv());
                st.executeUpdate();
            }

            conn.commit();
            System.out.println("Veterinário cadastrado com sucesso!");

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

    public static List<Veterinario> readByName(Connection conn, String nome) throws SQLException {
        List<Veterinario> lista = new ArrayList<>();

        String sql = " SELECT p.cpf, p.nome, p.telefone, v.crmv FROM Pessoa p JOIN Veterinario v ON v.Pessoa_cpf = p.cpf WHERE p.nome = ? ORDER BY p.nome";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Veterinario vet = new Veterinario(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getString("crmv")
                );
                lista.add(vet);
            }
        }

        return lista;
    }

    public static List<Veterinario> readAll(Connection conn) throws SQLException {
        List<Veterinario> lista = new ArrayList<>();

        String sql = """
        SELECT 
            p.cpf,
            p.nome,
            p.telefone,
            v.crmv
        FROM Pessoa p
        JOIN Veterinario v ON v.Pessoa_cpf = p.cpf
        ORDER BY p.nome
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Veterinario vet = new Veterinario(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getString("crmv")
                );
                lista.add(vet);
            }
        }

        return lista;
    }

    public static void delete(Connection conn, String cpf) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            // 1) Deletar consultas do veterinário
            String sqlConsulta = "DELETE FROM Consulta WHERE Veterinario_Pessoa_cpf = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlConsulta)) {
                st.setString(1, cpf);
                st.executeUpdate();
            }

            // 2) Deletar o veterinário
            String sqlVet = "DELETE FROM Veterinario WHERE Pessoa_cpf = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlVet)) {
                st.setString(1, cpf);
                st.executeUpdate();
            }

            // 3) Deletar a pessoa correspondente
            String sqlPessoa = "DELETE FROM Pessoa WHERE cpf = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlPessoa)) {
                st.setString(1, cpf);
                st.executeUpdate();
            }

            conn.commit();
            System.out.println("Veterinário deletado com sucesso!");

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

}

