package com.bootispringu.dentalsystemmenagment.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Message{

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne
@JoinColumn(name = "patient_id")
private Patient patient;

@ManyToOne
@JoinColumn(name = "doctor_id", nullable = true)
private Employee doctor;
@Column(name="subject")
private String subject;

@Column(name="content")
private String content;
@Column(name="sent_at")
private LocalDateTime sentAt;

@PrePersist
protected void onCreate() {
    this.sentAt = LocalDateTime.now();
}
}
