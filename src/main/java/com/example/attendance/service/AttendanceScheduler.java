package com.example.attendance.service;

import com.example.attendance.model.StudentAbsenceRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final GoogleSheetsReaderService sheetsReaderService;
    private final ReportGeneratorService reportGeneratorService;
    private final EmailSenderService emailSenderService;

    // Запускається ОДИН РАЗ одразу після старту проєкту в окремому потоці (не блокує запуск)
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        executeAttendanceProcess();
    }

    // Запускається щодня о 08:00 ранку
    @Scheduled(cron = "0 0 8 * * ?")
    public void executeAttendanceProcess() {
        log.info("🚀 Запуск збору даних з Google Sheets...");

        try {
            List<StudentAbsenceRecord> records = sheetsReaderService.readAttendanceData();
            String htmlReport = reportGeneratorService.generateHtmlReport(records);

            emailSenderService.sendReportEmail("bogdanchik0536@gmail.com", "Звіт про відсутність особового складу", htmlReport);
            log.info("🏁 Звіт успішно відправлено!");
        } catch (Exception e) {
            log.error("❌ Помилка під час виконання з звіту: ", e);
        }
    }
}