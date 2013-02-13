package com.worldcretornica.pistonme;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class CustomBlockPistonExtendEvent extends BlockPistonExtendEvent 
{

	public CustomBlockPistonExtendEvent(Block block, int length, BlockFace direction) 
	{
		super(block, length, direction);
	}

}
