package com.enterprise.eakip.agent.ai.tools;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AnalyticsTool implements Tool {

    @Override
    public String getName() {
        return "AnalyticsInsightsTool";
    }

    @Override
    public String getDescription() {
        return "Computes borrow frequency records, user active statistics and monthly reading rates";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("metricType", "Type of analytics required, e.g. POPULAR_BOOKS, BORROW_TRENDS");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String metricType = (String) arguments.get("metricType");
        Map<String, Object> result = new HashMap<>();
        if ("POPULAR_BOOKS".equalsIgnoreCase(metricType)) {
            result.put("mostBorrowed", "Clean Architecture");
            result.put("borrowCount", 145);
        } else if ("BORROW_TRENDS".equalsIgnoreCase(metricType)) {
            result.put("monthlyGrowth", "+12.4%");
            result.put("activeStudents", 280);
        } else {
            result.put("readingIndex", 8.4);
            result.put("status", "STABLE");
        }
        return result;
    }
}
