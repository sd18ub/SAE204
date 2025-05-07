public class AIImpactData {
    private String country;
    private int year;
    private String industry;
    private double aiAdoptionRate;
    private double contentVolume;
    private double jobLossRate;
    private double revenueIncrease;
    private double collaborationRate;
    private String topAIToolsUsed;
    private String regulationStatus;
    private double consumerTrust;
    private double marketShare;

    public AIImpactData(String country, int year, String industry, double aiAdoptionRate,
                        double contentVolume, double jobLossRate, double revenueIncrease,
                        double collaborationRate, String topAIToolsUsed, String regulationStatus,
                        double consumerTrust, double marketShare) {
        this.country = country;
        this.year = year;
        this.industry = industry;
        this.aiAdoptionRate = aiAdoptionRate;
        this.contentVolume = contentVolume;
        this.jobLossRate = jobLossRate;
        this.revenueIncrease = revenueIncrease;
        this.collaborationRate = collaborationRate;
        this.topAIToolsUsed = topAIToolsUsed;
        this.regulationStatus = regulationStatus;
        this.consumerTrust = consumerTrust;
        this.marketShare = marketShare;
    }

    @Override
    public String toString() {
        return String.format(
                "Country: %-12s | Year: %4d | Industry: %-12s | AI Adoption Rate: %6.2f%% | Content Volume: %6.2f TB\n" +
                        "Job Loss: %6.2f%% | Revenue Increase: %6.2f%% | Collaboration Rate: %6.2f%%\n" +
                        "Top AI Tool: %-15s | Regulation: %-10s | Consumer Trust: %6.2f%% | Market Share: %6.2f%%\n" +
                        "---------------------------------------------------------------------------------------------",
                country, year, industry, aiAdoptionRate, contentVolume,
                jobLossRate, revenueIncrease, collaborationRate,
                topAIToolsUsed, regulationStatus, consumerTrust, marketShare
        );
    }
    public String getCountry() {
        return country;
    }

    public int getYear() {
        return year;
    }

    public String getIndustry() {
        return industry;
    }

    public double getAiAdoptionRate() {
        return aiAdoptionRate;
    }

    public double getContentVolume() {
        return contentVolume;
    }

    public double getJobLossRate() {
        return jobLossRate;
    }

    public double getRevenueIncrease() {
        return revenueIncrease;
    }

    public double getCollaborationRate() {
        return collaborationRate;
    }

    public String getTopAIToolsUsed() {
        return topAIToolsUsed;
    }

    public String getRegulationStatus() {
        return regulationStatus;
    }

    public double getConsumerTrust() {
        return consumerTrust;
    }

    public double getMarketShare() {
        return marketShare;
    }


}
