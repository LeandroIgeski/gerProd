package dao;

public class TesteConexao {
    public static void main(String[] args) {
        try {
            java.sql.Connection conn = Conexao.conectar();
            if (conn != null) {
                System.out.println("Conexão com o banco 'MetalForge' estabelecida com sucesso!");
                conn.close();
            } else {
                System.out.println("Falha ao conectar.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
