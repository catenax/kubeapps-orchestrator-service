/********************************************************************************
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the CatenaX (ng) GitHub Organisation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package net.catenax.autosetup.manager;

import java.util.HashMap;
import java.util.Map;
import net.catenax.autosetup.constant.ToolType;
import net.catenax.autosetup.entity.AutoSetupTriggerEntry;
import net.catenax.autosetup.model.Customer;
import net.catenax.autosetup.model.SelectedTools;
import net.catenax.autosetup.vault.proxy.VaultAppManageProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class VaultManagerTest {

    public static final String ENCRYPTIONKEYS = "encryptionkeys";
    public static final String CONTENT = "content";
    public static final String DAPS_CERT = "daps-cert";
    public static final String CERTIFICATE_PRIVATE_KEY = "certificate-private-key";

    @MockBean
    private VaultAppManageProxy vaultManagerProxy;

    @MockBean
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @MockBean
    private OpenSSLClientManager openSSLClientManager;

    @Autowired
    private VaultManager vaultManager;

    @Value("${vault.url}")
    private String valutURL;

    @Value("${vault.token}")
    private String vaulttoken;

    @Value("${vault.timeout}")
    private String vaulttimeout;

    @Test
    void uploadKeyandValues() {
        AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder()
                .autosetupTenantName("Test")
                .build();
        Customer customer = Customer.builder()
                .organizationName("Test")
                .build();

        Map<String, String> mockInputMap = new HashMap<>();
        mockInputMap.put("targetCluster","test");
        mockInputMap.put("postgresPassword", "admin@123");
        mockInputMap.put("username", "admin");
        mockInputMap.put("password", "admin@123");
        mockInputMap.put("database", "postgres");

        SelectedTools selectedTools = SelectedTools.builder()
                .tool(ToolType.DFT)
                .label("DFT")
                .build();

        mockInputMap = vaultManager.uploadKeyandValues(customer, selectedTools,mockInputMap, autoSetupTriggerEntry);
        assertEquals(14, mockInputMap.size());
        assertEquals("test", mockInputMap.get("targetCluster"));
    }
}