package com.autosetup.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DFTUpdateRequest {

    private String keycloakUrl;
    private String keycloakRealm;
    private String keycloakFrontendClientId;
    private String keycloakBackendClientId;
    
    private String digitalTwinUrl;
    private String digitalTwinAuthUrl;
    private String digitalTwinClientId;
    private String digitalTwinClientSecret;

}
