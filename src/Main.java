import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        CSVDataLoader loader = new CSVDataLoader();
        List<AIImpactData> data = loader.loadData("Global_AI_Impact.csv");
        for (AIImpactData row : data) {
            System.out.println(row);
        }
        AIImpactTableViewer viewer = new AIImpactTableViewer();
        viewer.showTable(data);

    }
}