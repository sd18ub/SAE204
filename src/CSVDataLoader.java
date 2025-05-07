import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDataLoader {

    public List<AIImpactData> loadData(String filePath) {
        List<AIImpactData> dataList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Ignore l'en-tête

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length >= 12) {
                    try {
                        AIImpactData data = new AIImpactData(
                                parts[0],                              // Country
                                Integer.parseInt(parts[1]),           // Year
                                parts[2],                              // Industry
                                Double.parseDouble(parts[3]),         // AI Adoption Rate
                                Double.parseDouble(parts[4]),         // Content Volume
                                Double.parseDouble(parts[5]),         // Job Loss
                                Double.parseDouble(parts[6]),         // Revenue Increase
                                Double.parseDouble(parts[7]),         // Collaboration Rate
                                parts[8],                              // Top AI Tools Used
                                parts[9],                              // Regulation Status
                                Double.parseDouble(parts[10]),        // Consumer Trust
                                Double.parseDouble(parts[11])         // Market Share
                        );
                        dataList.add(data);
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur de conversion numérique sur la ligne : " + line);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }

        return dataList;
    }

}
