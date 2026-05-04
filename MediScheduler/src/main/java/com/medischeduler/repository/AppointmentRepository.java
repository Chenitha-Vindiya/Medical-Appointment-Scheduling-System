package com.medischeduler.repository;

import com.medischeduler.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1. Find all appointments for a specific doctor
    List<Appointment> findByDoctorId(Long doctorId);

    // 2. Find appointments for a doctor on a specific date
    // (Useful for the doctor's daily schedule)
    List<Appointment> findByAppointmentDateAndStartTime(LocalDate date, LocalTime time);

    // 3. Find appointments for a specific patient
    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByPatientIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(Long patientId, LocalDate date);

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(Long doctorId, LocalDate date);

    List<Appointment> findByStatusAndAppointmentDateAndStartTimeBefore(String status, LocalDate date, LocalTime time);

    List<Appointment> findByStatus(String status);

    List<Appointment> findByPatientIdAndStatusInOrderByAppointmentDateDesc(Long patientId, List<String> statuses);


    //Appointment repository
}