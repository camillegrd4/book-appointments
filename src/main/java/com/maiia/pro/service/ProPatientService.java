package com.maiia.pro.service;

import com.maiia.pro.entity.Patient;
import com.maiia.pro.repository.PatientRepository;
import com.sun.xml.bind.v2.model.core.ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProPatientService {
    private final PatientRepository patientRepository;

	public ProPatientService(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	public Patient find(String patientId) {
        return patientRepository.findById(patientId).orElseThrow();
    }

    public boolean exists(Integer patientId) {
        return patientRepository.existsById(patientId);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }
}
