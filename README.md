# SE1020 – Object Oriented Programing

## Project Title: Medical Appointment System

## Component 01: Patient Management

```
Description: Handles registration and personal health records for patients.
```
```
CRUD Operations:
```
- **Create:** Register a new patient and save data to patients.txt.
- **Read:** Search for patients by ID or National Identity number.
- **Update:** Edit contact information or emergency contact details.

  
- **Deactivate:** Deactivate a patient profile if they are no longer active.

```
OOP Concept: Encapsulation using a Patient class with private attributes and public
getters/setters.
```
```
UI Pages: Patient Registration, Patient Login, Patient Dashboard, Patient Profile.
```
## Component 02 : Doctor & Staff Management

```
Description: Manages the directory of medical professionals and their specialties.
```
```
CRUD Operations:
```
- **Create:** Add a new doctor profile (specialty, experience, room number) to
    doctors.txt.
- **Read:** View doctor profiles filtered by department (e.g., Cardiology, ENT).
- **Update:** Change a doctor’s consultation hours or room assignment.
- **Delete:** Remove a doctor from the system upon resignation.

```
OOP Concept: Inheritance where Doctor and Nurse classes inherit common properties
from a base Staff class.
```
```
UI Pages: Add Staff Form, Doctor Directory, Edit Staff Details.
```

## Component 03 : Appointment Scheduling

```
Description: The core module for managing the time slots between patients and
doctors.
```
```
CRUD Operations:
```
- **Create:** Book a new appointment and save to appointments.txt.
- **Read:** View upcoming appointments for a specific date or doctor.
- **Update:** Reschedule an appointment to a different time or date.
- **Delete:** Cancel an appointment and free up the slot.

```
OOP Concept: Polymorphism to calculate different appointment durations or fees
based on the check-up type (e.g., General vs. Specialist).
```
```
UI Pages: Appointment Booking Form, Daily Schedule View, Cancellation Page.
```
## Component 04 : Medical Record & History Management

```
Description: Tracks the medical history, previous diagnoses, and prescriptions.
```
```
CRUD Operations:
```
- **Create:** Add a new entry after a consultation (symptoms, diagnosis) to
    records.txt.
- **Read:** Retrieve the full medical history of a specific patient.
- **Update:** Correct or add follow-up notes to an existing medical record.
- **Delete:** Archive or remove old records (following medical data retention
    policies).

```
OOP Concept: Composition where a MedicalRecord object contains multiple
Prescription objects.
```
```
UI Pages: New Diagnosis Entry, Patient History Viewer, Record Modification Page.
```

## Component 05 : Billing and Payment Management

```
Description: Handles the financial transactions, insurance claims, and receipts.
```
```
CRUD Operations:
```
- **Create:** Generate a new invoice after a consultation and save to billing.txt.
- **Read:** Search for payment history by patient ID or Invoice number.
- **Update:** Mark a payment status as "Paid," "Pending," or "Refunded."
- **Delete:** Void a mistakenly generated invoice.

```
OOP Concept: Method Overloading for different payment types (e.g.,
processPayment(double amount) for cash vs processPayment(double amount, String
cardNum) for cards).
```
```
UI Pages: Generate Invoice, Billing Dashboard, Payment Status Update.
```
## Component 06 : Feedback & Inquiry Management

```
Description: A simple module for patients to leave reviews or ask questions regarding
services.
```
```
CRUD Operations:
```
- **Create:** Submit a new feedback or support ticket to feedback.txt.
- **Read:** Admin views a list of all submitted feedback and inquiries.
- **Update:** Mark a support inquiry as "Resolved" once addressed.
- **Delete:** Remove old or irrelevant feedback/spam entries.

```
OOP Concepts: Encapsulation to secure feedback data and Polymorphism for different
views (Patient sees "Submit," Admin sees "Moderate").
```
```
UI Pages: Feedback Submission Form, Contact Support Page, Admin Feedback
Dashboard.
```


