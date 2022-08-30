package com.poc.kubeappswrapper.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DFTUpdateRequest {

    private String bpnNumber;
    private String keycloackUrl;
    private String keycloackClientId;
    private String keyclackRealm;
    private String digitalTwinUrl;
    private String digitalTwinAuthUrl;
    private String digitalTwinClientId;
    private String digitalTwinClientSecret;

}
