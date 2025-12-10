package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.*;
import com.bootispringu.dentalsystemmenagment.Repository.ReceiptRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final AppointmentServiceService appointmentServiceService;

    public ReceiptService(ReceiptRepository receiptRepository,
                         AppointmentServiceService appointmentServiceService) {
        this.receiptRepository = receiptRepository;
        this.appointmentServiceService = appointmentServiceService;
    }

    public List<Receipt> findAll() {
        return receiptRepository.findAll();
    }

    public Optional<Receipt> findById(Long id) {
        return receiptRepository.findById(id);
    }

    public Optional<Receipt> findByReceiptNumber(String receiptNumber) {
        return receiptRepository.findByReceiptNumber(receiptNumber);
    }

    public List<Receipt> findByPatient(Patient patient) {
        return receiptRepository.findByPatient(patient);
    }

    public List<Receipt> findByReceiptDate(LocalDate date) {
        return receiptRepository.findByReceiptDate(date);
    }

    @Transactional
    public Receipt save(Receipt receipt) {

        if (receipt.getReceiptNumber() == null || receipt.getReceiptNumber().isBlank()) {
            receipt.setReceiptNumber(generateReceiptNumber());
        }
        
        if (receipt.getReceiptDate() == null) {
            receipt.setReceiptDate(LocalDate.now());
        }
        
        if (receipt.getCreatedAt() == null) {
            receipt.setCreatedAt(LocalDateTime.now());
        }
        
        receipt.setUpdatedAt(LocalDateTime.now());
        
        if (receipt.getStatus() == null) {
            receipt.setStatus(Receipt.ReceiptStatus.PAID);
        }
        
        return receiptRepository.save(receipt);
    }

    @Transactional
    public void delete(Long id) {
        receiptRepository.deleteById(id);
    }

    private String generateReceiptNumber() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String prefix = "REC-" + year + "-";
        

        List<Receipt> receiptsThisYear = receiptRepository.findAll().stream()
                .filter(r -> r.getReceiptNumber() != null && r.getReceiptNumber().startsWith(prefix))
                .toList();
        
        int maxNumber = 0;
        for (Receipt receipt : receiptsThisYear) {
            try {
                String numberPart = receipt.getReceiptNumber().substring(prefix.length());
                int num = Integer.parseInt(numberPart);
                if (num > maxNumber) {
                    maxNumber = num;
                }
            } catch (NumberFormatException e) {

            }
        }
        
        int nextNumber = maxNumber + 1;
        return prefix + String.format("%04d", nextNumber);
    }

    public List<Receipt> findAllOrderByDateDesc() {
        return receiptRepository.findAll().stream()
                .sorted((a, b) -> b.getReceiptDate().compareTo(a.getReceiptDate()))
                .toList();
    }

    @Transactional
    public Receipt createReceiptFromAppointment(Appointment appointment, Employee createdBy) {

        java.math.BigDecimal totalAmount = appointmentServiceService.calculateTotalForAppointment(appointment);
        
        if (totalAmount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Cannot create receipt: Appointment has no services or total amount is zero.");
        }

        Receipt receipt = new Receipt();
        receipt.setPatient(appointment.getPatient());
        receipt.setAppointment(appointment);
        receipt.setReceiptDate(LocalDate.now());
        receipt.setAmount(totalAmount);
        receipt.setPaymentMethod("PENDING");
        receipt.setStatus(Receipt.ReceiptStatus.PAID);
        receipt.setCreatedBy(createdBy);
        receipt.setDescription("Services provided during appointment on " + appointment.getAppointmentDate());

        return save(receipt);
    }
}

