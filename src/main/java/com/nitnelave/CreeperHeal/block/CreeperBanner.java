package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import org.bukkit.Bukkit;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

/**
 * Banner implementation of CreeperBlock.
<<<<<<< /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperBanner.java/left.java
 *
 * @author Jikoo
||||||| /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperBanner.java/base.java
 * 
 * @author drexplosionpd
 * 
=======
 *
 * @author drexplosionpd
>>>>>>> /usr/src/app/output/nitnelave/creeperheal/a8f5cf9a61b44a1c73353cfe0ba1bc9e341fd402/src/main/java/com/nitnelave/CreeperHeal/block/CreeperBanner.java/right.java
 */
public class CreeperBanner extends CreeperBlock
{

    /*
     * Constructor.
     */
    CreeperBanner(Banner banner)
    {
        super(banner);
    }

    /*
     * @see com.nitnelave.CreeperHeal.block.Replaceable#drop(boolean)
     */
    @Override
    public boolean drop(boolean forced)
    {
        if (forced || CreeperConfig.shouldDrop())
        {
            ItemStack itemStack = new ItemStack(blockState.getType());
            BannerMeta bannerMeta = ((BannerMeta) Bukkit.getItemFactory().getItemMeta(blockState.getType()));
            bannerMeta.setPatterns(((Banner) blockState).getPatterns());
            itemStack.setItemMeta(bannerMeta);
            blockState.getWorld().dropItemNaturally(blockState.getLocation().add(0.5, 0.5, 0.5), itemStack);
            return true;
        }
        return false;
    }

}
