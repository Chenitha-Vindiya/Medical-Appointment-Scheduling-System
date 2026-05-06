package com.medischeduler.service;

import com.medischeduler.model.Appointment;
import com.medischeduler.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Appointment> getPatientHistory(Long patientId) {
        System.out.println("Fetching patient history for ID: " + patientId);
        // Fetch only COMPLETED or CANCELLED records
        List<String> statuses = List.of("COMPLETED", "CANCELLED");
        return appointmentRepository.findByPatientIdAndStatusInOrderByAppointmentDateDesc(patientId, statuses);
    }
}
