package org.example.Servicos;

import org.example.Interface.GetInfo;

import java.sql.*;
import java.util.*;


public class Consulta implements GetInfo {
    private int id;
    private String dataHora;
    private String motivo;
    private String diagnostico;
    private String prescricao;
    private int animalId;
    private String animalNome;
    private String animalRaca;
    private String vetCpf;
    private String vetNome;
    private String vetCrmv;
    private String tutorNome;
    private String tutorTelefone;
    private StatusConsulta status;

    public Consulta(int id, String dataHora, StatusConsulta status, String motivo, String diagnostico, String prescricao, int animalId, String vetCpf) {
        this.id = id;
        this.dataHora = dataHora;
        this.status = status;
        this.motivo = motivo;
        this.diagnostico = diagnostico;
        this.prescricao = prescricao;
        this.animalId = animalId;
        this.vetCpf = vetCpf;
    }

    @Override
    public String gerarReceita(java.util.Scanner sc) {
        System.out.println("Preenchendo receita da consulta #" + id);
        System.out.println("Motivo: " + motivo);
        System.out.print("Digite a prescrição: ");
        String texto = sc.nextLine();
        this.prescricao = texto;
        return texto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public String getDataHora() {
        return dataHora;
    }

    public String getMotivo() {

        return motivo;
    }
    public String getDiagnostico() {

        return diagnostico;
    }
    public String getPrescricao() {

        return prescricao;
    }
    public int getAnimalId() {

        return animalId;
    }
    public String getVetCpf() {

        return vetCpf;
    }

    public void setAnimalNome(String animalNome) {
        this.animalNome = animalNome;
    }

    public void setVetNome(String vetNome) {
        this.vetNome = vetNome;
    }

    public void setVetCrmv(String vetCrmv) {
        this.vetCrmv = vetCrmv;
    }

    public void setTutorNome(String tutorNome) {
        this.tutorNome = tutorNome;
    }

    public void setAnimalRaca(String animalRaca) {
        this.animalRaca = animalRaca;
    }

    public String getAnimalNome() {
        return animalNome;
    }

    public String getVetNome() {
        return vetNome;
    }

    public String getTutorNome() {
        return tutorNome;
    }

    public String getVetCrmv() {
        return vetCrmv;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public String getAnimalRaca() {
        return animalRaca;
    }

    public String getTutorTelefone() {
        return tutorTelefone;
    }

    public static void create(Connection conn, Consulta consulta) throws SQLException {
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            String sqlCheckVet = "SELECT 1 FROM Veterinario WHERE Pessoa_cpf = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlCheckVet)) {
                st.setString(1, consulta.getVetCpf());
                ResultSet rs = st.executeQuery();
                if (!rs.next()) {
                    throw new SQLException("Erro: não existe veterinário com o CPF informado.");
                }
            }

            String sqlCheckAnimal = "SELECT 1 FROM Animal WHERE idAnimal = ?";
            try (PreparedStatement st = conn.prepareStatement(sqlCheckAnimal)) {
                st.setInt(1, consulta.getAnimalId());
                ResultSet rs = st.executeQuery();
                if (!rs.next()) {
                    throw new SQLException("Erro: não existe animal com o ID informado.");
                }
            }

            String sqlConsulta = "INSERT INTO Consulta(dataHora, status, motivo, diagnostico, prescricao, Veterinario_Pessoa_cpf, Animal_idAnimal) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement st = conn.prepareStatement(sqlConsulta, Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, consulta.getDataHora());

                StatusConsulta statusEnum = consulta.getStatus();

                if (statusEnum == null) {
                    statusEnum = StatusConsulta.AGENDADA;
                }

                st.setString(2, statusEnum.name());

                st.setString(3, consulta.getMotivo());
                st.setString(4, consulta.getDiagnostico());
                st.setString(5, consulta.getPrescricao());

                st.setString(6, consulta.getVetCpf());
                st.setInt(7, consulta.getAnimalId());

                st.executeUpdate();

                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGerado = rs.getInt(1);
                        consulta.setId(idGerado);
                    }
                }
            }

            conn.commit();
            System.out.println("Consulta agendada com sucesso!");

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

    public static List<Consulta> readByAnimalName(Connection conn, String nomeAnimal) throws SQLException {
        List<Consulta> consultas = new ArrayList<>();

        String sql = " SELECT c.idConsulta, c.dataHora, c.status, c.motivo, c.diagnostico, c.prescricao, a.idAnimal, " +
                "a.nome AS animalNome, a.raca AS animalRaca, " +
                "pVet.nome AS vetNome, v.crmv AS vetCrmv, c.Veterinario_Pessoa_cpf AS vetCpf " +
                "FROM Consulta c " +
                "JOIN Animal a  ON a.idAnimal = c.Animal_idAnimal " +
                "JOIN Veterinario v  ON v.Pessoa_cpf = c.Veterinario_Pessoa_cpf " +
                "JOIN Pessoa pVet  ON pVet.cpf = v.Pessoa_cpf " +
                "WHERE a.nome LIKE ? ORDER BY c.dataHora DESC";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "%" + nomeAnimal + "%");
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Consulta c = new Consulta(
                        rs.getInt("idConsulta"),
                        rs.getString("dataHora"),
                        StatusConsulta.valueOf(rs.getString("status").toUpperCase()),
                        rs.getString("motivo"),
                        rs.getString("diagnostico"),
                        rs.getString("prescricao"),
                        rs.getInt("idAnimal"),
                        rs.getString("vetCpf")
                );

                c.setAnimalNome(rs.getString("animalNome"));
                c.setAnimalRaca(rs.getString("animalRaca"));
                c.setVetNome(rs.getString("vetNome"));
                c.setVetCrmv(rs.getString("vetCrmv"));

                consultas.add(c);
            }
        }

        return consultas;
    }

    public static List<Consulta> readAll(Connection conn) throws SQLException {
        List<Consulta> consultas = new ArrayList<>();

        String sql = " SELECT c.idConsulta, c.dataHora, c.status, c.motivo, c.diagnostico, c.prescricao," +
                "a.idAnimal, a.nome  AS animalNome, a.raca  AS animalRaca, a.idade AS animalIdade, " +
                "pTutor.nome AS tutorNome, pTutor.cpf  AS tutorCpf, " +
                "pVet.nome AS vetNome, pVet.cpf  AS vetCpf, v.crmv AS vetCrmv " +
                "FROM Consulta c JOIN Animal a ON a.idAnimal = c.Animal_idAnimal JOIN Tutor t ON t.Pessoa_cpf = a.Tutor_Pessoa_cpf " +
                "JOIN Pessoa pTutor ON pTutor.cpf = t.Pessoa_cpf " +
                "JOIN Veterinario v ON v.Pessoa_cpf = c.Veterinario_Pessoa_cpf " +
                "JOIN Pessoa pVet ON pVet.cpf = v.Pessoa_cpf " +
                "ORDER BY c.dataHora DESC ";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Consulta consulta = new Consulta(
                        rs.getInt("idConsulta"),
                        rs.getString("dataHora"),
                        StatusConsulta.valueOf(rs.getString("status").toUpperCase()),
                        rs.getString("motivo"),
                        rs.getString("diagnostico"),
                        rs.getString("prescricao"),
                        rs.getInt("idAnimal"),
                        rs.getString("vetCpf")
                );

                consulta.setAnimalNome(rs.getString("animalNome"));
                consulta.setAnimalRaca(rs.getString("animalRaca"));

                consulta.setTutorNome(rs.getString("tutorNome"));

                consulta.setVetNome(rs.getString("vetNome"));
                consulta.setVetCrmv(rs.getString("vetCrmv"));

                consultas.add(consulta);
            }
        }

        return consultas;
    }

    public static void updateReceita(Connection conn, int idConsulta, String receita) throws SQLException {
        String sql = "UPDATE Consulta SET prescricao = ? WHERE idConsulta = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, receita);
            stmt.setInt(2, idConsulta);
            stmt.executeUpdate();
        }
    }
    public static void atualizarStatus(Connection conn, int idConsulta, StatusConsulta novoStatus) throws SQLException {
        String sql = "UPDATE Consulta SET status = ? WHERE idConsulta = ?";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, novoStatus.name());
            st.setInt(2, idConsulta);

            int linhas = st.executeUpdate();
            if (linhas == 0) {
                System.out.println("Nenhuma consulta encontrada com esse ID.");
            }
        }
    }


}







