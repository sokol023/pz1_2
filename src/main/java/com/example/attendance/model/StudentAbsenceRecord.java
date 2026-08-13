package com.example.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAbsenceRecord {
    private String group;
    private String fullName;
    private Map<String, AbsenceCategory> absences;
}