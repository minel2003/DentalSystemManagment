package com.bootispringu.dentalsystemmenagment.Controller;

import com.bootispringu.dentalsystemmenagment.Entity.Receipt;
import com.bootispringu.dentalsystemmenagment.Service.AppointmentServiceService;
import com.bootispringu.dentalsystemmenagment.Service.ReceiptService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FinanceController {

    private final ReceiptService receiptService;
    private final AppointmentServiceService appointmentServiceService;

    public FinanceController(ReceiptService receiptService,
                            AppointmentServiceService appointmentServiceService) {
        this.receiptService = receiptService;
        this.appointmentServiceService = appointmentServiceService;
    }

    @GetMapping("/finance/home")
    public String financeHome(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        List<Receipt> receipts = receiptService.findAllOrderByDateDesc();
        

        long totalReceipts = receipts.size();
        long paidReceipts = receipts.stream()
                .filter(r -> r.getStatus() == Receipt.ReceiptStatus.PAID)
                .count();
        long pendingReceipts = receipts.stream()
                .filter(r -> r.getStatus() == Receipt.ReceiptStatus.PAID && 
                            "PENDING".equals(r.getPaymentMethod()))
                .count();

        model.addAttribute("totalReceipts", totalReceipts);
        model.addAttribute("paidReceipts", paidReceipts);
        model.addAttribute("pendingReceipts", pendingReceipts);
        model.addAttribute("recentReceipts", receipts.stream().limit(10).toList());

        return "finance/home";
    }


    @GetMapping("/finance/receipt/list")
    public String listReceipts(Model model) {
        model.addAttribute("receipts", receiptService.findAllOrderByDateDesc());
        return "finance/receipt_list";
    }


    @GetMapping("/finance/receipt/view/{id}")
    public String viewReceipt(@PathVariable Long id, Model model) {
        Receipt receipt = receiptService.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));
        
        // Get services if receipt is linked to an appointment
        List<com.bootispringu.dentalsystemmenagment.Entity.AppointmentService> services = null;
        java.math.BigDecimal calculatedTotal = null;
        if (receipt.getAppointment() != null) {
            services = appointmentServiceService.findByAppointment(receipt.getAppointment());
            // Calculate total from services (this is the correct amount to display)
            if (services != null && !services.isEmpty()) {
                calculatedTotal = appointmentServiceService.calculateTotalForAppointment(receipt.getAppointment());
                // Update receipt amount if it doesn't match calculated total
                if (calculatedTotal.compareTo(receipt.getAmount()) != 0) {
                    receipt.setAmount(calculatedTotal);
                    receiptService.save(receipt);
                }
            }
        }
        

        java.math.BigDecimal displayAmount = calculatedTotal != null ? calculatedTotal : receipt.getAmount();
        
        model.addAttribute("receipt", receipt);
        model.addAttribute("appointmentServices", services);
        model.addAttribute("calculatedTotal", displayAmount);
        model.addAttribute("companyName", "DentalCare Pro");
        model.addAttribute("companyAddress", "123 Main Street, City, State 12345");
        return "finance/receipt_view";
    }


    @PostMapping("/finance/receipt/{id}/payment")
    public String updatePaymentStatus(@PathVariable Long id,
                                     @RequestParam("paymentMethod") String paymentMethod,
                                     @RequestParam("status") String status,
                                     RedirectAttributes redirectAttributes) {
        try {
            Receipt receipt = receiptService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Receipt not found"));

            receipt.setPaymentMethod(paymentMethod);
            receipt.setStatus(Receipt.ReceiptStatus.valueOf(status.toUpperCase()));
            
            receiptService.save(receipt);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Payment status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update payment status: " + e.getMessage());
        }

        return "redirect:/finance/receipt/view/" + id;
    }
}

