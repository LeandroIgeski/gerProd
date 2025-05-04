package dao;

import model.Recurso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    public List<Recurso> buscarTodos() {
        List<Recurso> lista = new ArrayList<>();
        String sql = "SELECT codigo, nome FROM recursos ORDER BY codigo";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nome = rs.getString("nome");
                lista.add(new Recurso(codigo, nome));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
