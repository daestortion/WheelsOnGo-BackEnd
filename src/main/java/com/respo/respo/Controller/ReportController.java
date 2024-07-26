package com.respo.respo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.respo.respo.Entity.ReportEntity;
import com.respo.respo.Service.ReportService;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public List<ReportEntity> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportEntity> getReportById(@PathVariable int id) {
        ReportEntity report = reportService.getReportById(id);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ReportEntity createReport(@RequestBody ReportEntity report) {
        return reportService.createReport(report);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportEntity> updateReport(@PathVariable int id, @RequestBody ReportEntity reportDetails) {
        ReportEntity updatedReport = reportService.updateReport(id, reportDetails);
        return updatedReport != null ? ResponseEntity.ok(updatedReport) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable int id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
