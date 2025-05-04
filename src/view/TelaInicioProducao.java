package view;

import dao.Conexao;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TelaInicioProducao extends JFrame {
    private JComboBox<String> cbRecurso;
    private JTextField txtNumeroOP;
    private JLabel lblHorario;
    private JButton btnEnviar, btnFechar;
    private Timer timer;
    private String tipoUsuario;  // campo para guardar o tipo do usuário

    // Construtor padrão, pode ser mantido se quiser
    public TelaInicioProducao() {
        this("default"); // chama o outro construtor com valor padrão
    }

    // Novo construtor que aceita o tipo de usuário
    public TelaInicioProducao(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;  // armazena o tipo do usuário

        setTitle("Início de Produção - Usuário: " + tipoUsuario);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        cbRecurso = new JComboBox<>(new String[]{
                "1001001 - Corte a Laser",
                "1002001 - Solda Robô 1",
                "1002002 - Solda Robô 2",
                "1003001 - Solda Manual",
                "1004001 - Pintura",
                "1005001 - Montagem"
        });

        txtNumeroOP = new JTextField();

        lblHorario = new JLabel();
        atualizarHorario();

        timer = new Timer(1000, e -> atualizarHorario());
        timer.start();

        btnEnviar = new JButton("Enviar");
        btnFechar = new JButton("Fechar");

        btnEnviar.addActionListener(e -> enviarProducao());
        btnFechar.addActionListener(e -> fecharTela());

        add(new JLabel("Selecione o Recurso:"));
        add(cbRecurso);
        add(new JLabel("Número da OP:"));
        add(txtNumeroOP);
        add(lblHorario);
        add(new JLabel()); // Espaço vazio
        add(btnEnviar);
        add(btnFechar);

        setVisible(true);
    }

    private void atualizarHorario() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lblHorario.setText("Horário: " + sdf.format(new Date()));
    }

    private void enviarProducao() {
        String recursoCodigo = cbRecurso.getSelectedItem().toString().split(" - ")[0];
        String numeroOP = txtNumeroOP.getText();
        Timestamp horarioAtual = new Timestamp(System.currentTimeMillis());

        // Agora você pode usar tipoUsuario ou transformar em matrícula do usuário
        String matriculaUsuario = "10804"; // aqui pode colocar lógica para pegar usuário logado baseado em tipoUsuario, por exemplo

        String sql = "INSERT INTO producao (recurso_codigo, op_numero, horario_inicio, matricula) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recursoCodigo);
            stmt.setString(2, numeroOP);
            stmt.setTimestamp(3, horarioAtual);
            stmt.setString(4, matriculaUsuario);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Início de produção registrado com sucesso!");
            fecharTela();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao registrar início de produção.");
        }
    }

    private void fecharTela() {
        dispose();
        new TelaInicial(tipoUsuario);  // Você também deve garantir que TelaInicial tenha esse construtor
    }
}
