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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import net.catenax.autosetup.constant.AppActions;
import net.catenax.autosetup.constant.ToolType;
import net.catenax.autosetup.model.SelectedTools;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class EDCDataplaneManagerTest {

    @Mock
    private KubeAppsPackageManagement appManagement;

    @Mock
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @Value("${mail.replyto.address}")
    private String replyTo;

    @Value("${dns.name}")
    private String dnsOriginalName;

    @InjectMocks
    private  EDCDataplaneManager edcDataplaneManager;

    @Test
    void managePackage() {
        Map<String, String> mockInputMap = new HashMap<>();
        mockInputMap.put("targetCluster","test");
        mockInputMap.put("dnsName", "test");
        mockInputMap.put("dnsNameURLProtocol","https");
        String s = replyTo;
        SelectedTools selectedTools = SelectedTools.builder()
                .tool(ToolType.DFT)
                .label("DFT")
                .build();
        mockInputMap = edcDataplaneManager.managePackage(null, AppActions.CREATE,selectedTools,mockInputMap, null);
        assertEquals(5, mockInputMap.size());
        assertEquals("test", mockInputMap.get("targetCluster"));
    }
}