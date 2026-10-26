package com.bloodlink.repository;
import com.bloodlink.model.BloodRequest;
import com.bloodlink.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    long countByStatus(RequestStatus status);

    @Query("""
            select r from BloodRequest r
            where r.status = :status
              and lower(r.city) = lower(:city)
            order by r.neededBy
            """)
    List<BloodRequest> findByStatusAndCity(
            @Param("status") RequestStatus status,
            @Param("city") String city);
}
