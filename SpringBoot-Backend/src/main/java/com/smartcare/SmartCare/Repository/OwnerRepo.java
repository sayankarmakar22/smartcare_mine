package com.smartcare.SmartCare.Repository;

import com.smartcare.SmartCare.Model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepo extends JpaRepository<Owner,String> {
    Boolean existsByngoId(String id);

    Owner findByownerId(String id);
}
