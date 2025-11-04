package com.blessed_brains.visionfitAi;
import com.prolificinteractive.materialcalendarview.*;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import android.graphics.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PushupDecorator implements DayViewDecorator {

    private final Map<CalendarDay, Integer> pushupData;

    // Four sets for different ranges
    private final Set<CalendarDay> lightDays = new HashSet<>();
    private final Set<CalendarDay> mediumDays = new HashSet<>();
    private final Set<CalendarDay> highDays = new HashSet<>();
    private final Set<CalendarDay> veryHighDays = new HashSet<>();

    public PushupDecorator(Map<CalendarDay, Integer> data) {
        this.pushupData = data;

        for (Map.Entry<CalendarDay, Integer> entry : data.entrySet()) {
            int count = entry.getValue();
            CalendarDay day = entry.getKey();
            if (count < 30) lightDays.add(day);
            else if (count < 60) mediumDays.add(day);
            else if (count < 90) highDays.add(day);
            else veryHighDays.add(day);
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return lightDays.contains(day) || mediumDays.contains(day)
                || highDays.contains(day) || veryHighDays.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Decorate based on which set the day belongs to
        if (!lightDays.isEmpty()) view.addSpan(new DotSpan(8, Color.parseColor("#FF7043"))); // light orange
        if (!mediumDays.isEmpty()) view.addSpan(new DotSpan(8, Color.parseColor("#FFB74D"))); // orange
        if (!highDays.isEmpty()) view.addSpan(new DotSpan(8, Color.parseColor("#FFD54F"))); // yellow
        if (!veryHighDays.isEmpty()) view.addSpan(new DotSpan(8, Color.parseColor("#4CAF50"))); // green
    }
}
