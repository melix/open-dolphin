/*
 * Copyright 2012 Canoo Engineering AG.
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

package com.canoo.dolphin.core.comm

public class JsonCodecTest extends GroovyTestCase {

    void testEmpty() {
        assertSoManyCommands(0)
    }

    void testOne() {
        assertSoManyCommands(1)
    }

    void testMany() {
        assertSoManyCommands(10)
    }

    void assertSoManyCommands(int count) {
        def codec = new JsonCodec()
        def commands = []
        count.times{
            commands << new AttributeCreatedCommand(pmId: it, attributeId: it*count, propertyName: "prop$it", newValue: "value$it", qualifier: null)
        }
        def coded = codec.encode(commands)
        def decoded = codec.decode(coded)
        assert commands.toString() == decoded.toString()
    }
    void testCodingCreatePresentationModelCommandWithDisallowedSelfReflectiveMapEntry() {
        def map = [ propertyName: 'x', qualifier: null ]
        map.value = map
        shouldFail {
            assertCodingCreatePresentationModel(map)
        }
    }
    void testCodingCreatePresentationModelWithStructuredEntry() {
        def map = [ propertyName: 'x', qualifier: null ]
        map.value = "ok"
        assertCodingCreatePresentationModel(map)
    }

    void testCodingCreatePresentationModelWithEmptyAttributes() {
        assertCodingCreatePresentationModel([:])
    }

    void assertCodingCreatePresentationModel(Map attributes) {
        def codec = new JsonCodec()
        def commands = []
        commands << new CreatePresentationModelCommand(pmId: "bla", attributes: [attributes])
        def coded = codec.encode(commands)
        def decoded = codec.decode(coded)
        assert commands.toString().toList().sort() == decoded.toString().toList().sort() // ;-)
    }



}
