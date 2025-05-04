import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TelaLogin extends JFrame {
    private JTextField txtMatricula;
    private JPasswordField txtSenha;

    public TelaLogin() {
        setTitle("MetalForge - Login");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Matrícula:"));
        txtMatricula = new JTextField();
        panel.add(txtMatricula);

        panel.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        panel.add(txtSenha);

        JButton btnEntrar = new JButton("Entrar");
        JButton btnSair = new JButton("Sair");

        panel.add(btnEntrar);
        panel.add(btnSair);

        add(panel, BorderLayout.CENTER);

        btnEntrar.addActionListener(e -> realizarLogin());
        btnSair.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void realizarLogin() {
        String matricula = txtMatricula.getText();
        String senha = new String(txtSenha.getPassword());

        if (matricula.equals("adm") && senha.equals("adm123")) {
            JOptionPane.showMessageDialog(this, "Login como administrador bem-sucedido.");
            new TelaInicial(); // Redireciona para a Tela Inicial
            dispose();
        } else {
            try (Connection conn = Conexao.conectar()) {
                String sql = "SELECT * FROM usuarios WHERE matricula=? AND senha=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, matricula);
                stmt.setString(2, senha);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String funcao = rs.getString("funcao");
                    JOptionPane.showMessageDialog(this, "Login bem-sucedido como " + funcao);
                    new TelaInicial(); // Redireciona para a Tela Inicial após login bem-sucedido
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Matrícula ou senha inválidos.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro de conexão com o banco de dados.");
            }
        }
    }

    public static void main(String[] args) {
        new TelaLogin();
    }
}
