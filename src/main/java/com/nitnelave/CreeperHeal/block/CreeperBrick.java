package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

class CreeperBrick extends CreeperBlock
{

    CreeperBrick(BlockState blockState)
    {
        super(blockState);

<<<<<<< /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperBrick.java/left.java
        if (CreeperConfig.getBool(CfgVal.CRACK_DESTROYED_BRICKS))
||||||| /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperBrick.java/base.java
        if (CreeperConfig.getBool(CfgVal.CRACK_DESTROYED_BRICKS) && getRawData() == (byte) 0)
=======
        if (CreeperConfig.getBool(CfgVal.CRACK_DESTROYED_BRICKS) && blockState.getRawData() == (byte) 0)
>>>>>>> /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperBrick.java/right.java
            blockState.setType(Material.CRACKED_STONE_BRICKS);
    }

}
