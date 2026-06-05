package com.riskgis.model.enums;

public enum RiskLevel {
    LOW("低风险", 0, 25),
    MEDIUM("中风险", 25, 50),
    HIGH("高风险", 50, 75),
    VERY_HIGH("极高风险", 75, 100);

    private final String label;
    private final int minScore;
    private final int maxScore;

    RiskLevel(String label, int minScore, int maxScore) {
        this.label = label;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public String getLabel() { return label; }
    public int getMinScore() { return minScore; }
    public int getMaxScore() { return maxScore; }

    public static RiskLevel fromScore(double score) {
        if (score >= 75) return VERY_HIGH;
        if (score >= 50) return HIGH;
        if (score >= 25) return MEDIUM;
        return LOW;
    }
}
