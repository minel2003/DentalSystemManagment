package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Appointment;
import com.bootispringu.dentalsystemmenagment.Entity.AppointmentService;
import com.bootispringu.dentalsystemmenagment.Repository.AppointmentServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AppointmentServiceService {

    private final AppointmentServiceRepository appointmentServiceRepository;

    public AppointmentServiceService(AppointmentServiceRepository appointmentServiceRepository) {
        this.appointmentServiceRepository = appointmentServiceRepository;
    }

    public List<AppointmentService> findByAppointment(Appointment appointment) {
        return appointmentServiceRepository.findByAppointmentOrderByCreatedAtAsc(appointment);
    }

    @Transactional
    public AppointmentService save(AppointmentService appointmentService) {

        if (appointmentService.getTotalPrice() == null) {
            BigDecimal quantity = BigDecimal.valueOf(appointmentService.getQuantity() != null ? appointmentService.getQuantity() : 1);
            BigDecimal unitPrice = appointmentService.getUnitPrice() != null ? appointmentService.getUnitPrice() : BigDecimal.ZERO;
            appointmentService.setTotalPrice(unitPrice.multiply(quantity));
        }
        return appointmentServiceRepository.save(appointmentService);
    }

    @Transactional
    public AppointmentService addServiceToAppointment(Appointment appointment, com.bootispringu.dentalsystemmenagment.Entity.Service service, Integer quantity, String notes) {
        AppointmentService appointmentService = new AppointmentService();
        appointmentService.setAppointment(appointment);
        appointmentService.setService(service);
        appointmentService.setQuantity(quantity != null ? quantity : 1);
        appointmentService.setUnitPrice(service.getPrice());
        appointmentService.setNotes(notes);
        

        BigDecimal qty = BigDecimal.valueOf(appointmentService.getQuantity());
        appointmentService.setTotalPrice(service.getPrice().multiply(qty));
        
        return save(appointmentService);
    }

    @Transactional
    public void delete(Long id) {
        appointmentServiceRepository.deleteById(id);
    }

    @Transactional
    public void deleteByAppointment(Appointment appointment) {
        List<AppointmentService> services = findByAppointment(appointment);
        appointmentServiceRepository.deleteAll(services);
    }

    public BigDecimal calculateTotalForAppointment(Appointment appointment) {
        List<AppointmentService> services = findByAppointment(appointment);
        return services.stream()
                .map(AppointmentService::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

