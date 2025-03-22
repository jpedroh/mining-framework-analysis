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

import javax.annotation.concurrent.Immutable;

/**
 * Represents the types of inventories that are accessible
 * in the Minecraft world.
 *
 * @author TridentSDK
 * @since 0.3-alpha-DP
 */
@Immutable
public enum InventoryType {
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/left.java
    /**
     * Misc types, also used for ender chests.
     */
    CONTAINER,
    CHEST,
    CRAFTING_TABLE,
    FURNACE,
    DISPENSER,
    ENCHANTING_TABLE,
    BREWING_STAND,
    VILLAGER,
    BEACON,
    ANVIL,
    HOPPER,
    DROPPER,
    SHULKER_BOX,
    ENTITY_HORSE("EntityHorse"),
    /**
     * Player inventory, not instantiable
     */
    PLAYER("player");

    /**
     * The raw name of the inventory as represented by the
     * protocol.
     */
    private final String raw;

    /**
     * Creates a new inventory type based on the enum name.
     */
    InventoryType() {
        this.raw = "minecraft:" + this.name().toLowerCase();
    }

    /**
     * Creates a new inventory type based on the given raw
     * inventory name.
     *
     * @param raw the raw inventory name
     */
    InventoryType(String raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return this.raw;
    }
||||||| /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/base.java
    PLAYER, CHEST
=======
    /**
     * Represents a player inventory. This exists in code
     * only, no explicit type for an inventory of this type
     * may be created sent by the server.
     */
    PLAYER("Player"),
    CONTAINER,
    CHEST,
    CRAFTING_TABLE,
    FURNACE,
    DISPENSER,
    ENCHANTING_TABLE,
    BREWING_STAND,
    VILLAGER,
    BEACON,
    ANVIL,
    HOPPER,
    DROPPER,
    SHULKER_BOX,
    HORSE("EntityHorse");

    /**
     * The raw string form of the inventory type
     */
    private final String name;

    /**
     * Creates an inventory type that is the default
     * prefixed by {@code minecraft:} and the enum name in
     * lowercase.
     */
    InventoryType() {
        this.name = "minecraft:" + this.name().toLowerCase();
    }

    /**
     * Creates a new inventory with the given custom name
     * as an irregularity.
     *
     * @param name the name of the inventory
     */
    InventoryType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/right.java
}
