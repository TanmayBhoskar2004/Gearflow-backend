package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.ServiceReportResponse;
import com.vst.vehicle_service_tracker.entity.*;
import com.vst.vehicle_service_tracker.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final JobCardRepository jobCardRepository;
    private final InvoiceRepository invoiceRepository;
    private final JobPartRepository jobPartRepository;

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    public ReportServiceImpl(JobCardRepository jobCardRepository,
                             InvoiceRepository invoiceRepository,
                             JobPartRepository jobPartRepository) {
        this.jobCardRepository = jobCardRepository;
        this.invoiceRepository = invoiceRepository;
        this.jobPartRepository = jobPartRepository;
    }

    @Override
    public ServiceReportResponse getServiceReport() {
        ServiceReportResponse r = new ServiceReportResponse();

        List<JobCard> jobs = jobCardRepository.findAll();

        // Services by status
        Map<String, Long> byStatus = new HashMap<>();
        for (JobStatus s : JobStatus.values()) {
            byStatus.put(s.name(), jobs.stream().filter(j -> j.getStatus() == s).count());
        }
        r.setServicesByStatus(byStatus);

        // Monthly service volume
        Map<String, Long> monthly = new TreeMap<>();
        for (JobCard j : jobs) {
            String key = j.getReceivedDate().format(MONTH_FORMAT);
            monthly.put(key, monthly.getOrDefault(key, 0L) + 1);
        }
        r.setMonthlyServiceVolume(monthly);

        // Monthly revenue (paid invoices)
        List<Invoice> invoices = invoiceRepository.findAll();
        Map<String, BigDecimal> revenue = new TreeMap<>();
        for (Invoice inv : invoices) {
            if (inv.isPaid() && inv.getInvoiceDate() != null) {
                String key = inv.getInvoiceDate().format(MONTH_FORMAT);
                revenue.put(key, revenue.getOrDefault(key, BigDecimal.ZERO).add(inv.getTotal()));
            }
        }
        r.setMonthlyRevenue(revenue);

        // Total revenue & outstanding
        r.setTotalRevenue(invoices.stream()
                .filter(Invoice::isPaid)
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        r.setTotalOutstanding(invoices.stream()
                .filter(i -> !i.isPaid())
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Top parts used
        Map<String, Long> topParts = new HashMap<>();
        for (JobPart jp : jobPartRepository.findAll()) {
            String name = jp.getPart().getName();
            topParts.put(name, topParts.getOrDefault(name, 0L) + jp.getQuantityUsed());
        }
        // Sort top 5
        Map<String, Long> sortedTop = topParts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
        r.setTopPartsUsed(sortedTop);

        return r;
    }
}