package view;

import dao.Conexao;
import view.TelaLogin;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TelaCadastroUsuario extends JFrame {
    private JTextField campoMatricula, campoNome;
    private JPasswordField campoSenha;
    private JComboBox<String> comboFuncao;

    public TelaCadastroUsuario() {
        setTitle("Cadastro de Usuário");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new GridLayout(5, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        campoMatricula = new JTextField();
        campoNome = new JTextField();
        campoSenha = new JPasswordField();
        comboFuncao = new JComboBox<>(new String[] {
                "Operador", "Líder", "Supervisor", "Gerente", "Diretor", "Admin"
        });

        painel.add(new JLabel("Matrícula:"));
        painel.add(campoMatricula);
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("Senha:"));
        painel.add(campoSenha);
        painel.add(new JLabel("Função:"));
        painel.add(comboFuncao);

        JButton botaoSalvar = new JButton("Salvar");
        painel.add(new JLabel()); // espaço vazio
        painel.add(botaoSalvar);

        add(painel);

        botaoSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarUsuario();
            }
        });
    }

    private void salvarUsuario() {
        String matricula = campoMatricula.getText();
        String nome = campoNome.getText();
        String senha = new String(campoSenha.getPassword());
        String funcao = (String) comboFuncao.getSelectedItem();

        if (matricula.isEmpty() || nome.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try (Connection conn = Conexao.conectar()) {
            String sql = "INSERT INTO usuarios (matricula, nome, senha, funcao) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, matricula);
            stmt.setString(2, nome);
            stmt.setString(3, senha);
            stmt.setString(4, funcao);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            new TelaLogin().setVisible(true);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
