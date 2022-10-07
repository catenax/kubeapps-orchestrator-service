package net.catenax.autosetup.manager;

import net.catenax.autosetup.constant.AppActions;
import net.catenax.autosetup.constant.ToolType;
import net.catenax.autosetup.model.SelectedTools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DFTBackendManagerTest {

    @InjectMocks
    private DFTBackendManager dftBackendManager;

    @Mock
    private KubeAppsPackageManagement appManagement;

    @Mock
    private PortalIntegrationManager portalIntegrationManager;

    @Mock
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @Test
    void managePackage() {

        SelectedTools selectedTools = SelectedTools.builder()
                .tool(ToolType.DFT)
                .label("dfttool")
                .build();
        Map<String, String> mockInputMap = new HashMap<>();
        mockInputMap.put("dnsName","test");
        mockInputMap.put("dnsNameURLProtocol","https");
        Map<String, String> resultMap = dftBackendManager.managePackage(null,AppActions.CREATE,selectedTools,mockInputMap,null);
        assertNotEquals(resultMap,Exception.class);
    }
}