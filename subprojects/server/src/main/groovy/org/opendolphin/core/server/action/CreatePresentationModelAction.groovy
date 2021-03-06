/*
 * Copyright 2012-2013 Canoo Engineering AG.
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

package org.opendolphin.core.server.action

import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.comm.ActionRegistry
import groovy.transform.CompileStatic

@CompileStatic
class CreatePresentationModelAction extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        registry.register(CreatePresentationModelCommand) { CreatePresentationModelCommand command, response ->
            createPresentationModel(command, serverDolphin) // closure wrapper for correct scoping and extracted method for static compilation
        }
    }

    private static void createPresentationModel(CreatePresentationModelCommand command, ServerDolphin serverDolphin) {
        List<ServerAttribute> attributes = new LinkedList()
        for (Map<String, Object> attr in command.attributes) {
            ServerAttribute attribute = new ServerAttribute((String) attr.propertyName, attr.value, (String) attr.qualifier, Tag.tagFor[(String) attr.tag])
            attribute.id = attr.id as Long
            attributes << attribute
        }
        PresentationModel model = new ServerPresentationModel(command.pmId, attributes)
        model.presentationModelType = command.pmType
        serverDolphin.serverModelStore.add(model)
    }
}
