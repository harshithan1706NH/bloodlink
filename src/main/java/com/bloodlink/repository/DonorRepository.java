package com.bloodlink.repository;
import com.bloodlink.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    @Query("""
            select d from Donor d
            where d.bloodGroup = :bloodGroup
              and lower(d.city) = lower(:city)
              and d.available = true
            order by d.name
            """)
    List<Donor> findAvailableDonors(
            @Param("bloodGroup") BloodGroup bloodGroup,
            @Param("city") String city);
}
