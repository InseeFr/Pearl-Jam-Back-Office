package fr.insee.pearljam.api.campaign.controller;

public class EndpointDisabledException extends RuntimeException {
    public EndpointDisabledException() {
        super("This endpoint is not enabled");
    }
}
