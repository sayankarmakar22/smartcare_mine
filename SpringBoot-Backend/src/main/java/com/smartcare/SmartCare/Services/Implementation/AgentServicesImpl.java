package com.smartcare.SmartCare.Services.Implementation;

import com.smartcare.SmartCare.DTO.AgentDTO;
import com.smartcare.SmartCare.DTO.AgentResponse;
import com.smartcare.SmartCare.Helper.AgentHelper;
import com.smartcare.SmartCare.Model.Agent;
import com.smartcare.SmartCare.Redis.Helper.RedisAgentHelper;
import com.smartcare.SmartCare.Redis.Model.RedisAgent;
import com.smartcare.SmartCare.Repository.AgentRepo;
import com.smartcare.SmartCare.Services.AgentServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AgentServicesImpl implements AgentServices {
    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private RedisTemplate redisTemplate;

    private final String hashKeyForAgent="agentKey";

    private Logger log = LoggerFactory.getLogger(AgentServicesImpl.class);

    @Override
    public Object saveAgent(AgentDTO agentDTO) {
        Agent savedAgent = agentRepo.save(AgentHelper.convertIntoAgent(agentDTO, new Agent()));
        log.info("saved " + agentDTO.getAgentId() + " to sql ");
        redisTemplate.opsForHash().put(hashKeyForAgent,agentDTO.getAgentId(), RedisAgentHelper.convertIntoRedisAgent(new RedisAgent(),savedAgent));
        log.info("saved " + agentDTO.getAgentId() + " to redis");
        return AgentHelper.setAgentResponse(savedAgent,new AgentResponse());
    }

    @Override
    public Object viewAgent(String agentId) {
        Object foundAgentFromRedis = redisTemplate.opsForHash().get(hashKeyForAgent, agentId);
        if(foundAgentFromRedis == null){
            log.info("found  " + agentId + "from  sql");
            return agentRepo.findById(agentId);
        }
        else if(foundAgentFromRedis != null){
            log.info("found  " + agentId + "from  redis");
            return foundAgentFromRedis;
        }
        throw new RuntimeException("agent not exists");
    }

    @Override
    public String deleteAgent(String agentId) {
        redisTemplate.opsForHash().delete(hashKeyForAgent,agentId);
        log.info("deleted " + agentId + " from redis");
        if(agentRepo.existsByagentId(agentId)){
            agentRepo.deleteById(agentId);
            log.info("deleted " + agentId + " from sql");
            return "deleted id : " + agentId;
        }
        throw new RuntimeException("agent not exists");
    }

    @Override
    public String generateNextAgentId(String ownerId){
        String latestAgentId = agentRepo.latestAgentId(ownerId);
        int trimmedId = Integer.parseInt(latestAgentId.substring(3)) ;
        return latestAgentId.replace(String.valueOf(trimmedId),String.valueOf(trimmedId +1));
    }
}
