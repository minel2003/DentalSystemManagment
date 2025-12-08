package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.Employee;
import com.bootispringu.dentalsystemmenagment.Entity.Message;
import com.bootispringu.dentalsystemmenagment.Entity.Patient;
import com.bootispringu.dentalsystemmenagment.Repository.EmployeeRepository;
import com.bootispringu.dentalsystemmenagment.Repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final EmployeeRepository employeeRepository;

    public MessageService(MessageRepository messageRepository,
                          EmployeeRepository employeeRepository) {
        this.messageRepository = messageRepository;
        this.employeeRepository = employeeRepository;
    }

    public void sendMessageToDoctor(Patient patient,
                                    String doctorFirstName,
                                    String doctorLastName,
                                    String subject,
                                    String content) {

        Optional<Employee> doctorOpt =
                employeeRepository.findByFirstNameAndLastName(doctorFirstName, doctorLastName);

        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        Employee doctor = doctorOpt.get();


        Message message = new Message();
        message.setPatient(patient);
        message.setDoctor(doctor);
        message.setSubject(subject);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());

        messageRepository.save(message);
    }
    public List<Message> getMessagesForDoctor(Employee doctor) {
        return messageRepository.findByDoctorOrderBySentAtDesc(doctor);
    }

    public long countMessagesForDoctor(Employee doctor) {
        return messageRepository.countByDoctor(doctor);
    }
}
