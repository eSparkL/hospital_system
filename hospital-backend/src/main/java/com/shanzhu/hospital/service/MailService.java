package com.shanzhu.hospital.service;

public interface MailService {
    void sendAppointmentReminder(String to, String patientName, String doctorName, String appointmentTime);
}