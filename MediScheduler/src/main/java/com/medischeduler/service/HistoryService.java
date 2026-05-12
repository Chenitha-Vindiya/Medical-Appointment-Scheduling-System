package com.medischeduler.service;

import com.medischeduler.model.Appointment;
import com.medischeduler.model.History;
import com.medischeduler.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    public void createHistoryRecord(Appointment appointment) {
        String status = appointment.getStatus().toUpperCase();
        if (!status.equals("COMPLETED") && !status.equals("CANCELLED")) {
            return;
        }

        // Get the most recent history record for this appointment
        Optional<History> lastRecordOpt = historyRepository.findTopByAppointmentIdOrderByCreatedAtDesc(appointment.getId());

        if (lastRecordOpt.isPresent()) {
            History lastRecord = lastRecordOpt.get();

            // If the status is the same AND it happened less than 1 minute ago, it's a spam double-click.
            // If it's older than 1 minute, it means it's a legitimate 2nd cancellation after being rescheduled!
            if (lastRecord.getStatus().equalsIgnoreCase(status) &&
                    lastRecord.getCreatedAt().isAfter(java.time.LocalDateTime.now().minusMinutes(1))) {
                return;
            }
        }

        // Save to History table
        History history = new History();
        history.setAppointment(appointment);
        history.setPatient(appointment.getPatient());
        history.setStatus(status);
        historyRepository.save(history);
    }

    // --- PATIENT METHODS ---
    public List<History> getPatientHistory(Long patientId) {
        return historyRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public void deleteHistoryByPatient(Long historyId, Long patientId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        if (history.getPatient().getId().equals(patientId)) {
            historyRepository.delete(history);
        } else {
            throw new RuntimeException("Unauthorized deletion attempt.");
        }
    }

    // --- DOCTOR METHODS ---
    public List<History> getDoctorHistory(Long doctorId) {
        return historyRepository.findByAppointmentDoctorIdOrderByCreatedAtDesc(doctorId);
    }

    public void addDoctorNote(Long historyId, String note, Long doctorId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        if (history.getAppointment().getDoctor().getId().equals(doctorId)) {
            history.setExtraNote(note);
            historyRepository.save(history);
        } else {
            throw new RuntimeException("Unauthorized update attempt.");
        }
    }
}