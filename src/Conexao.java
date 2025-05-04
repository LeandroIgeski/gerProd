import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
        public static Connection conectar() {
            Connection conexao = null;

            String url = "jdbc:mysql://localhost:3306/metalforge";
            String usuario = "root";
            String senha = "88845541leandroi";

            try {
                conexao = DriverManager.getConnection(url, usuario, senha);
                System.out.println("Conexão realizada com sucesso!");
            } catch (SQLException e) {
                System.out.println("Erro na conexão: " + e.getMessage());
            }

            return conexao;
        }

        public static void main(String[] args) {
            conectar(); // Testa a conexão
        }
    }


