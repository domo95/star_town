package org.example.star_town.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.agent.Agent;
import org.example.star_town.agent.AgentConfig;
import org.example.star_town.agent.AgentType;
import org.example.star_town.model.AgentEntity;
import org.example.star_town.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 智能体服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {
    
    private final AgentRepository agentRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 保存智能体到数据库
     */
    @Transactional
    public AgentEntity saveAgent(Agent agent) {
        try {
            AgentEntity entity = new AgentEntity();
            entity.setId(agent.getId());
            entity.setName(agent.getName());
            entity.setType(agent.getType());
            entity.setPositionX(agent.getPosition().getX());
            entity.setPositionY(agent.getPosition().getY());
            entity.setStateJson(objectMapper.writeValueAsString(agent.getState()));
            entity.setMemoryJson(objectMapper.writeValueAsString(agent.getMemory()));
            entity.setConfigJson(objectMapper.writeValueAsString(agent.getConfig()));
            entity.setStatus(agent.getStatus());
            entity.setIsActive(true);
            
            return agentRepository.save(entity);
        } catch (JsonProcessingException e) {
            log.error("Error serializing agent data for agent {}: {}", agent.getId(), e.getMessage());
            throw new RuntimeException("Failed to save agent", e);
        }
    }
    
    /**
     * 从数据库加载智能体
     */
    @Transactional(readOnly = true)
    public Optional<Agent> loadAgent(String agentId) {
        return agentRepository.findById(agentId).map(this::convertToAgent);
    }
    
    /**
     * 加载所有活跃的智能体
     */
    @Transactional(readOnly = true)
    public List<Agent> loadAllActiveAgents() {
        return agentRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToAgent)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据类型加载智能体
     */
    @Transactional(readOnly = true)
    public List<Agent> loadAgentsByType(AgentType type) {
        return agentRepository.findByType(type)
                .stream()
                .map(this::convertToAgent)
                .collect(Collectors.toList());
    }
    
    /**
     * 删除智能体
     */
    @Transactional
    public void deleteAgent(String agentId) {
        agentRepository.deleteById(agentId);
        log.info("Deleted agent: {}", agentId);
    }
    
    /**
     * 批量保存智能体
     */
    @Transactional
    public List<AgentEntity> saveAllAgents(List<Agent> agents) {
        List<AgentEntity> entities = agents.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        
        return agentRepository.saveAll(entities);
    }
    
    /**
     * 更新智能体位置
     */
    @Transactional
    public void updateAgentPosition(String agentId, double x, double y) {
        agentRepository.findById(agentId).ifPresent(agent -> {
            agent.setPositionX(x);
            agent.setPositionY(y);
            agentRepository.save(agent);
        });
    }
    
    /**
     * 更新智能体状态
     */
    @Transactional
    public void updateAgentStatus(String agentId, Agent.AgentStatus status) {
        agentRepository.findById(agentId).ifPresent(agent -> {
            agent.setStatus(status);
            agentRepository.save(agent);
        });
    }
    
    /**
     * 查找附近的智能体
     */
    @Transactional(readOnly = true)
    public List<Agent> findNearbyAgents(double x, double y, double radius) {
        return agentRepository.findNearbyAgents(x, y, radius)
                .stream()
                .map(this::convertToAgent)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取智能体统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getAgentStatistics() {
        return agentRepository.countByType()
                .stream()
                .collect(Collectors.toMap(
                        result -> ((AgentType) result[0]).getDisplayName(),
                        result -> (Long) result[1]
                ));
    }
    
    /**
     * 将AgentEntity转换为Agent
     */
    private Agent convertToAgent(AgentEntity entity) {
        try {
            AgentConfig config = objectMapper.readValue(entity.getConfigJson(), AgentConfig.class);
            Agent agent = new Agent(entity.getId(), entity.getName(), entity.getType(), config);
            
            agent.setPosition(new org.example.star_town.world.Position(
                    entity.getPositionX(), entity.getPositionY()));
            
            Map<String, Object> state = objectMapper.readValue(entity.getStateJson(), Map.class);
            agent.setState(state);
            
            Map<String, Object> memory = objectMapper.readValue(entity.getMemoryJson(), Map.class);
            agent.setMemory(memory);
            
            // 设置状态
            agent.setStatus(entity.getStatus());
            
            return agent;
        } catch (JsonProcessingException e) {
            log.error("Error deserializing agent data for agent {}: {}", entity.getId(), e.getMessage());
            throw new RuntimeException("Failed to load agent", e);
        }
    }
    
    /**
     * 将Agent转换为AgentEntity
     */
    private AgentEntity convertToEntity(Agent agent) {
        try {
            AgentEntity entity = new AgentEntity();
            entity.setId(agent.getId());
            entity.setName(agent.getName());
            entity.setType(agent.getType());
            entity.setPositionX(agent.getPosition().getX());
            entity.setPositionY(agent.getPosition().getY());
            entity.setStateJson(objectMapper.writeValueAsString(agent.getState()));
            entity.setMemoryJson(objectMapper.writeValueAsString(agent.getMemory()));
            entity.setConfigJson(objectMapper.writeValueAsString(agent.getConfig()));
            entity.setStatus(agent.getStatus());
            entity.setIsActive(true);
            
            return entity;
        } catch (JsonProcessingException e) {
            log.error("Error serializing agent data for agent {}: {}", agent.getId(), e.getMessage());
            throw new RuntimeException("Failed to convert agent to entity", e);
        }
    }
}
