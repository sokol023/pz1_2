package com.example.attendance.service;

import com.example.attendance.model.AbsenceCategory;
import com.example.attendance.model.StudentAbsenceRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ReportGeneratorService {

    public String generateHtmlReport(List<StudentAbsenceRecord> records) {
        LocalDate now = LocalDate.now();

        String fullDate = now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String shortYearDate = now.format(DateTimeFormatter.ofPattern("dd.MM.yy"));
        String dayMonth = now.format(DateTimeFormatter.ofPattern("dd.MM"));
        String singleDigitDayMonth = now.format(DateTimeFormatter.ofPattern("d.M"));

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body style='font-family: Arial, sans-serif;'>");
        htmlBuilder.append("<h2>Загальний список особового складу на (").append(fullDate).append("):</h2>");
        htmlBuilder.append("<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>");
        htmlBuilder.append("<tr style='background-color: #f2f2f2;'>")
                .append("<th style='padding: 8px;'>№</th>")
                .append("<th style='padding: 8px;'>Група</th>")
                .append("<th style='padding: 8px;'>ПІБ</th>")
                .append("<th style='padding: 8px;'>Статус / Причина</th>")
                .append("</tr>");

        int index = 1;

        if (records != null) {
            for (StudentAbsenceRecord record : records) {
                if (record.getAbsences() == null) continue;

                for (Map.Entry<String, AbsenceCategory> entry : record.getAbsences().entrySet()) {
                    String rawDate = entry.getKey().trim();
                    AbsenceCategory category = entry.getValue();

                    boolean isToday = rawDate.contains(dayMonth)
                            || rawDate.contains(singleDigitDayMonth)
                            || rawDate.equalsIgnoreCase(fullDate)
                            || rawDate.equalsIgnoreCase(shortYearDate);

                    if (isToday) {
                        // Підсвічуємо тих, хто не "В наявності"
                        String rowStyle = (category == AbsenceCategory.PRESENT)
                                ? ""
                                : "style='background-color: #fff2f2; font-weight: bold;'";

                        htmlBuilder.append("<tr ").append(rowStyle).append(">")
                                .append("<td style='padding: 8px;'>").append(index++).append("</td>")
                                .append("<td style='padding: 8px;'>").append(record.getGroup()).append("</td>")
                                .append("<td style='padding: 8px;'>").append(record.getFullName()).append("</td>")
                                .append("<td style='padding: 8px;'>").append(category.getDisplayName()).append("</td>")
                                .append("</tr>");
                    }
                }
            }
        }

        if (index == 1) {
            htmlBuilder.append("<tr><td colspan='4' style='padding: 8px; text-align: center;'>Дані на сьогодні відсутні.</td></tr>");
        }

        htmlBuilder.append("</table>");
        htmlBuilder.append("</body></html>");

        return htmlBuilder.toString();
    }
}