package com.nsa.bt.scrumble.models;

public class User {
    private int id;
    private int serviceId;
    private String providerId;

    public User() {
    }

    public User(int id, int serviceId, String providerId) {
        // A users Scrumble id
        this.id = id;
        // The id of that user from the authentication server e.g. a users GitLab id
        this.serviceId = serviceId;
        // Provider id being "gitlab" for example. Defined by in application.properties
        // e.g. spring.security.oauth2.client.registration.gitlab.client-id
        this.providerId = providerId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}