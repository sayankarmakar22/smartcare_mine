package com.smartcare.SmartCare.Repository;

import com.smartcare.SmartCare.Model.HelpList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelpListRepo extends JpaRepository<HelpList,Integer> {
}
