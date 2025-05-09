import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jfree.chart.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class AIImpactTableViewer  {
    // Méthode pour afficher un nuage de points (scatter plot)
    public void showScatterPlot(List<AIImpactData> d, String xCrit, String yCrit) {
        // Création d'une série pour les données du graphique
        XYSeries series = new XYSeries(xCrit + " vs " + yCrit);

        // Parcours des données pour ajouter les points au graphique
        for (AIImpactData x : d) {
            double xVal = switch (xCrit) {
                case "Year" -> x.getYear();
                case "Job Loss (%)" -> x.getJobLossRate();
                case "AI Adoption (%)" -> x.getAiAdoptionRate();
                case "Revenue Increase (%)" -> x.getRevenueIncrease();
                case "Collab. Rate (%)" -> x.getCollaborationRate();
                case "Trust (%)" -> x.getConsumerTrust();
                case "Market Share (%)" -> x.getMarketShare();
                case "Content Volume (TB)" -> x.getContentVolume();
                default -> -1;
            };

            double yVal = switch (yCrit) {
                case "Job Loss (%)" -> x.getJobLossRate();
                case "AI Adoption (%)" -> x.getAiAdoptionRate();
                case "Revenue Increase (%)" -> x.getRevenueIncrease();
                case "Collab. Rate (%)" -> x.getCollaborationRate();
                case "Trust (%)" -> x.getConsumerTrust();
                case "Market Share (%)" -> x.getMarketShare();
                case "Content Volume (TB)" -> x.getContentVolume();
                default -> -1;
            };

            // Ajout du point si les valeurs sont valides
            if (xVal >= 0 && yVal >= 0) {
                series.add(xVal, yVal);
            }
        }

        // Création du dataset et du graphique
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createScatterPlot(
                yCrit + " en fonction de " + xCrit,
                xCrit,
                yCrit,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // Affichage du graphique
        JFrame fr = new JFrame("Nuage de points");
        fr.setSize(800, 500);
        fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fr.add(new ChartPanel(chart));
        fr.setVisible(true);
    }

    // Méthode pour afficher la table des données
    public void showTable(List<AIImpactData> d) {
        // Définition des colonnes de la table
        String[] cols = {
                "Country", "Year", "Industry", "AI Adoption (%)", "Content Volume (TB)",
                "Job Loss (%)", "Revenue Increase (%)", "Collab. Rate (%)",
                "Top AI Tool", "Regulation", "Trust (%)", "Market Share (%)"
        };

        // Définition des critères de filtrage pour les graphiques
        String[] fields = {
                "Job Loss (%)", "AI Adoption (%)", "Revenue Increase (%)",
                "Collab. Rate (%)", "Trust (%)", "Market Share (%)", "Content Volume (TB)", "Year"
        };

        // Remplir les données de la table
        Object[][] data = new Object[d.size()][cols.length];
        for (int i = 0; i < d.size(); i++) {
            AIImpactData x = d.get(i);
            data[i][0] = x.getCountry(); data[i][1] = x.getYear(); data[i][2] = x.getIndustry();
            data[i][3] = x.getAiAdoptionRate(); data[i][4] = x.getContentVolume();
            data[i][5] = x.getJobLossRate(); data[i][6] = x.getRevenueIncrease();
            data[i][7] = x.getCollaborationRate(); data[i][8] = x.getTopAIToolsUsed();
            data[i][9] = x.getRegulationStatus(); data[i][10] = x.getConsumerTrust();
            data[i][11] = x.getMarketShare();
        }

        // Création du modèle pour la table avec des colonnes non éditables
        DefaultTableModel m = new DefaultTableModel(data, cols) {
            public Class getColumnClass(int c) {
                if (c == 0 || c == 2 || c == 8 || c == 9) return String.class;
                if (c == 1) return Integer.class;
                return Double.class;
            }
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // Configuration de la table
        JTable t = new JTable(m);
        t.setBackground(new Color(30, 30, 30));
        t.setForeground(Color.WHITE);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setRowHeight(24);
        t.setGridColor(new Color(70, 70, 70));
        t.setSelectionBackground(new Color(60, 60, 100));
        t.setSelectionForeground(Color.WHITE);

        // Configuration de l'entête de la table
        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(50, 50, 50));
        h.setForeground(Color.WHITE);
        h.setFont(new Font("SansSerif", Font.BOLD, 13));

        // Alignement des cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Tri des données par colonne
        TableRowSorter<DefaultTableModel> s = new TableRowSorter<>(m);
        t.setRowSorter(s);

        // Ajout de la table dans un JScrollPane pour le défilement
        JScrollPane sp = new JScrollPane(t);
        sp.getViewport().setBackground(new Color(30, 30, 30));

        // Champ de texte pour le tri par colonnes
        JTextField f1 = new JTextField();
        f1.setBackground(new Color(40, 40, 40));
        f1.setForeground(Color.WHITE);
        f1.setCaretColor(Color.WHITE);
        f1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Trier par colonnes (ex: 3,1 ou 3:desc,1:asc)", 0, 0,
                new Font("SansSerif", Font.BOLD, 12), Color.LIGHT_GRAY));

        // Ajout d'un écouteur pour le champ de texte de tri
        f1.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                String txt = f1.getText().trim();
                if (txt.isEmpty()) {
                    s.setSortKeys(null); return;
                }

                String[] ps = txt.split(",");
                List<RowSorter.SortKey> keys = new ArrayList<>();
                for (String p : ps) {
                    String[] sp = p.split(":");
                    try {
                        int col = Integer.parseInt(sp[0].trim()) - 1;
                        if (col < 0 || col >= t.getColumnCount()) continue;
                        SortOrder o = SortOrder.ASCENDING;
                        if (sp.length == 2 && sp[1].trim().equalsIgnoreCase("desc")) o = SortOrder.DESCENDING;
                        keys.add(new RowSorter.SortKey(col, o));
                    } catch (Exception e) {}
                }
                s.setSortKeys(keys);
                s.sort();
            }

            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        // Panneau pour afficher les filtres de sélection
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(20, 20, 20));
        top.add(f1, BorderLayout.CENTER);

        // Panneau de filtres en bas de la fenêtre
        JPanel filt = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filt.setBackground(new Color(20, 20, 20));

        // Création des combobox pour les filtres
        Set<String> inds = new TreeSet<>();
        Set<String> countries = new TreeSet<>();
        Set<Integer> years = new TreeSet<>();
        for (AIImpactData row : d) {
            inds.add(row.getIndustry());
            countries.add(row.getCountry());
            years.add(row.getYear());
        }

        JComboBox<String> indBox = new JComboBox<>(inds.toArray(new String[0]));
        JComboBox<String> countryBox = new JComboBox<>();
        countryBox.addItem("Tous les pays");
        for (String c : countries) countryBox.addItem(c);

        JComboBox<String> yearBox = new JComboBox<>();
        yearBox.addItem("Toutes les années");
        for (Integer y : years) yearBox.addItem(String.valueOf(y));

        JComboBox<String> chartTypeBox = new JComboBox<>(new String[]{"Barres", "Circulaire", "Nuage de points", "Courbe"});
        JComboBox<String> xAxisBox = new JComboBox<>(fields);
        JComboBox<String> yAxisBox = new JComboBox<>(fields);

        indBox.setBackground(new Color(50,50,50));
        indBox.setForeground(Color.WHITE);
        indBox.setFont(new Font("SansSerif", Font.PLAIN, 13));

        countryBox.setBackground(new Color(50,50,50));
        countryBox.setForeground(Color.WHITE);
        countryBox.setFont(new Font("SansSerif", Font.PLAIN, 13));

        yearBox.setBackground(new Color(50,50,50));
        yearBox.setForeground(Color.WHITE);
        yearBox.setFont(new Font("SansSerif", Font.PLAIN, 13));

        chartTypeBox.setBackground(new Color(50,50,50));
        chartTypeBox.setForeground(Color.WHITE);
        chartTypeBox.setFont(new Font("SansSerif", Font.PLAIN, 13));

        xAxisBox.setBackground(new Color(50,50,50));
        xAxisBox.setForeground(Color.WHITE);
        xAxisBox.setFont(new Font("SansSerif", Font.PLAIN, 13));

        yAxisBox.setBackground(new Color(50,50,50));
        yAxisBox.setForeground(Color.WHITE);
        yAxisBox.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton btn = new JButton("Afficher graphique filtré");
        btn.setBackground(new Color(60,60,60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));

        btn.addActionListener(e -> {
            String industry = (String) indBox.getSelectedItem();
            String country = (String) countryBox.getSelectedItem();
            String yearStr = (String) yearBox.getSelectedItem();
            String type = (String) chartTypeBox.getSelectedItem();
            String xCrit = (String) xAxisBox.getSelectedItem();
            String yCrit = (String) yAxisBox.getSelectedItem();

            List<AIImpactData> filtered = new ArrayList<>();
            for (AIImpactData x : d) {
                boolean matchIndustry = x.getIndustry().equalsIgnoreCase(industry);
                boolean matchCountry = country.equals("Tous les pays") || x.getCountry().equalsIgnoreCase(country);
                boolean matchYear = yearStr.equals("Toutes les années") || x.getYear() == Integer.parseInt(yearStr);
                if (matchIndustry && matchCountry && matchYear) {
                    filtered.add(x);
                }
            }

            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Aucune donnée trouvée pour les filtres sélectionnés.");
                return;
            }

            switch (type) {
                case "Circulaire" -> showPieChartByField(filtered, yCrit);
                case "Barres" -> showBarChartByField(filtered, yCrit);
                case "Nuage de points" -> showScatterPlot(filtered, xCrit, yCrit);
                case "Courbe" -> showLineChart(filtered, yCrit);
            }
        });

        filt.add(new JLabel("Industrie :"));
        filt.add(indBox);
        filt.add(new JLabel("Pays :"));
        filt.add(countryBox);
        filt.add(new JLabel("Année :"));
        filt.add(yearBox);
        filt.add(new JLabel("Type :"));
        filt.add(chartTypeBox);
        filt.add(new JLabel("X Axis :"));
        filt.add(xAxisBox);
        filt.add(new JLabel("Y Axis :"));
        filt.add(yAxisBox);
        filt.add(btn);

        // Création du cadre principal
        JFrame fr = new JFrame("AI Impact Viewer - Dark");
        fr.setSize(1300, 800);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.getContentPane().setBackground(new Color(20,20,20));
        fr.setLayout(new BorderLayout());
        fr.add(top, BorderLayout.NORTH);
        fr.add(sp, BorderLayout.CENTER);
        fr.add(filt, BorderLayout.SOUTH);
        fr.setVisible(true);
    }
    // Méthode pour afficher un graphique en courbes (Line Chart)
    public void showLineChart(List<AIImpactData> d, String yCrit) {
        // Création d'une map pour stocker les données par année
        Map<Integer, List<Double>> yearData = new HashMap<>();

        // Parcours des données pour remplir la map avec les valeurs par année
        for (AIImpactData x : d) {
            // Récupérer la valeur du critère choisi pour chaque entrée
            double value = switch (yCrit) {
                case "AI Adoption (%)" -> x.getAiAdoptionRate();
                case "Job Loss (%)" -> x.getJobLossRate();
                case "Revenue Increase (%)" -> x.getRevenueIncrease();
                case "Collab. Rate (%)" -> x.getCollaborationRate();
                case "Trust (%)" -> x.getConsumerTrust();
                case "Market Share (%)" -> x.getMarketShare();
                case "Content Volume (TB)" -> x.getContentVolume();
                default -> 0;  // Valeur par défaut si aucun critère ne correspond
            };

            // Ajout des valeurs par année à la map
            yearData.computeIfAbsent(x.getYear(), k -> new ArrayList<>()).add(value);
        }

        // Calcul de la moyenne par année
        Map<Integer, Double> yearlyAvg = new HashMap<>();
        for (Map.Entry<Integer, List<Double>> entry : yearData.entrySet()) {
            double sum = 0;
            // Calcul de la somme des valeurs pour chaque année
            for (double v : entry.getValue()) {
                sum += v;
            }
            // Ajout de la moyenne dans la map
            yearlyAvg.put(entry.getKey(), sum / entry.getValue().size());
        }

        // Création de la série de données pour le graphique en courbes
        XYSeries series = new XYSeries(yCrit);
        for (Map.Entry<Integer, Double> entry : yearlyAvg.entrySet()) {
            series.add(entry.getKey(), entry.getValue());  // Ajout de chaque année avec sa moyenne
        }

        // Création du dataset pour le graphique
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        // Création du graphique en courbes avec les données et légendes
        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Évolution de " + yCrit + " par année", "Année", yCrit, // Titre et légendes
                dataset, PlotOrientation.VERTICAL, true, true, false
        );

        // Affichage du graphique dans une nouvelle fenêtre
        JFrame fr = new JFrame("Graphique en courbes");
        fr.setSize(800, 500);
        fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fr.add(new ChartPanel(lineChart));
        fr.setVisible(true);
    }

    // Méthode pour afficher un graphique en barres (Bar Chart)
    public void showBarChartByField(List<AIImpactData> d, String f) {
        Map<String, List<Double>> vals = new HashMap<>();
        // Collecte des données pour chaque pays
        for (AIImpactData x : d) {
            double v = switch (f) {
                case "Job Loss (%)" -> x.getJobLossRate();
                case "AI Adoption (%)" -> x.getAiAdoptionRate();
                case "Revenue Increase (%)" -> x.getRevenueIncrease();
                case "Collab. Rate (%)" -> x.getCollaborationRate();
                case "Trust (%)" -> x.getConsumerTrust();
                case "Market Share (%)" -> x.getMarketShare();
                case "Content Volume (TB)" -> x.getContentVolume();
                default -> 0;
            };
            // Regroupement des valeurs par pays
            vals.computeIfAbsent(x.getCountry(), k -> new ArrayList<>()).add(v);
        }

        // Création du dataset pour le graphique
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (String k : vals.keySet()) {
            List<Double> list = vals.get(k);
            double sum = 0;
            // Calcul de la somme des valeurs pour chaque pays
            for (double x : list) sum += x;
            ds.addValue(sum / list.size(), f, k);  // Calcul de la moyenne par pays et ajout au dataset
        }

        // Création du graphique en barres
        JFreeChart chart = ChartFactory.createBarChart("Moyenne " + f + " par pays", "Pays", f, ds);
        // Affichage du graphique dans une nouvelle fenêtre
        JFrame fr = new JFrame("Graphique Barres");
        fr.setSize(800, 500);
        fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fr.add(new ChartPanel(chart));
        fr.pack();
        fr.setVisible(true);
    }

    // Méthode pour afficher un graphique circulaire (Pie Chart)
    public void showPieChartByField(List<AIImpactData> d, String f) {
        Map<String, Double> countryAverages = new HashMap<>();

        // Collecte des valeurs pour chaque pays
        for (AIImpactData x : d) {
            double v = switch (f) {
                case "Job Loss (%)" -> x.getJobLossRate();
                case "AI Adoption (%)" -> x.getAiAdoptionRate();
                case "Revenue Increase (%)" -> x.getRevenueIncrease();
                case "Collab. Rate (%)" -> x.getCollaborationRate();
                case "Trust (%)" -> x.getConsumerTrust();
                case "Market Share (%)" -> x.getMarketShare();
                case "Content Volume (TB)" -> x.getContentVolume();
                default -> 0;
            };
            // Regroupe les valeurs par pays pour calculer les moyennes
            countryAverages.merge(x.getCountry(), v, Double::sum);
        }

        // Création de la map pour compter le nombre d'entrées par pays
        Map<String, Integer> countryCounts = new HashMap<>();
        for (AIImpactData x : d) {
            countryCounts.merge(x.getCountry(), 1, Integer::sum);
        }

        // Calcul de la moyenne pour chaque pays
        for (String country : countryAverages.keySet()) {
            double sum = countryAverages.get(country);
            int count = countryCounts.getOrDefault(country, 1);
            countryAverages.put(country, sum / count);  // Calcul de la moyenne
        }

        // Création du dataset pour le graphique circulaire
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : countryAverages.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());  // Ajout des valeurs au dataset
        }

        // Création du graphique circulaire
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Répartition de la moyenne de " + f + " par pays", pieDataset, true, true, false
        );

        // Affichage du graphique dans une nouvelle fenêtre
        JFrame fr = new JFrame("Graphique Circulaire");
        fr.setSize(800, 500);
        fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fr.add(new ChartPanel(pieChart));
        fr.setVisible(true);
    }



}