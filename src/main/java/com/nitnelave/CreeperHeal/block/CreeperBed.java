package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;

import java.util.Collections;
import java.util.List;

/**
 * Bed implementation of CreeperMultiblock.
 *
 * @author Jikoo
 */
class CreeperBed extends CreeperMultiblock
{

    CreeperBed(BlockState blockState)
    {
<<<<<<< /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperBed.java/left.java
        super(blockState);

        BlockData blockData = blockState.getBlockData();
        if (!(blockData instanceof Bed))
            throw new IllegalArgumentException("Invalid BlockData: " + blockData.getClass().getName());

        Bed bed = ((Bed) blockData);
        BlockState head, foot;

        if (bed.getPart() == Bed.Part.HEAD)
        {
            head = blockState;
            foot = blockState.getBlock().getRelative(bed.getFacing().getOppositeFace()).getState();
        }
        else
        {
            head = blockState.getBlock().getRelative(bed.getFacing()).getState();
            foot = blockState;
        }

        // Ensure materials match.
        if (head.getType() != foot.getType())
            return;

        BlockData headData = head.getBlockData();
        BlockData footData = foot.getBlockData();

        // Ensure bed halves are correct.
        if (!(headData instanceof Bed) || !(footData instanceof Bed)
                || ((Bed) headData).getPart() == ((Bed) footData).getPart())
            return;

        this.blockState = head;
        this.dependents.add(foot);
||||||| /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperBed.java/base.java
        Bed bedData = castData(blockState, Bed.class);
        orientation = bedData.getFacing();
        Block block = blockState.getBlock();
        if (!bedData.isHeadOfBed())
            block = block.getRelative(orientation);
        this.blockState = block.getState();
=======
        super(blockState);
        Bed bedData = castData(blockState, Bed.class);
        orientation = bedData.getFacing();
        Block block = blockState.getBlock();
        if (!bedData.isHeadOfBed())
            block = block.getRelative(orientation);
        this.blockState = block.getState();
>>>>>>> /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperBed.java/right.java
    }

    @Override
    public List<NeighborBlock> getDependentNeighbors()
    {
        return Collections.emptyList();
    }

}
