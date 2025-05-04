package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.util.Rotation;

public class TelaRelatorio {

    private Connection conexao;

    public TelaRelatorio(Connection conexao) {
        this.conexao = conexao;
    }

    public void gerarRelatorioCompleto(int idRecurso, String caminhoSaidaPdf) {
        try {
            Document document = new Document(PageSize.A4.rotate()); // Folha horizontal
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(caminhoSaidaPdf));
            document.open();

            document.add(new Paragraph("Relatório Completo - Recurso ID: " + idRecurso,
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD)));
            document.add(new Paragraph(" "));

            BufferedImage imgProducao = gerarGraficoProducaoDiaria(idRecurso);
            com.itextpdf.text.Image graficoProducao = com.itextpdf.text.Image.getInstance(writer, imgProducao, 1.0f);
            document.add(new Paragraph("Produção Diária"));
            document.add(graficoProducao);
            document.add(new Paragraph(" "));

            BufferedImage imgStatus = gerarGraficoDuracaoStatus(idRecurso);
            com.itextpdf.text.Image graficoStatus = com.itextpdf.text.Image.getInstance(writer, imgStatus, 1.0f);
            document.add(new Paragraph("Duração dos Status"));
            document.add(graficoStatus);
            document.add(new Paragraph(" "));

            BufferedImage imgParadas = gerarGraficoParadas(idRecurso);
            com.itextpdf.text.Image graficoParadas = com.itextpdf.text.Image.getInstance(writer, imgParadas, 1.0f);
            document.add(new Paragraph("Paradas da Máquina"));
            document.add(graficoParadas);

            document.close();
            System.out.println("Relatório gerado com sucesso: " + caminhoSaidaPdf);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage gerarGraficoProducaoDiaria(int idRecurso) throws Exception {
        String sql = "SELECT DATE(horario_inicio) AS dia, COUNT(*) AS total_pecas " +
                "FROM producao WHERE recurso_codigo = ? GROUP BY DATE(horario_inicio) ORDER BY dia";
        PreparedStatement ps = conexao.prepareStatement(sql);
        ps.setInt(1, idRecurso);
        ResultSet rs = ps.executeQuery();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        while (rs.next()) {
            String dia = rs.getString("dia");
            int totalPecas = rs.getInt("total_pecas");
            dataset.addValue(totalPecas, "Produção", dia);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Produção Diária",
                "Data",
                "Quantidade de Peças",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new GradientPaint(0.0f, 0.0f, Color.BLUE,
                0.0f, 0.0f, Color.CYAN));
        renderer.setMaximumBarWidth(0.1);
        plot.setRenderer(renderer);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6));
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        return chart.createBufferedImage(800, 500);
    }

    private BufferedImage gerarGraficoDuracaoStatus(int idRecurso) throws Exception {
        String sql = "SELECT status, SUM(TIMESTAMPDIFF(MINUTE, horario_inicio, horario_fim)) AS duracao_minutos " +
                "FROM disponibilidade WHERE recurso_codigo = ? GROUP BY status";

        PreparedStatement ps = conexao.prepareStatement(sql);
        ps.setInt(1, idRecurso);
        ResultSet rs = ps.executeQuery();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        while (rs.next()) {
            String status = rs.getString("status");
            int duracao = rs.getInt("duracao_minutos");
            dataset.addValue(duracao, "Duração", status);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Duração dos Status",
                "Status",
                "Tempo (minutos)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new GradientPaint(0.0f, 0.0f, Color.RED,
                0.0f, 0.0f, Color.ORANGE));
        plot.setRenderer(renderer);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6));
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        return chart.createBufferedImage(800, 500);
    }

    private BufferedImage gerarGraficoParadas(int idRecurso) throws Exception {
        String sql = "SELECT status, COUNT(*) AS total " +
                "FROM disponibilidade " +
                "WHERE recurso_codigo = ? AND status IN ('Manutenção', 'Falta de consumível', 'Absenteísmo', 'Almoço', 'Setup') " +
                "GROUP BY status";

        PreparedStatement ps = conexao.prepareStatement(sql);
        ps.setInt(1, idRecurso);
        ResultSet rs = ps.executeQuery();

        DefaultPieDataset dataset = new DefaultPieDataset();
        while (rs.next()) {
            String status = rs.getString("status");
            int total = rs.getInt("total");
            dataset.setValue(status, total);
        }

        JFreeChart chart = ChartFactory.createPieChart3D(
                "Paradas da Máquina (por tipo de status)",
                dataset,
                true, true, false);

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.7f);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("Sem dados");
        plot.setExplodePercent("Manutenção", 0.08);

        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        return chart.createBufferedImage(800, 500);
    }
}
