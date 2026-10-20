package com.bloodlink.repository;

import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.BloodRequest;
import com.bloodlink.model.Donor;
import com.bloodlink.model.RequestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class RepositoryQueryTest {
    @Autowired
    private DonorRepository donors;

    @Autowired
    private BloodRequestRepository requests;

    @Test
    void explicitQueriesFilterAndOrderResults() {
        donors.save(donor("Alex", BloodGroup.O_POSITIVE, "Berlin", true));
        donors.save(donor("Blair", BloodGroup.O_POSITIVE, "Berlin", false));
        donors.save(donor("Casey", BloodGroup.A_POSITIVE, "Berlin", true));

        BloodRequest later = request("Later", "Berlin", LocalDate.of(2026, 11, 2));
        BloodRequest sooner = request("Sooner", "Berlin", LocalDate.of(2026, 10, 25));
        BloodRequest fulfilled = request("Done", "Berlin", LocalDate.of(2026, 10, 21));
        fulfilled.setStatus(RequestStatus.FULFILLED);
        requests.save(later);
        requests.save(sooner);
        requests.save(fulfilled);

        assertThat(donors.findAvailableDonors(BloodGroup.O_POSITIVE, "berlin"))
                .extracting(Donor::getName)
                .containsExactly("Alex");
        assertThat(requests.findByStatusAndCity(RequestStatus.OPEN, "BERLIN"))
                .extracting(BloodRequest::getPatientName)
                .containsExactly("Sooner", "Later");
    }

    private Donor donor(String name, BloodGroup bloodGroup, String city, boolean available) {
        Donor donor = new Donor();
        donor.setName(name);
        donor.setPhone("phone-" + name);
        donor.setBloodGroup(bloodGroup);
        donor.setCity(city);
        donor.setAvailable(available);
        return donor;
    }

    private BloodRequest request(String patient, String city, LocalDate neededBy) {
        BloodRequest request = new BloodRequest();
        request.setPatientName(patient);
        request.setBloodGroup(BloodGroup.O_POSITIVE);
        request.setHospital("Central Hospital");
        request.setCity(city);
        request.setUnitsNeeded(2);
        request.setNeededBy(neededBy);
        return request;
    }
}
