package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;

/**
 * Piston implementation of the CreeperMultiblock.
 *
 * @author Jikoo
 *
 */
class CreeperPiston extends CreeperMultiblock {

    CreeperPiston(BlockState blockState) {
        super(blockState);

<<<<<<< /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperPiston.java/left.java
        BlockData blockData = blockState.getBlockData();
||||||| /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperPiston.java/base.java
    /*
     * Constructor.
     */
    CreeperPiston(BlockState blockState)
    {
        Block block = blockState.getBlock();
        if (blockState.getType().equals(Material.PISTON_EXTENSION))
            block = block.getRelative(castData(blockState, PistonExtensionMaterial.class).getAttachedFace());
        this.blockState = block.getState();
        PistonBaseMaterial data = castData(this.blockState, PistonBaseMaterial.class);
        orientation = data.getFacing();
        Block extension_block = block.getRelative(orientation);
        extended = extension_block.getType().equals(Material.PISTON_EXTENSION) &&
                   castData(extension_block.getState(), PistonExtensionMaterial.class).getFacing().equals(orientation);
        PistonBaseMaterial newdata = data.clone();
        newdata.setPowered(false);
        this.blockState.setData(newdata);
    }
=======
    /*
     * Constructor.
     */
    CreeperPiston(BlockState blockState)
    {
        super(blockState);
        Block block = blockState.getBlock();
        if (blockState.getType().equals(Material.PISTON_EXTENSION))
            block = block.getRelative(castData(blockState, PistonExtensionMaterial.class).getAttachedFace());
        this.blockState = block.getState();
        PistonBaseMaterial data = castData(this.blockState, PistonBaseMaterial.class);
        orientation = data.getFacing();
        Block extension_block = block.getRelative(orientation);
        extended = extension_block.getType().equals(Material.PISTON_EXTENSION) &&
                   castData(extension_block.getState(), PistonExtensionMaterial.class).getFacing().equals(orientation);
        PistonBaseMaterial newdata = data.clone();
        newdata.setPowered(false);
        this.blockState.setData(newdata);
    }
>>>>>>> /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperPiston.java/right.java

        if (blockData instanceof Piston) {

            Piston piston = ((Piston) blockData);
            Block headBlock = blockState.getBlock().getRelative(piston.getFacing());
            BlockData headBlockData = headBlock.getBlockData();

            if (headBlockData instanceof PistonHead && ((PistonHead) headBlockData).getFacing() == piston.getFacing()) {
                this.dependents.add(headBlock.getState());
            }

        } else if (blockData instanceof PistonHead) {

            PistonHead piston = ((PistonHead) blockData);
            Block pistonBlock = blockState.getBlock().getRelative(piston.getFacing().getOppositeFace());
            BlockData pistonBlockData = pistonBlock.getBlockData();

            if (pistonBlockData instanceof Piston && ((Piston) pistonBlockData).getFacing() == piston.getFacing()) {
                this.blockState = pistonBlock.getState();
                this.dependents.add(blockState);
            }

        } else {
            throw new IllegalArgumentException("Invalid BlockData: " + blockData.getClass().getName());
        }
    }

}
