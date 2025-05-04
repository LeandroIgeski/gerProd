import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TelaStatusRecurso extends JFrame {
    private JComboBox<String> cbRecurso, cbStatus;
    private JTextArea txtComentarios;
    private JLabel lblHorario;
    private JButton btnEnviar, btnFechar;
    private Timer timer;

    public TelaStatusRecurso() {
        setTitle("Status de Recurso");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 10, 10));

        cbRecurso = new JComboBox<>(new String[]{
                "1001001 - Corte a Laser",
                "1002001 - Solda Robô 1",
                "1002002 - Solda Robô 2",
                "1003001 - Solda Manual",
                "1004001 - Pintura",
                "1005001 - Montagem"
        });

        cbStatus = new JComboBox<>(new String[]{
                "Produção", "Manutenção", "Falta de consumível", "Absenteísmo", "Almoço", "Setup"
        });

        txtComentarios = new JTextArea(3, 20);
        JScrollPane scrollComentarios = new JScrollPane(txtComentarios);

        lblHorario = new JLabel();
        atualizarHorario();
        timer = new Timer(1000, e -> atualizarHorario());
        timer.start();

        btnEnviar = new JButton("Enviar");
        btnEnviar.setBackground(new Color(0, 153, 0));
        btnEnviar.setForeground(Color.WHITE);

        btnFechar = new JButton("Fechar");
        btnFechar.setBackground(new Color(204, 0, 0));
        btnFechar.setForeground(Color.WHITE);

        btnEnviar.addActionListener(e -> enviarStatus());
        btnFechar.addActionListener(e -> fecharTela());

        add(new JLabel("Selecione o Recurso:"));
        add(cbRecurso);
        add(new JLabel("Selecione o Status:"));
        add(cbStatus);
        add(new JLabel("Comentários:"));
        add(scrollComentarios);
        add(lblHorario);
        add(new JLabel());
        add(btnEnviar);
        add(btnFechar);

        setVisible(true);
    }

    private void atualizarHorario() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        lblHorario.setText("Horário: " + sdf.format(new Date()));
    }

    private void enviarStatus() {
        String recursoCodigo = cbRecurso.getSelectedItem().toString().split(" - ")[0];
        String status = cbStatus.getSelectedItem().toString();
        String comentarios = txtComentarios.getText();
        Timestamp horarioAtual = new Timestamp(System.currentTimeMillis());

        String tabela = "disponibilidade_" + recursoCodigo;

        try (Connection conn = Conexao.conectar()) {

            // 1. Finaliza o último status aberto (define o horario_fim)
            String sqlUpdateAnterior = "UPDATE " + tabela + " SET horario_fim = ? WHERE horario_fim IS NULL";
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateAnterior)) {
                stmtUpdate.setTimestamp(1, horarioAtual);
                stmtUpdate.executeUpdate();
            }

            // 2. Insere o novo status com horario_fim NULL (representa status atual)
            String sqlInsertNovo = "INSERT INTO " + tabela +
                    " (status, comentarios, horario_inicio, horario_fim) VALUES (?, ?, ?, NULL)";
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertNovo)) {
                stmtInsert.setString(1, status);
                stmtInsert.setString(2, comentarios);
                stmtInsert.setTimestamp(3, horarioAtual);
                stmtInsert.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Status registrado com sucesso!");
            fecharTela();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao registrar status no banco de dados.");
        }
    }

    private void fecharTela() {
        dispose();
        new TelaInicial(); // Volta à tela inicial
    }

    public static void main(String[] args) {
        new TelaStatusRecurso();
    }
}
