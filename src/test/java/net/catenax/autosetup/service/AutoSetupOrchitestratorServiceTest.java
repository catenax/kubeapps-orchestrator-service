package net.catenax.autosetup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenax.autosetup.manager.AutoSetupTriggerManager;
import net.catenax.autosetup.model.AutoSetupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class AutoSetupOrchitestratorServiceTest {

    @Autowired
    private AutoSetupOrchitestratorService autoSetupOrchitestratorService;

    @MockBean
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @Test
    void createPackage() {

        String json = "{\n" +
                "    \"customer\": {\n" +
                "        \"organizationName\": \"Verul1\",\n" +
                "        \"country\": \"IN\",\n" +
                "        \"state\": \"GN\",\n" +
                "        \"city\": \"BL\",\n" +
                "        \"email\": \"sachin.argade@t-systems.com\"\n" +
                "    },\n" +
                "    \"properties\": {\n" +
                "        \"bpnNumber\": \"BPN12345611\",\n" +
                "        \"role\": \"recycler\",\n" +
                "        \"subscriptionId\": \"DAS-D234\",\n" +
                "        \"serviceId\": \"DFT-WITH-EDC\"\n" +
                "    }\n" +
                "}";

        try {
            AutoSetupRequest autoSetupRequest = new ObjectMapper().readValue(json,AutoSetupRequest.class);
            Mockito.when(autoSetupTriggerManager.isAutoSetupAvailableforOrgnizationName(Mockito.anyString())).thenReturn(null);
            String uuid = autoSetupOrchitestratorService.createPackage(autoSetupRequest);
            assertThat(uuid).isNotEmpty();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}