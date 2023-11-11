package com.smartcare.SmartCare.Services.Implementation;

import com.smartcare.SmartCare.DTO.AgentDTO;
import com.smartcare.SmartCare.DTO.AgentResponse;
import com.smartcare.SmartCare.Helper.AgentHelper;
import com.smartcare.SmartCare.Model.Agent;
import com.smartcare.SmartCare.Repository.AgentRepo;
import com.smartcare.SmartCare.Services.AgentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentServicesImpl implements AgentServices {
    @Autowired
    private AgentRepo agentRepo;
    @Override
    public Object saveAgent(AgentDTO agentDTO) {
        return AgentHelper.setAgentResponse((agentRepo.save(AgentHelper.convertIntoAgent(agentDTO,new Agent()))),new AgentResponse());
    }

    @Override
    public Object viewAgent(String agentId) {
        if(agentRepo.existsByagentId(agentId)){
            return agentRepo.findById(agentId);
        }
        throw new RuntimeException("agent not exists");
    }

    @Override
    public String deleteAgent(String agentId) {
        if(agentRepo.existsByagentId(agentId)){
            agentRepo.deleteById(agentId);
            return "deleted id : " + agentId;
        }
        throw new RuntimeException("agent not exists");
    }

    @Override
    public String checkNgoAgentId(String id) {
        return agentRepo.existsByagentId(id) ? "Ngo Agent Id not available" : "Ngo Agent Id available";
    }
    @Override
    public String generateNextAgentId(String ownerId){
        String latestAgentId = agentRepo.latestAgentId(ownerId);
        int trimmedId = Integer.parseInt(latestAgentId.substring(3)) ;
        return latestAgentId.replace(String.valueOf(trimmedId),String.valueOf(trimmedId +1));
    }
}
