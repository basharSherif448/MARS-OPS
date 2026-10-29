package colony.events; // الباكيج الخاص بأحداث وطوارئ المستعمرة

import agents.Agent; // استيراد كلاس Agent
import agents.Human; // استيراد كلاس Human للمريض

/**
 * كلاس يمثل حالة الطوارئ الطبية لرواد المستعمرة ويرث من Emergency[cite: 1]
 */
public class MedicalEmergency extends Emergency {
    private Human patient;

    public MedicalEmergency(String id, int severity, EmergencyStatus status, Human patient, MarsColony marsColony) {
        super(id, severity, status, patient.getLocation(), marsColony); // موقع الطارئ هو موقع المريض الحالي
        this.patient = patient;
    }

    // إرجاع مرجع المريض المتأثر
    public Human getPatient() {
        return patient;
    }

    // تنفيذ الاستجابة لحالة الطوارئ الطبية (Section 9.5)
    @Override
    public boolean respond() {
        // التحقق من أن حالة الطوارئ نشطة والمريض متواجد
        if (super.getStatus() != EmergencyStatus.ACTIVE || patient == null) {
            return false;
        }

        Agent medicalOfficer = null; // الكادر الطبي المستجيب

        for (Agent agent : this.marsColony.getAgents()) {
            if (agent.isAvailable() && agent.canHandle(this)) {
                medicalOfficer = agent;
                break;
            }
        }

        if (medicalOfficer == null) {
            this.markUnresolved("No available medical officer for patient"); // تسجيل عدم القدرة على العلاج
            return false;
        }

        medicalOfficer.setLocation(patient.getLocation());

        if (!hasEnoughMedicalSupplies()) {
            this.markUnresolved("Insufficient medical supplies in colony"); // فشل لعدم توافر الأدوية
            return false;
        }

        consumeMedicalSupplies();

        patient.setHealthStatus("Healthy");

        this.resolve();
        return true;
    }

    private boolean hasEnoughMedicalSupplies() {
        return this.marsColony.getTotalResource("MedicalSupplies") >= 1.0;
    }

    private void consumeMedicalSupplies() {
        this.marsColony.getResourceManager().consume("MedicalSupplies", 1.0);
    }
}
