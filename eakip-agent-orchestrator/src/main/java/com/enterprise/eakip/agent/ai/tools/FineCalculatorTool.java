package com.enterprise.eakip.agent.ai.tools;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FineCalculatorTool implements Tool {

    @Override
    public String getName() {
        return "FineCalculatorTool";
    }

    @Override
    public String getDescription() {
        return "Calculates potential late fees accrued based on date values difference parameters";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("overdueDays", "Number of days the borrowing period has lapsed");
        return params;
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        Object daysVal = arguments.get("overdueDays");
        int days = 0;
        if (daysVal instanceof Number) {
            days = ((Number) daysVal).intValue();
        } else if (daysVal instanceof String) {
            try {
                days = Integer.parseInt((String) daysVal);
            } catch (Exception e) {
                // Default
            }
        }
        
        double rate = 2.50; // $2.50 fine per day
        double fine = days * rate;

        Map<String, Object> result = new HashMap<>();
        result.put("fineAmount", fine);
        result.put("currency", "USD");
        result.put("daysOverdue", days);
        return result;
    }
}
