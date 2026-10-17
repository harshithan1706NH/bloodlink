package com.bloodlink.repository;
import com.bloodlink.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DonorRepository extends JpaRepository<Donor, Long> {
    List<Donor> findByBloodGroupAndCityIgnoreCaseAndAvailableTrue(BloodGroup bloodGroup, String city);
}
