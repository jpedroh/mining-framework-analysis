/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.inventory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InventoryTypeTest {
    @Test
    public void testTypes() {
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/test/java/net/tridentsdk/inventory/InventoryTypeTest.java/left.java
        String types = "minecraft:container\n" +
                "minecraft:chest\n" +
                "minecraft:crafting_table\n" +
                "minecraft:furnace\n" +
                "minecraft:dispenser\n" +
                "minecraft:enchanting_table\n" +
                "minecraft:brewing_stand\n" +
                "minecraft:villager\n" +
                "minecraft:beacon\n" +
                "minecraft:anvil\n" +
                "minecraft:hopper\n" +
                "minecraft:dropper\n" +
                "minecraft:shulker_box\n" +
                "EntityHorse\n" +
                "player";
||||||| /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/test/java/net/tridentsdk/inventory/InventoryTypeTest.java/base.java
        // TODO
=======
        String types = "Player\n" +
                "minecraft:container\n" +
                "minecraft:chest\n" +
                "minecraft:crafting_table\n" +
                "minecraft:furnace\n" +
                "minecraft:dispenser\n" +
                "minecraft:enchanting_table\n" +
                "minecraft:brewing_stand\n" +
                "minecraft:villager\n" +
                "minecraft:beacon\n" +
                "minecraft:anvil\n" +
                "minecraft:hopper\n" +
                "minecraft:dropper\n" +
                "minecraft:shulker_box\n" +
                "EntityHorse";
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/test/java/net/tridentsdk/inventory/InventoryTypeTest.java/right.java
        for (InventoryType type : InventoryType.values()) {
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/test/java/net/tridentsdk/inventory/InventoryTypeTest.java/left.java
        assertEquals(types.split("\n")[type.ordinal()],
                type.toString());
||||||| /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/test/java/net/tridentsdk/inventory/InventoryTypeTest.java/base.java
        assertEquals(type);
=======
        assertEquals(types.split("\n")[type.ordinal()], type.toString());
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/test/java/net/tridentsdk/inventory/InventoryTypeTest.java/right.java
    }
    }
}
