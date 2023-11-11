package com.smartcare.SmartCare.Repository;

import com.smartcare.SmartCare.Model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepo extends JpaRepository<Agent,String> {
    Boolean existsByagentId(String id);
    @Query(value = "select agent_id from agent where ngo_id =:ownerId order by agent_id desc limit 1",nativeQuery = true)
    String latestAgentId(String ownerId);
}
