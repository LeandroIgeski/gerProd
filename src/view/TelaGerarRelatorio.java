package view;

import dao.Conexao;
import model.TelaRelatorio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class TelaGerarRelatorio extends JFrame {

    private JComboBox<String> comboRecursos;
    private JButton btnGerarPdf, btnFechar;
    private String tipoUsuario;
    private List<Recurso> recursos;

    public TelaGerarRelatorio(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
        setTitle("Gerar Relatório");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                voltarTelaInicial();
            }
        });

        recursos = carregarRecursos();

        comboRecursos = new JComboBox<>();
        for (Recurso r : recursos) {
            comboRecursos.addItem(r.getNome());
        }

        btnGerarPdf = new JButton("Gerar PDF");
        btnGerarPdf.addActionListener(e -> gerarPdf());

        btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> voltarTelaInicial());

        JPanel painelCentro = new JPanel(new GridLayout(2, 1, 5, 5));
        painelCentro.add(new JLabel("Selecione o recurso:"));
        painelCentro.add(comboRecursos);

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnGerarPdf);
        painelBotoes.add(btnFechar);

        add(painelCentro, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        setVisible(true);
    }
    private void voltarTelaInicial() {
        new TelaInicial(tipoUsuario).setVisible(true);
        dispose();
    }

    private List<Recurso> carregarRecursos() {
        List<Recurso> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             var ps = conn.prepareStatement("SELECT codigo, nome FROM recursos");
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Recurso(rs.getString("codigo"), rs.getString("nome")));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar recursos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private void gerarPdf() {
        int idx = comboRecursos.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um recurso!");
            return;
        }

        Recurso recursoSelecionado = recursos.get(idx);

        try (Connection conn = Conexao.conectar()) {
            TelaRelatorio relatorio = new TelaRelatorio(conn);

            // Ajuste do caminho para Windows com pasta existente
            String caminhoPasta = System.getProperty("user.home") + "\\OneDrive\\Área de Trabalho";
            java.io.File pasta = new java.io.File(caminhoPasta);
            if (!pasta.exists()) {
                pasta.mkdirs(); // cria a pasta caso não exista
            }

            String caminhoPdf = caminhoPasta + "\\relatorio_" + recursoSelecionado.getCodigo() + ".pdf";

            // Chamada ao método que gera o relatório completo
            relatorio.gerarRelatorioCompleto(Integer.parseInt(recursoSelecionado.getCodigo()), caminhoPdf);

            JOptionPane.showMessageDialog(this, "Relatório gerado:\n" + caminhoPdf);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage());
        }
    }


    private static class Recurso {
        private String codigo;
        private String nome;

        public Recurso(String codigo, String nome) {
            this.codigo = codigo;
            this.nome = nome;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getNome() {
            return nome;
        }
    }
}
