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

package org.opendolphin.core.server

import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.ModelStoreListener

public class ServerDolphinTest extends GroovyTestCase {
    ServerDolphin dolphin

    @Override
    protected void setUp() throws Exception {
        dolphin = new ServerDolphin()
    }

    // todo dk: creating a SPM adds the respective commands to the response

    void testListPresentationModels() {
        assert dolphin.listPresentationModelIds().empty
        assert dolphin.listPresentationModels().empty

        def pm1 = new ServerPresentationModel("first", [])
        dolphin.modelStore.add pm1

        assert ['first'].toSet() == dolphin.listPresentationModelIds()
        assert [pm1] == dolphin.listPresentationModels().toList()

        def pm2 = new ServerPresentationModel("second", [])
        dolphin.modelStore.add pm2

        assert 2 == dolphin.listPresentationModelIds().size()
        assert 2 == dolphin.listPresentationModels().size()

        for (id in dolphin.listPresentationModelIds()) {
            assert dolphin.findPresentationModelById(id) in dolphin.listPresentationModels()
        }
    }

    void testAddRemoveModelStoreListener() {
        int typedListenerCallCount = 0
        int listenerCallCount = 0
        ModelStoreListener listener = new ModelStoreListener() {
            @Override
            void modelStoreChanged(ModelStoreEvent event) {
                listenerCallCount++
            }
        }
        ModelStoreListener typedListener = new ModelStoreListener() {
            @Override
            void modelStoreChanged(ModelStoreEvent event) {
                typedListenerCallCount++
            }
        }
        dolphin.addModelStoreListener 'person', typedListener
        dolphin.addModelStoreListener listener
        dolphin.getModelStore().add(new ServerPresentationModel('p1', []))
        ServerPresentationModel modelWithType = new ServerPresentationModel('person1', [])
        modelWithType.setPresentationModelType('person')
        dolphin.getModelStore().add(modelWithType)
        dolphin.getModelStore().add(new ServerPresentationModel('p2', []))
        dolphin.removeModelStoreListener 'person', typedListener
        dolphin.removeModelStoreListener listener
        assert 3 == listenerCallCount
        assert 1 == typedListenerCallCount


    }

    void testAddModelStoreListenerWithClosure() {
        int typedListenerCallCount = 0
        int listenerCallCount = 0
        dolphin.addModelStoreListener 'person', { evt -> typedListenerCallCount++ }
        dolphin.addModelStoreListener { evt -> listenerCallCount++ }
        dolphin.getModelStore().add(new ServerPresentationModel('p1', []))
        ServerPresentationModel modelWithType = new ServerPresentationModel('person1', [])
        modelWithType.setPresentationModelType('person')
        dolphin.getModelStore().add(modelWithType)
        dolphin.getModelStore().add(new ServerPresentationModel('p2', []))
        assert 3 == listenerCallCount
        assert 1 == typedListenerCallCount
    }

    void testHasModelStoreListener() {
        ModelStoreListener listener = getListener()
        assert !dolphin.hasModelStoreListener(null)
        assert !dolphin.hasModelStoreListener(listener)
        dolphin.addModelStoreListener listener
        assert dolphin.hasModelStoreListener(listener)
        listener = getListener()
        dolphin.addModelStoreListener('person', listener)
        assert !dolphin.hasModelStoreListener('car',listener)
        assert dolphin.hasModelStoreListener('person',listener)
    }

    void testRegisterDefaultActions() {
        dolphin.registerDefaultActions();
        def numDefaultActions = dolphin.serverConnector.dolphinServerActions.size()

        // multiple calls should not lead to multiple initializations
        dolphin.registerDefaultActions();
        assert numDefaultActions == dolphin.serverConnector.dolphinServerActions.size()
    }

    private ModelStoreListener getListener() {
        new ModelStoreListener() {
            @Override
            void modelStoreChanged(ModelStoreEvent event) {
                // do nothing
            }
        }
    }
}
