package com.example.attendance.dto;

import com.example.attendance.model.AbsenceCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class AttendanceReportDto {
    private Map<AbsenceCategory, Long> categoryCounts;
    private Map<AbsenceCategory, Map<String, List<String>>> categoryGroupStudentsMap;
}