/*
 * Copyright 2010-2021 Australian Signals Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.plugins.algorithms.sna.similarity;

import au.gov.asd.tac.constellation.graph.node.GraphNode;
import au.gov.asd.tac.constellation.plugins.PluginExecution;
import au.gov.asd.tac.constellation.plugins.algorithms.AlgorithmPluginRegistry;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Resource Allocation Index action.
 *
 * @author canis_majoris
 */
@ActionID(category = "Similarity", id = "au.gov.asd.tac.constellation.plugins.algorithms.sna.similarity.ResourceAllocationIndexAction")
@ActionRegistration(displayName = "#CTL_ResourceAllocationIndexAction")
@Messages("CTL_ResourceAllocationIndexAction=Resource Allocation Index")
public final class ResourceAllocationIndexAction implements ActionListener {

    private final GraphNode context;

    public ResourceAllocationIndexAction(final GraphNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(final ActionEvent ev) {
        PluginExecution.withPlugin(AlgorithmPluginRegistry.RESOURCE_ALLOCATION_INDEX)
                .withParameter(ResourceAllocationIndexPlugin.INCLUDE_CONNECTIONS_IN_PARAMETER_ID, true)
                .withParameter(ResourceAllocationIndexPlugin.INCLUDE_CONNECTIONS_OUT_PARAMETER_ID, true)
                .withParameter(ResourceAllocationIndexPlugin.TREAT_UNDIRECTED_BIDIRECTIONAL_PARAMETER_ID, true)
                .withParameter(ResourceAllocationIndexPlugin.MINIMUM_COMMON_FEATURES_PARAMETER_ID, 3)
                .withParameter(ResourceAllocationIndexPlugin.SELECTED_ONLY_PARAMETER_ID, false)
                .withParameter(ResourceAllocationIndexPlugin.COMMUNITY_PARAMETER_ID, false)
                .executeLater(context.getGraph());
    }
}
