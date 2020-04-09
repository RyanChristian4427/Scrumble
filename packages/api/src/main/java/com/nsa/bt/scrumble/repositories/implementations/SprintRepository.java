package com.nsa.bt.scrumble.repositories.implementations;

import com.nsa.bt.scrumble.models.Sprint;
import com.nsa.bt.scrumble.repositories.ISprintRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Repository
public class SprintRepository  implements ISprintRepository {

    private static final Logger logger = LoggerFactory.getLogger(SprintRepository.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Sprint createSprint(int workspaceId, Sprint sprint)  {
        String insertStatement = "INSERT INTO sprints (workspace_id, title, description, status, start_date, due_date, sprint_data) VALUES (?, ?, ?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();


        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(insertStatement, new String[] {"id"});
            ps.setInt(1, workspaceId);
            ps.setString(2, sprint.getTitle());
            ps.setString(3, sprint.getDescription());
            ps.setString(4, sprint.getStatus());
            ps.setDate(5, sprint.getStartDate());
            ps.setDate(6, sprint.getDueDate());
            ps.setObject(7, getSprintJsonbData(sprint));
            return ps;
        }, keyHolder);
        sprint.setId(Math.toIntExact(keyHolder.getKey().longValue()));
        return sprint;
    }

    @Override
    public void deleteSprint(int sprintId) {
        String deleteWorkspace = "DELETE FROM sprints WHERE id = ?";
        Object[] params = new Object[]{ sprintId };
        int[] types = new int[]{Types.INTEGER};
        jdbcTemplate.update(deleteWorkspace, params, types);
    }

    @Override
    public List<Sprint> getAllSprintsForWorkspace(int workspaceId, String filter) {
        String selectStatement;
        Object[] params;
        int[] types;
        if(filter.equalsIgnoreCase("none")) {
            selectStatement = "SELECT * FROM sprints where workspace_id = ?";
            params = new Object[]{ workspaceId };
            types = new int[]{Types.INTEGER};
        } else {
            logger.info("Going for filter");
            selectStatement = "SELECT * FROM sprints where workspace_id = ? AND status = ?";
            params = new Object[]{ workspaceId, filter };
            types = new int[]{Types.INTEGER, Types.VARCHAR};
        }
        return mapRowsToSprintList(selectStatement, params, types);
    }

    @Override
    public Sprint editSprint(int workspaceId, Sprint sprint) {
        String deleteWorkspace = "UPDATE sprints SET title = ?, description = ?, status = ?, start_date = ?, due_date = ?, sprint_data = ? WHERE id = ?";
        Object[] params = new Object[]
                {
                        sprint.getTitle(),
                        sprint.getDescription(),
                        sprint.getStatus(),
                        sprint.getStartDate(),
                        sprint.getDueDate(),
                        getSprintJsonbData(sprint),
                        sprint.getId()
                };
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.DATE, Types.OTHER, Types.INTEGER} ;
        jdbcTemplate.update(deleteWorkspace, params, types);
        return sprint;
    }

    @Override
    public Sprint getSprintById(int sprintId) {
        return null;
    }

    @Override
    public Map<String, Integer> getProjectIdsToMileStoneIds(int sprintId) {
        String selectStatement = "SELECT sprint_data FROM sprints where id = ?";
        Object[] params = new Object[]{sprintId};
        int[] types = new int[]{Types.INTEGER};
        return jdbcTemplate.queryForObject(selectStatement,
                params,
                types,
                (rs, row) -> {
                    try {
                        return parseJsonDataToMilestoneIds(((PGobject) rs.getObject("sprint_data")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    @Override
    public List<Sprint> getPageOfSprints(int workspaceId, int pageNumber, int pageSize) {
        String selectStatement = "SELECT * FROM sprints where workspace_id = ? LIMIT ? OFFSET ?";
        Object[] params = new Object[]{ workspaceId , pageSize, pageNumber };
        int[] types = new int[]{ Types.INTEGER, Types.INTEGER, Types.INTEGER };
        return mapRowsToSprintList(selectStatement, params, types);
    }

    private List<Sprint> mapRowsToSprintList(String sqlSelect, Object[] params , int[] types) {
        return new ArrayList<>(jdbcTemplate.query(sqlSelect,
                params,
                types,
                (rs, row) -> {
                    try {
                        return new Sprint(
                                rs.getInt("id"),
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getString("status"),
                                rs.getDate("start_date"),
                                rs.getDate("due_date"),
                                parseJsonDataToMilestoneIds(((PGobject) rs.getObject("sprint_data"))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }));
    }

    private PGobject getSprintJsonbData(Sprint sprint) {
        try {
            Map<Object, Object> dataMap = new HashMap<>();
            PGobject jsonObject = new PGobject();
            ObjectMapper objectMapper = new ObjectMapper();

            dataMap.put("projects_to_milestones", sprint.getProjectIdToMilestoneIds());
            String Map_Json_String = objectMapper.writeValueAsString(dataMap);
            jsonObject.setType("jsonb");
            jsonObject.setValue(Map_Json_String);
            return jsonObject;
        } catch (SQLException | JsonProcessingException exception) {
            logger.error(exception.getMessage());
            return null;
        }
    }

    private Map<String, Integer> parseJsonDataToMilestoneIds(PGobject jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return (Map<String, Integer>) mapper.readValue(jsonData.getValue(), Map.class).get("projects_to_milestones");
    }
}
