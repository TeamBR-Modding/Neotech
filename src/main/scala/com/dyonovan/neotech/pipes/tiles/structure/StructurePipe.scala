package com.dyonovan.neotech.pipes.tiles.structure

import com.dyonovan.neotech.pipes.blocks.{BlockPipeSpecial, BlockPipe, PipeProperties}
import com.dyonovan.neotech.pipes.types.SimplePipe
import mcmultipart.block.TileCoverable
import net.minecraft.util.EnumFacing

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis pauljoda
  * @since August 15, 2015
  */
class StructurePipe extends TileCoverable with SimplePipe {
    /**
      * Used as a simple check to see if the pipe can connect. At it's most basic, it just checks if the tile in that
      * direction is a pipe. This is mainly used for path finding but also on the renderer
      *
      * @param facing The direction from this block
      * @return
      */
    override def canConnect(facing: EnumFacing): Boolean = {
        worldObj.getBlockState(pos.offset(facing)).getBlock match {
            case block: BlockPipe  => //We are checking if it and us are colored
                // Check for white
                if(worldObj.getBlockState(pos.offset(facing)).getValue(PipeProperties.COLOR).ordinal() == 0 ||
                    worldObj.getBlockState(pos).getValue(PipeProperties.COLOR).ordinal() == 0)
                    return true
                if (worldObj.getBlockState(pos.offset(facing)).getBlock.asInstanceOf[BlockPipe].colored)
                    worldObj.getBlockState(pos.offset(facing)).getValue(PipeProperties.COLOR).ordinal() ==
                            worldObj.getBlockState(pos).getValue(PipeProperties.COLOR).ordinal() && super.canConnect(facing)
                else
                    super.canConnect(facing)
            case advanced : BlockPipeSpecial => super.canConnect(facing)
            case _ => false
        }
    }
}
