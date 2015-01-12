/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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

package net.tridentsdk.meta.nbt;

/**
 * @author The TridentSDK Team
 */
public class DoubleTag extends NBTTag {
    double value;

    public DoubleTag(String name) {
        super(name);
    }

    public double getValue() {
        return this.value;
    }

    public DoubleTag setValue(double value) {
        this.value = value;
        return this;
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.meta.nbt.NBTTag#type()
     */
    @Override
    public TagType getType() {
        return TagType.DOUBLE;
    }
}
