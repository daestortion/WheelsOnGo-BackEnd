package com.respo.respo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.respo.respo.Entity.ReportEntity;
import com.respo.respo.Repository.ReportRepository;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public List<ReportEntity> getAllReports() {
        return reportRepository.findAll();
    }

    public ReportEntity getReportById(int id) {
        return reportRepository.findById(id).orElse(null);
    }

    public ReportEntity createReport(ReportEntity report) {
        return reportRepository.save(report);
    }

    public ReportEntity updateReport(int id, ReportEntity reportDetails) {
        ReportEntity report = reportRepository.findById(id).orElse(null);
        if (report != null) {
            report.setTitle(reportDetails.getTitle());
            report.setDescription(reportDetails.getDescription());
            report.setUser(reportDetails.getUser());
            return reportRepository.save(report);
        }
        return null;
    }

    public void deleteReport(int id) {
        reportRepository.deleteById(id);
    }
}
