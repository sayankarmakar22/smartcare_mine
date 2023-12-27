package com.smartcare.SmartCare.Repository;

import com.smartcare.SmartCare.Model.PinGenerate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface PinGenerateRepo extends JpaRepository<PinGenerate,Integer> {
    @Query(value = "select pin from pin_generate where customer_id =:id",nativeQuery = true)
    String findPinByCustId(String id);
}
