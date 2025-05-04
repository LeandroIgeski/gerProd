package view;

import dao.Conexao;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

import view.TelaInicial;
import view.TelaCadastroUsuario;

public class TelaLogin extends JFrame {
    private JTextField campoMatricula;
    private JPasswordField campoSenha;

    public TelaLogin() {
        setTitle("Login - Sistema de Produção MetalForge");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new GridLayout(3, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        campoMatricula = new JTextField();
        campoSenha = new JPasswordField();

        painel.add(new JLabel("Matrícula:"));
        painel.add(campoMatricula);
        painel.add(new JLabel("Senha:"));
        painel.add(campoSenha);

        JButton botaoLogin = new JButton("Entrar");
        painel.add(new JLabel()); // espaço vazio
        painel.add(botaoLogin);

        add(painel);

        botaoLogin.addActionListener(e -> realizarLogin());

        // Tenta criar o usuário admin ao abrir a tela
        criarUsuarioAdmin();
    }

    private void criarUsuarioAdmin() {
        try (Connection conn = Conexao.conectar()) {
            if (conn != null) {
                String verifica = "SELECT COUNT(*) FROM usuarios WHERE matricula = 'admin'";
                PreparedStatement ps = conn.prepareStatement(verifica);
                ResultSet rs = ps.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    String insert = "INSERT INTO usuarios (matricula, nome, senha, funcao) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insert);
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, "Administrador");
                    insertStmt.setString(3, "admin");
                    insertStmt.setString(4, "Admin");
                    insertStmt.executeUpdate();
                    System.out.println("Usuário 'admin' criado automaticamente.");
                }
            } else {
                System.err.println("Conexão com banco de dados falhou.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar/verificar usuário admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void realizarLogin() {
        String matricula = campoMatricula.getText();
        String senha = new String(campoSenha.getPassword());

        try (Connection conn = Conexao.conectar()) {
            String sql = "SELECT * FROM usuarios WHERE matricula = ? AND senha = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, matricula);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
                String funcao = rs.getString("funcao");
                if ("Admin".equalsIgnoreCase(funcao)) {
                    new TelaCadastroUsuario().setVisible(true);
                } else {
                    new TelaInicial(funcao).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciais inválidas.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
