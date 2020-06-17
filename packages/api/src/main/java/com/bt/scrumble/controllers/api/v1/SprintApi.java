package com.bt.scrumble.controllers.api.v1;

import com.bt.scrumble.models.Sprint;
import com.bt.scrumble.security.UserPrincipal;
import com.bt.scrumble.services.ISprintService;
import com.bt.scrumble.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class SprintApi {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISprintService sprintService;

    @Value("${app.msg.error.auth}")
    private String authErrorMsg;

    @GetMapping("/workspace/{workspaceId}/sprints")
    public ResponseEntity<Object> getWorkspaceSprints(
            Authentication auth,
            @PathVariable(value = "workspaceId") int workspaceId,
            @RequestParam(value = "filter") String filter
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Optional<String> accessTokenOptional = userService.getToken(userPrincipal.getId());
        if (accessTokenOptional.isPresent()) {
            var sprints = sprintService.getSprintsForWorkspace(workspaceId, filter);
            return ResponseEntity.ok().body(sprints);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(authErrorMsg);
    }

    @PostMapping("/workspace/{workspaceId}/sprint")
    public ResponseEntity<Object> createSprint(
            Authentication auth,
            @PathVariable(value = "workspaceId") int workspaceId,
            @RequestBody Sprint sprint
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Optional<String> accessTokenOptional = userService.getToken(userPrincipal.getId());
        return accessTokenOptional.<ResponseEntity<Object>>map(s ->
                ResponseEntity.ok().body(sprintService.createSprint(workspaceId, sprint, s))).orElseGet(() ->
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(authErrorMsg)
        );
    }

    @PutMapping("/workspace/{workspaceId}/sprint")
    public ResponseEntity<Object> editSprint(
            Authentication auth,
            @PathVariable(value = "workspaceId") int workspaceId,
            @RequestBody Sprint sprint
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Optional<String> accessTokenOptional = userService.getToken(userPrincipal.getId());
        if (accessTokenOptional.isPresent()) {
            sprint = sprintService.editSprint(workspaceId, sprint, accessTokenOptional.get());
            return ResponseEntity.ok().body(sprint);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", authErrorMsg));
    }

    // POST Method to get data? What a quality engineer, ladies and gentlemen.
    @PostMapping("/workspace/{workspaceId}/sprint/issues")
    public ResponseEntity<Object> getSprintIssues(
            Authentication auth,
            @PathVariable(value = "workspaceId") int workspaceId,
            @RequestBody Sprint sprint
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        Optional<String> accessTokenOptional = userService.getToken(userPrincipal.getId());
        return accessTokenOptional.<ResponseEntity<Object>>map(s ->
                ResponseEntity.ok().body(sprintService.getSprintIssues(workspaceId, sprint, s))).orElseGet(() ->
                ResponseEntity.status(400).body(authErrorMsg));
    }
}