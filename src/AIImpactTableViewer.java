import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import org.jfree.chart.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class AIImpactTableViewer {

    public void showTable(List<AIImpactData> dataList) {
        String[] columnNames = {
                "Country", "Year", "Industry", "AI Adoption (%)", "Content Volume (TB)",
                "Job Loss (%)", "Revenue Increase (%)", "Collab. Rate (%)",
                "Top AI Tool", "Regulation", "Trust (%)", "Market Share (%)"
        };

        Object[][] rowData = new Object[dataList.size()][columnNames.length];

        for (int i = 0; i < dataList.size(); i++) {
            AIImpactData d = dataList.get(i);
            rowData[i][0] = d.getCountry();
            rowData[i][1] = d.getYear();
            rowData[i][2] = d.getIndustry();
            rowData[i][3] = d.getAiAdoptionRate();
            rowData[i][4] = d.getContentVolume();
            rowData[i][5] = d.getJobLossRate();
            rowData[i][6] = d.getRevenueIncrease();
            rowData[i][7] = d.getCollaborationRate();
            rowData[i][8] = d.getTopAIToolsUsed();
            rowData[i][9] = d.getRegulationStatus();
            rowData[i][10] = d.getConsumerTrust();
            rowData[i][11] = d.getMarketShare();
        }

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 2, 8, 9 -> String.class;
                    case 1 -> Integer.class;
                    default -> Double.class;
                };
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer decimalRenderer = new DefaultTableCellRenderer() {
            final DecimalFormat df = new DecimalFormat("0.00");

            @Override
            public void setValue(Object value) {
                if (value instanceof Number) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setText(df.format(value));
                } else {
                    super.setValue(value);
                }
            }
        };

        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        int[] numericCols = {3, 4, 5, 6, 7, 10, 11};
        for (int col : numericCols) {
            table.getColumnModel().getColumn(col).setCellRenderer(decimalRenderer);
        }

        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(70, 70, 70));
        table.setSelectionBackground(new Color(60, 60, 100));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(24);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(50, 50, 50));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));

        JTextField sortField = new JTextField();
        sortField.setBackground(new Color(40, 40, 40));
        sortField.setForeground(Color.WHITE);
        sortField.setCaretColor(Color.WHITE);
        sortField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sortField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Trier par colonnes (ex: 3,1 ou 3:desc,1:asc)", 0, 0,
                new Font("SansSerif", Font.BOLD, 12), Color.LIGHT_GRAY
        ));

        sortField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateSort() {
                String input = sortField.getText().trim();
                if (input.isEmpty()) {
                    sorter.setSortKeys(null);
                    return;
                }

                String[] parts = input.split(",");
                List<RowSorter.SortKey> keys = new ArrayList<>();

                for (String part : parts) {
                    String[] split = part.split(":");
                    try {
                        int columnIndex = Integer.parseInt(split[0].trim()) - 1;
                        if (columnIndex < 0 || columnIndex >= table.getColumnCount()) continue;

                        SortOrder order = SortOrder.ASCENDING;
                        if (split.length == 2 && split[1].trim().equalsIgnoreCase("desc")) {
                            order = SortOrder.DESCENDING;
                        }

                        keys.add(new RowSorter.SortKey(columnIndex, order));
                    } catch (NumberFormatException ignored) {}
                }

                sorter.setSortKeys(keys);
                sorter.sort();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSort(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSort(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSort(); }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(20, 20, 20));
        topPanel.add(sortField, BorderLayout.CENTER);

        JPanel graphControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        graphControlPanel.setBackground(new Color(20, 20, 20));

        String[] fields = {
                "Job Loss (%)", "AI Adoption (%)", "Revenue Increase (%)",
                "Collab. Rate (%)", "Trust (%)", "Market Share (%)",
                "Content Volume (TB)"
        };

        JComboBox<String> fieldSelector = new JComboBox<>(fields);
        fieldSelector.setBackground(new Color(50, 50, 50));
        fieldSelector.setForeground(Color.WHITE);
        fieldSelector.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton showChartButton = new JButton("Afficher");
        showChartButton.setFocusPainted(false);
        showChartButton.setBackground(new Color(60, 60, 60));
        showChartButton.setForeground(Color.WHITE);
        showChartButton.setFont(new Font("SansSerif", Font.BOLD, 13));

        showChartButton.addActionListener(e -> {
            String selectedField = (String) fieldSelector.getSelectedItem();
            showBarChartByField(dataList, selectedField);
        });

        graphControlPanel.add(new JLabel("Critère du graphique :"));
        graphControlPanel.add(fieldSelector);
        graphControlPanel.add(showChartButton);

        topPanel.add(graphControlPanel, BorderLayout.SOUTH);

        JFrame frame = new JFrame("AI Impact Data Viewer - Dark Mode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(20, 20, 20));
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(20, 20, 20));

        Set<String> industries = new TreeSet<>();
        for (AIImpactData data : dataList) {
            industries.add(data.getIndustry());
        }

        JComboBox<String> industrySelector = new JComboBox<>(industries.toArray(new String[0]));
        industrySelector.setBackground(new Color(50, 50, 50));
        industrySelector.setForeground(Color.WHITE);
        industrySelector.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JComboBox<String> critereSelector = new JComboBox<>(fields);
        critereSelector.setBackground(new Color(50, 50, 50));
        critereSelector.setForeground(Color.WHITE);
        critereSelector.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton showFilteredChartButton = new JButton("Afficher graphique filtré");
        showFilteredChartButton.setFocusPainted(false);
        showFilteredChartButton.setBackground(new Color(60, 60, 60));
        showFilteredChartButton.setForeground(Color.WHITE);
        showFilteredChartButton.setFont(new Font("SansSerif", Font.BOLD, 13));

        showFilteredChartButton.addActionListener(e -> {
            String industry = (String) industrySelector.getSelectedItem();
            String critere = (String) critereSelector.getSelectedItem();
            showIndustryFilteredChart(dataList, industry, critere);
        });

        filterPanel.add(new JLabel("Industrie :"));
        filterPanel.add(industrySelector);
        filterPanel.add(new JLabel("Critère :"));
        filterPanel.add(critereSelector);
        filterPanel.add(showFilteredChartButton);

        frame.add(filterPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    public void showBarChartByField(List<AIImpactData> dataList, String fieldLabel) {
        Map<String, List<Double>> valuesByCountry = new HashMap<>();

        for (AIImpactData data : dataList) {
            double value;
            switch (fieldLabel) {
                case "Job Loss (%)": value = data.getJobLossRate(); break;
                case "AI Adoption (%)": value = data.getAiAdoptionRate(); break;
                case "Revenue Increase (%)": value = data.getRevenueIncrease(); break;
                case "Collab. Rate (%)": value = data.getCollaborationRate(); break;
                case "Trust (%)": value = data.getConsumerTrust(); break;
                case "Market Share (%)": value = data.getMarketShare(); break;
                case "Content Volume (TB)": value = data.getContentVolume(); break;
                default: continue;
            }

            valuesByCountry.computeIfAbsent(data.getCountry(), k -> new ArrayList<>()).add(value);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, List<Double>> entry : valuesByCountry.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            dataset.addValue(avg, fieldLabel, entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Moyenne de " + fieldLabel + " par Pays",
                "Pays",
                fieldLabel,
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        JFrame chartFrame = new JFrame("Graphique - " + fieldLabel);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(800, 500);
        chartFrame.add(chartPanel);
        chartFrame.setVisible(true);
    }

    public void showIndustryFilteredChart(List<AIImpactData> dataList, String industryFilter, String critereLabel) {
        Map<String, List<Double>> valuesByCountry = new HashMap<>();

        for (AIImpactData data : dataList) {
            if (!data.getIndustry().equalsIgnoreCase(industryFilter)) continue;

            double value;
            switch (critereLabel) {
                case "Job Loss (%)": value = data.getJobLossRate(); break;
                case "AI Adoption (%)": value = data.getAiAdoptionRate(); break;
                case "Revenue Increase (%)": value = data.getRevenueIncrease(); break;
                case "Collab. Rate (%)": value = data.getCollaborationRate(); break;
                case "Trust (%)": value = data.getConsumerTrust(); break;
                case "Market Share (%)": value = data.getMarketShare(); break;
                case "Content Volume (TB)": value = data.getContentVolume(); break;
                default: continue;
            }

            valuesByCountry.computeIfAbsent(data.getCountry(), k -> new ArrayList<>()).add(value);
        }

        if (valuesByCountry.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Aucune donnée trouvée pour l'industrie : " + industryFilter,
                    "Alerte", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, List<Double>> entry : valuesByCountry.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            dataset.addValue(avg, critereLabel, entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                critereLabel + " - Industrie : " + industryFilter,
                "Pays",
                critereLabel,
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame("Graphique filtré - " + industryFilter);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(800, 500);
        chartFrame.add(chartPanel);
        chartFrame.setVisible(true);
    }
}