import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.*;

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
            public Class getColumnClass(int columnIndex) {
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

        //Centrer les choses dans les colonnes
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

        // Centrer Year
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Colonnes numériques alignées à droite avec 2 décimales
        int[] numericCols = {3, 4, 5, 6, 7, 10, 11};
        for (int col : numericCols) {
            table.getColumnModel().getColumn(col).setCellRenderer(decimalRenderer);
        }

        // 🎨 Thème sombre
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

        // Tri multiple par défaut
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));

        // 🔢 Zone de tri dynamique
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
                        if (columnIndex < 0 || columnIndex >= table.getColumnCount()) {
                            System.err.println("Numéro de colonne invalide : " + (columnIndex + 1));
                            continue;
                        }

                        SortOrder order = SortOrder.ASCENDING;
                        if (split.length == 2) {
                            String direction = split[1].trim().toLowerCase();
                            if (direction.equals("desc")) order = SortOrder.DESCENDING;
                        }

                        keys.add(new RowSorter.SortKey(columnIndex, order));
                    } catch (NumberFormatException e) {
                        System.err.println("Entrée invalide pour le tri : " + part);
                    }
                }

                sorter.setSortKeys(keys);
                sorter.sort();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSort(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSort(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSort(); }
        });

        // 🧩 Panel du haut contenant le champ de tri
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(20, 20, 20));
        topPanel.add(sortField, BorderLayout.CENTER);

        // 💡 Fenêtre principale
        JFrame frame = new JFrame("AI Impact Data Viewer - Dark Mode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 650);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(20, 20, 20));
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
