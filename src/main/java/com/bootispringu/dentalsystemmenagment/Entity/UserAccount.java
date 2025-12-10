package com.bootispringu.dentalsystemmenagment.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "user_account", 
       indexes = {
           @Index(name = "idx_user_account_employee", columnList = "employee_id"),
           @Index(name = "idx_user_account_patient", columnList = "patient_id")
       })
@Check(constraints = "((employee_id IS NULL AND patient_id IS NULL AND role IN ('ADMIN', 'MANAGER', 'FINANCE')) OR " +
                     "(employee_id IS NOT NULL AND patient_id IS NULL AND role IN ('DOCTOR', 'RECEPTIONIST', 'MANAGER', 'FINANCE')) OR " +
                     "(employee_id IS NULL AND patient_id IS NOT NULL AND role = 'PATIENT'))")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name="user_name", nullable = false, unique = true)
    private String username;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = true)
    private Patient patient;

}
