package com.worldcretornica.pistonme;

import java.util.Random;

import net.minecraft.server.v1_4_R1.BlockPistonMoving;
import net.minecraft.server.v1_4_R1.TileEntity;
import net.minecraft.server.v1_4_R1.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class PistonMe extends JavaPlugin implements Listener
{


	public void onEnable() 
	{
		getServer().getPluginManager().registerEvents(this, this);
	}
	

	public void onDisable() 
	{

	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onBlockPistonExtendEvent(final BlockPistonExtendEvent event)
	{
		if(!(event instanceof CustomBlockPistonExtendEvent))
		{			
			event.setCancelled(true);
			CustomBlockPistonExtendEvent newevent = new CustomBlockPistonExtendEvent(event.getBlock(), event.getLength(), event.getDirection());
            getServer().getPluginManager().callEvent(newevent);
			
            if(!newevent.isCancelled())
            {
            	moveBlocks(event.getBlock());
            }
		}
	}	
	
	
	public void moveBlocks(Block block) 
	{		
        if (testeachblocks(block)) 
        {
        	block.setData((byte) (block.getData() | 8));
        	
            Random rand = new Random(block.getWorld().getSeed());

            block.getWorld().playSound(
            		new Location(block.getWorld(), (double) block.getX() + 0.5D, (double) block.getY() + 0.5D, (double) block.getZ() + 0.5D), 
            		Sound.PISTON_EXTEND, 0.5F, rand.nextFloat() * 0.25F + 0.6F);
        } 
    }
	
	private boolean testeachblocks(Block block) 
	{
        int xmod = 0;
        int ymod = 0;
        int zmod = 0;
        
        switch(block.getData())
        {
        case 0: ymod -= 1; break;
        case 1: ymod += 1; break;
        case 2: zmod -= 1; break;
        case 3: zmod += 1; break;
        case 4: xmod -= 1; break;
        case 5: xmod += 1; break;
        default: return false;
        }
        
        int nbBlocksPushed = 1;
        boolean foundair = false;
        
        //Test the blocks
        while (nbBlocksPushed < 13 && !foundair) 
        {                  	
            Block currentblock = block.getWorld().getBlockAt(block.getX() + (xmod * (nbBlocksPushed)), block.getY() + (ymod * (nbBlocksPushed)), block.getZ() + (zmod * (nbBlocksPushed)));

            if (currentblock.getY() <= 0 || currentblock.getY() >= 255) 
            {
                return false;
            }

            //if (currentblock.getType() != Material.AIR) 
            //{
                if (!canPush(currentblock, true)) 
                    return false;

                if (nbBlocksPushed == 12) 
                	foundair = true; //return false;
                
                ++nbBlocksPushed;
            //}
            //else
            //{
            //	foundair = true;
            //}
        }
                
        //Move the blocks
        while (nbBlocksPushed >= 0) 
        {       	
        	Block currentblock = block.getWorld().getBlockAt(block.getX() + xmod * nbBlocksPushed, block.getY() + ymod * nbBlocksPushed, block.getZ() + zmod * nbBlocksPushed);

            Material mat = currentblock.getType();
            byte data = currentblock.getData();

            WorldServer w = ((CraftWorld) block.getWorld()).getHandle();
            
            if (mat.equals(block.getType()) && currentblock.getLocation().distance(block.getLocation()) == 0) 
            {
                w.setRawTypeIdAndData(currentblock.getX() + xmod, currentblock.getY() + ymod, currentblock.getZ() + zmod, net.minecraft.server.v1_4_R1.Block.PISTON_MOVING.id, block.getData() | 0, false);

                TileEntity te = BlockPistonMoving.a(net.minecraft.server.v1_4_R1.Block.PISTON_EXTENSION.id, block.getData() | 0, block.getData(), true, false);
                
            	w.setTileEntity(currentblock.getX() + xmod, currentblock.getY() + ymod, currentblock.getZ() + zmod, te);
            } 
            else 
            {
                w.setRawTypeIdAndData(currentblock.getX() + xmod, currentblock.getY() + ymod, currentblock.getZ() + zmod, net.minecraft.server.v1_4_R1.Block.PISTON_MOVING.id, data, false);

                TileEntity te = BlockPistonMoving.a(mat.getId(), data, block.getData(), true, false);

                w.setTileEntity(currentblock.getX() + xmod, currentblock.getY() + ymod, currentblock.getZ() + zmod, te);
            }

            nbBlocksPushed--;
        }

        return true;
    }

	
	private boolean canPush(Block block, boolean flag)
	{
		Material mat = block.getType();
		
		if (mat == Material.OBSIDIAN || 
				mat == Material.PORTAL || 
				mat == Material.ENDER_PORTAL || 
				mat == Material.ENDER_PORTAL_FRAME || 
				mat == Material.BEDROCK) 
		{
            return false;
        } 
		else 
        {
            if (block.getType() != Material.PISTON_BASE && block.getType() != Material.PISTON_STICKY_BASE) 
            {
                if (!flag) 
                {
                    return false;
                }
            } 
            else if ((block.getData() & 8) != 0) 
            {
                return false;
            }

            return !(block.getState() instanceof InventoryHolder);
        }
	}
}
