package com.nsa.bt.scrumble.regression;
import org.springframework.stereotype.Service; 
import org.springframework.beans.factory.annotation.Autowired;
import com.nsa.bt.scrumble.dto.Issue;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class DataGrabber {
    private int[][] dataPoints = new int[][]{};

    @Autowired
    RestTemplate restTemplate;
    
    public void setDataPoints(Issue[] issues) {
        if(issues.length == 0) {
            this.dataPoints = new int[][]{};
        } else { 
            this.dataPoints = new int[issues.length][issues.length];
            for(int i = 0; i< issues.length; i++) {
                if(issues[i].getTimeSpent() > 0) {
                    this.dataPoints[i][0] = issues[i].getStoryPoint();
                    this.dataPoints[i][1] = issues[i].getTimeSpent();
                }
            }
        }
    }

    public Issue[] getClosedIssues(String gitLabBaseUrl, int projectId, String accessToken) {
        String closedIssuesUri = String.format("%1s/projects/%2s/issues?state=closed&access_token=%3s", gitLabBaseUrl, projectId, accessToken);
        ResponseEntity<Issue[]> closeIssuesResponse = restTemplate.getForEntity(closedIssuesUri, Issue[].class);
        Issue[] closedIssues = closeIssuesResponse.getBody();
        return closedIssues;
    }

    public int[][] getDataPoints() {
        return dataPoints;
    }
}