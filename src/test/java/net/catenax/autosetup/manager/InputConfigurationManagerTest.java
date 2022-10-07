package net.catenax.autosetup.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenax.autosetup.entity.AutoSetupTriggerEntry;
import net.catenax.autosetup.model.AutoSetupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class InputConfigurationManagerTest {
    @Autowired
    private InputConfigurationManager inputConfigurationManager;

    @Test
    void prepareInputConfiguration() {

        String json = "{\n" +
                "    \"customer\": {\n" +
                "        \"organizationName\": \"Verul\",\n" +
                "        \"country\": \"IN\",\n" +
                "        \"state\": \"GN\",\n" +
                "        \"city\": \"BL\",\n" +
                "        \"email\": \"sachin.argade@t-systems.com\"\n" +
                "    },\n" +
                "    \"properties\": {\n" +
                "        \"bpnNumber\": \"BPN12345611\",\n" +
                "        \"role\": \"recycler\",\n" +
                "        \"subscriptionId\": \"DAS-D234\",\n" +
                "        \"serviceId\": \"T-SYSTEM-DFT-EDC\"\n" +
                "    }\n" +
                "}";
        String uuID = UUID.randomUUID().toString();
        try {
            AutoSetupRequest autoSetupRequest = new ObjectMapper().readValue(json,AutoSetupRequest.class);
            Map<String, String>resultMap =  inputConfigurationManager.prepareInputConfiguration(autoSetupRequest, uuID);
            assertNotEquals(resultMap,Exception.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void prepareInputFromDBObject() {

        AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder().autosetupTenantName("test").build();
        Map<String, String>resultMap =  inputConfigurationManager.prepareInputFromDBObject(autoSetupTriggerEntry);
        assertNotEquals(resultMap,Exception.class);
    }
}