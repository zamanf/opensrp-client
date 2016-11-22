package org.ei.opensrp.vaccinator.domain;

import org.ei.opensrp.domain.Alert;
import org.joda.time.DateTime;
import org.ei.opensrp.vaccinator.db.VaccineRepo.Vaccine;

import java.util.Date;

/**
 * Created by keyman on 16/11/2016.
 */
public class VaccineWrapper {
    private String status;
    private Vaccine vaccine;
    private DateTime vaccineDate;
    private Alert alert;
    private String previousVaccine;
    private boolean compact;

    private String patientName;
    private String patientNumber;

    private DateTime updatedVaccineDate;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Vaccine getVaccine() {
        return vaccine;
    }

    public void setVaccine(Vaccine vaccine) {
        this.vaccine = vaccine;
    }

    public DateTime getVaccineDate() {
        return vaccineDate;
    }

    public void setVaccineDate(DateTime vaccineDate) {
        this.vaccineDate = vaccineDate;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public String getPreviousVaccine() {
        return previousVaccine;
    }

    public void setPreviousVaccine(String previousVaccine) {
        this.previousVaccine = previousVaccine;
    }

    public boolean isCompact() {
        return compact;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public DateTime getUpdatedVaccineDate() {
        return updatedVaccineDate;
    }

    public void setUpdatedVaccineDate(DateTime updatedVaccineDate) {
        this.updatedVaccineDate = updatedVaccineDate;
    }

    //Custom getters
    public String getVaccineAsString() {
        return vaccine.display();
    }

    public String getVaccineDateAsString() {
        return vaccineDate != null ? vaccineDate.toString("yyyy-MM-dd") : "";
    }

    public String getUpdatedVaccineDateAsString() {
        return updatedVaccineDate != null ? updatedVaccineDate.toString("yyyy-MM-dd") : "";
    }


}
