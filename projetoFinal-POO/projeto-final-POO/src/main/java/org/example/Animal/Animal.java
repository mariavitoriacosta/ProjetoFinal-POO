package org.example.Animal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Animal {
    protected int id;
    protected String nome;
    protected int idade;
    protected String raca;

    public Animal(int id, String nome, int idade, String raca) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo.");
        }
        if (idade < 0) {
            throw new IllegalArgumentException("Idade não pode ser negativa.");
        }
        
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

    public static void deleteAnimal(Connection conn, int idAnimal) throws SQLException {
        if (conn == null) {
            throw new SQLException("Conexão nula.");
        }
        
        String sql = "DELETE FROM Animal WHERE idAnimal = ?";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idAnimal);
            
            st.executeUpdate();

        } catch (SQLException entrada) {
            System.err.println("Erro ao executar exclusão SQL: " + entrada.getMessage());
            throw entrada; 
        }
    }
}