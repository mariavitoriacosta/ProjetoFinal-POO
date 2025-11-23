package Servicos;

import java.sql.*;
import java.util.*;


public class Consulta implements ExportarTXT {
    private int id;
    private int animalId;
    private String dataHora;
    private String motivo;
    private String diagnostico;
    private String prescricao;
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

    public void setTutorTelefone(String tutorTelefone) {
        this.tutorTelefone = tutorTelefone;
    }

    public void preencherReceita(Scanner sc) {
        System.out.println("\n=== Preenchendo Dados da Consulta ===");
        System.out.println("Motivo: " + motivo);

        System.out.print("Informe o diagnóstico: ");
        String diag = sc.nextLine();
        this.diagnostico = diag;

        System.out.print("Informe a prescrição (receita): ");
        String presc = sc.nextLine();
        this.prescricao = presc;
    }

    @Override
    public void exportarTXT(String caminhoArquivo) throws Exception {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter(caminhoArquivo))) {

            writer.write("====== RECEITA VETERINÁRIA ======\n\n");

            writer.write("Consulta Nº: " + id + "\n");
            writer.write("Data/Hora: " + dataHora + "\n");
            writer.write("Status: " + (status != null ? status.name() : "NÃO INFORMADO") + "\n\n");

            writer.write("----- Tutor -----\n");
            writer.write("Nome: " + tutorNome + "\n");
            writer.write("Telefone: " + tutorTelefone + "\n\n");

            writer.write("----- Animal -----\n");
            writer.write("Nome: " + animalNome + "\n");
            writer.write("Raça: " + animalRaca + "\n");
            writer.write("ID: " + animalId + "\n\n");

            writer.write("----- Veterinário -----\n");
            writer.write("Nome: " + vetNome + "\n");
            writer.write("CRMV: " + vetCrmv + "\n");
            writer.write("CPF: " + vetCpf + "\n\n");

            writer.write("----- Motivo -----\n");
            writer.write(motivo + "\n\n");

            writer.write("----- Diagnóstico -----\n");
            writer.write(diagnostico + "\n\n");

            writer.write("----- Prescrição -----\n");
            writer.write(prescricao + "\n\n");

            writer.write("=================================\n");
        }
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

    public static Consulta readById(Connection conn, int idConsulta) throws SQLException {
        String sql = " SELECT c.idConsulta, c.dataHora, c.status, c.motivo, c.diagnostico, c.prescricao," +
                "a.idAnimal, a.nome  AS animalNome, a.raca  AS animalRaca, " +
                "pTutor.nome AS tutorNome, pTutor.telefone AS tutorTelefone, " +
                "pVet.nome AS vetNome, pVet.cpf  AS vetCpf, v.crmv AS vetCrmv " +
                "FROM Consulta c " +
                "JOIN Animal a ON a.idAnimal = c.Animal_idAnimal " +
                "JOIN Tutor t ON t.Pessoa_cpf = a.Tutor_Pessoa_cpf " +
                "JOIN Pessoa pTutor ON pTutor.cpf = t.Pessoa_cpf " +
                "JOIN Veterinario v ON v.Pessoa_cpf = c.Veterinario_Pessoa_cpf " +
                "JOIN Pessoa pVet ON pVet.cpf = v.Pessoa_cpf " +
                "WHERE c.idConsulta = ?";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idConsulta);
            ResultSet rs = st.executeQuery();

            if (!rs.next()) {
                return null;
            }

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
            consulta.setTutorTelefone(rs.getString("tutorTelefone"));

            consulta.setVetNome(rs.getString("vetNome"));
            consulta.setVetCrmv(rs.getString("vetCrmv"));

            return consulta;
        }
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

    public static void updateDiagnosticoEPrescricao(Connection conn, int idConsulta, String diagnostico, String prescricao) throws SQLException {
        String sql = "UPDATE Consulta SET diagnostico = ?, prescricao = ? WHERE idConsulta = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, diagnostico);
            stmt.setString(2, prescricao);
            stmt.setInt(3, idConsulta);
            stmt.executeUpdate();
        }
    }

    public static boolean isPrimeiraConsulta(Connection conn, int animalId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM Consulta WHERE Animal_idAnimal = ?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, animalId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") == 1;
            }
        }
        return false;
    }
}







