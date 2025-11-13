package Animal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Animal {
    protected int id;
    protected String nome;
    protected int idade;
    protected String raca;

    public Animal(int id, String nome, int idade, String raca) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.raca = raca;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getIdade() {
        return idade;
    }

    public String getRaca() {
        return raca;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static boolean deleteAnimal(Connection conn, int idAnimal) throws SQLException {
        String sql = "DELETE FROM Animal WHERE idAnimal = ?";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idAnimal);
            int rows = st.executeUpdate();
            return rows > 0; // retorna true se deletou
        }
    }

}

