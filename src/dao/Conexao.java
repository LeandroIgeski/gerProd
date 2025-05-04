package dao;

import java.sql.*;

public class Conexao {
    private static final String URL_SEM_BANCO = "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String URL_COM_BANCO = "jdbc:mysql://localhost:3306/MetalForge?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "88845541leandroi";

    static {
        criarBancoSeNecessario();
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL_COM_BANCO, USUARIO, SENHA);
    }

    private static void criarBancoSeNecessario() {
        try (Connection conn = DriverManager.getConnection(URL_SEM_BANCO, USUARIO, SENHA);
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(false);

            ResultSet rs = stmt.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'MetalForge'");
            if (!rs.next()) {
                System.out.println("Banco 'MetalForge' nao encontrado. Criando...\n");

                stmt.executeUpdate("CREATE DATABASE MetalForge");

                // Cria tabelas principais
                stmt.executeUpdate("CREATE TABLE MetalForge.usuarios (" +
                        "matricula VARCHAR(20) PRIMARY KEY, " +
                        "nome VARCHAR(100), " +
                        "senha VARCHAR(100), " +
                        "funcao ENUM('Operador', 'Líder', 'Supervisor', 'Gerente', 'Diretor', 'Admin'))");

                // Inserir usuário admin padrão
                stmt.executeUpdate("INSERT INTO MetalForge.usuarios (matricula, nome, senha, funcao) " +
                        "VALUES ('admin', 'Administrador', 'admin', 'Admin')");

                stmt.executeUpdate("CREATE TABLE MetalForge.recursos (codigo VARCHAR(10) PRIMARY KEY, nome VARCHAR(100))");

                // Verifica se recursos já existem antes de inserir
                ResultSet rsRecursos = stmt.executeQuery("SELECT COUNT(*) FROM MetalForge.recursos");
                if (rsRecursos.next() && rsRecursos.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO MetalForge.recursos (codigo, nome) VALUES " +
                            "('1001001', 'Corte a Laser'), ('1002001', 'Solda Robô 1'), " +
                            "('1002002', 'Solda Robô 2'), ('1003001', 'Solda Manual'), " +
                            "('1004001', 'Pintura'), ('1005001', 'Montagem')");
                }

                stmt.executeUpdate("CREATE TABLE MetalForge.producao (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "recurso_codigo VARCHAR(10), " +
                        "op_numero VARCHAR(50), " +
                        "matricula VARCHAR(20), " +
                        "horario_inicio DATETIME, " +
                        "FOREIGN KEY (recurso_codigo) REFERENCES MetalForge.recursos(codigo), " +
                        "FOREIGN KEY (matricula) REFERENCES MetalForge.usuarios(matricula))");

                stmt.executeUpdate("CREATE TABLE MetalForge.reporte_producao (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "recurso_codigo VARCHAR(10), " +
                        "op_numero VARCHAR(50), " +
                        "quantidade INT, " +
                        "horario DATETIME, " +
                        "matricula_usuario VARCHAR(20), " +
                        "FOREIGN KEY (recurso_codigo) REFERENCES MetalForge.recursos(codigo), " +
                        "FOREIGN KEY (matricula_usuario) REFERENCES MetalForge.usuarios(matricula))");

                stmt.executeUpdate("CREATE TABLE MetalForge.disponibilidade (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "recurso_codigo VARCHAR(10) NOT NULL, " +
                        "status ENUM('Produção', 'Manutenção', 'Falta de consumível', 'Absenteísmo', 'Almoço', 'Setup'), " +
                        "comentarios TEXT, " +
                        "horario_inicio DATETIME, " +
                        "horario_fim DATETIME, " +
                        "FOREIGN KEY (recurso_codigo) REFERENCES MetalForge.recursos(codigo))");

                stmt.executeUpdate("CREATE TABLE MetalForge.performance (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "recurso_codigo VARCHAR(10) NOT NULL, " +
                        "op VARCHAR(50) NOT NULL, " +
                        "codigo_peca VARCHAR(50) NOT NULL, " +
                        "quantidade INT NOT NULL, " +
                        "horario_reportado DATETIME NOT NULL, " +
                        "FOREIGN KEY (recurso_codigo) REFERENCES MetalForge.recursos(codigo))");

                conn.commit();
                System.out.println("Banco de dados e tabelas criados com sucesso!\n");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao criar/verificar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
