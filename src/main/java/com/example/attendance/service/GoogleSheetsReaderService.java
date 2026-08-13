package com.example.attendance.service;

import com.example.attendance.model.AbsenceCategory;
import com.example.attendance.model.StudentAbsenceRecord;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSheetsReaderService {

    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}")
    private String spreadsheetId;

    @Value("${google.sheets.range}")
    private String range;

    @Value("${google.sheets.api-key:}")
    private String apiKey;

    public List<StudentAbsenceRecord> readAttendanceData() {
        List<StudentAbsenceRecord> records = new ArrayList<>();
        try {
            Sheets.Spreadsheets.Values.Get request = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range);

            if (apiKey != null && !apiKey.isBlank()) {
                request.setKey(apiKey);
            }

            ValueRange response = request.execute();
            List<List<Object>> values = response.getValues();

            if (values == null || values.size() < 2) {
                log.warn("Таблиця порожня або відсутні дані.");
                return records;
            }

            // 1. Отримуємо шапку (рядок з датами)
            List<Object> header = values.get(0);
            Map<Integer, String> colToDateMap = new HashMap<>();

            for (int col = 2; col < header.size(); col++) {
                String val = header.get(col) != null ? header.get(col).toString().trim() : "";
                if (!val.isEmpty()) {
                    colToDateMap.put(col, val);
                }
            }

            // 2. Зчитуємо дані всіх осіб за списком
            for (int i = 1; i < values.size(); i++) {
                List<Object> row = values.get(i);
                if (row == null || row.isEmpty()) continue;

                String group = row.size() > 0 && row.get(0) != null ? row.get(0).toString().trim() : "";
                String fullName = row.size() > 1 && row.get(1) != null ? row.get(1).toString().trim() : "";

                if (fullName.isEmpty() || fullName.equalsIgnoreCase("ПІП") || fullName.equalsIgnoreCase("ПІБ")) {
                    continue;
                }

                Map<String, AbsenceCategory> absences = new HashMap<>();

                for (Map.Entry<Integer, String> entry : colToDateMap.entrySet()) {
                    int colIndex = entry.getKey();
                    String date = entry.getValue();

                    if (colIndex < row.size()) {
                        String cellValue = row.get(colIndex) != null ? row.get(colIndex).toString().trim() : "";
                        AbsenceCategory category = AbsenceCategory.parse(cellValue);
                        absences.put(date, category);
                    } else {
                        // Якщо клітинка порожня — за замовчуванням В наявності
                        absences.put(date, AbsenceCategory.PRESENT);
                    }
                }

                records.add(new StudentAbsenceRecord(group, fullName, absences));
            }

            log.info("✅ Успішно зчитано осіб за списком: {}", records.size());

        } catch (Exception e) {
            log.error("❌ Помилка зчитування з Google Sheets: ", e);
        }

        return records;
    }
}