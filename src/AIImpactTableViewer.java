import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
            rowData[i][1] = Integer.valueOf(d.getYear());
            rowData[i][2] = d.getIndustry();
            rowData[i][3] = Double.valueOf(d.getAiAdoptionRate());
            rowData[i][4] = Double.valueOf(d.getContentVolume());
            rowData[i][5] = Double.valueOf(d.getJobLossRate());
            rowData[i][6] = Double.valueOf(d.getRevenueIncrease());
            rowData[i][7] = Double.valueOf(d.getCollaborationRate());
            rowData[i][8] = d.getTopAIToolsUsed();
            rowData[i][9] = d.getRegulationStatus();
            rowData[i][10] = Double.valueOf(d.getConsumerTrust());
            rowData[i][11] = Double.valueOf(d.getMarketShare());
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

        // ✅ Tri multiple par défaut
        List<SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new SortKey(1, SortOrder.DESCENDING));
        sortKeys.add(new SortKey(3, SortOrder.DESCENDING));
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
                List<SortKey> keys = new ArrayList<>();

                for (String part : parts) {
                    String[] split = part.split(":");
                    try {
                        int columnIndex = Integer.parseInt(split[0].trim()) - 1; // 🟢 Corrigé ici
                        if (columnIndex < 0 || columnIndex >= table.getColumnCount()) {
                            System.err.println("Numéro de colonne invalide : " + (columnIndex + 1));
                            continue;
                        }

                        SortOrder order = SortOrder.ASCENDING;
                        if (split.length == 2) {
                            String direction = split[1].trim().toLowerCase();
                            if (direction.equals("desc")) order = SortOrder.DESCENDING;
                        }

                        keys.add(new SortKey(columnIndex, order));
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
