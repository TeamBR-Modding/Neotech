package com.teambrmodding.neotech.common.tiles.machines.operators;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.GuiTextFormat;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambr.bookshelf.util.InventoryUtils;
import com.teambr.bookshelf.util.TimeUtils;
import com.teambrmodding.neotech.client.gui.machines.operators.GuiTreeFarm;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.operators.ContainerTreeFarm;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/16/2017
 */
public class TileTreeFarm extends AbstractMachine {
    public static final int AXE_SLOT    = 0;
    public static final int SHEARS_SLOT = 1;

    protected final int SAPLING_SLOTS_START = getInitialSize() - 3;
    protected final int SAPLING_SLOTS_END   =  getInitialSize();

    protected boolean isBuildingCache;
    Queue<BlockPos> cache = new PriorityQueue<>(new Comparator<BlockPos>() {
        @Override
        public int compare(BlockPos o1, BlockPos o2) {
            return Double.compare(o1.distanceSq(pos.getX(), pos.getY(), pos.getZ()),
                    o2.distanceSq(pos.getX(), pos.getY(), pos.getZ()));
        }
    });

    /**
     * Used to get how far this farm can chop
     * @return How many blocks to move from center
     */
    protected int getChoppingRange() {
        return 4 * getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY);
    }

    /**
     * How much energy this costs to chop
     * @return Energy cost
     */
    protected int costToOperate() {
        return 200* getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY);
    }

    /**
     * Get how many blocks this machine can cut
     * @return The blocks to chop
     */
    protected int getChopCount() {
        return 2 * getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU);
    }

    /**
     * The initial size of the inventory
     */
    @Override
    public int getInitialSize() {
        return 18;
    }

    /**
     * Add all modes you want, in order, here
     */
    @Override
    public void addValidModes() {
        validModes.add(EnumInputOutputMode.INPUT_ALL);
        validModes.add(EnumInputOutputMode.INPUT_PRIMARY);
        validModes.add(EnumInputOutputMode.INPUT_SECONDARY);
        validModes.add(EnumInputOutputMode.OUTPUT_ALL);
        validModes.add(EnumInputOutputMode.ALL_MODES);
    }

    /**
     * Return the list of upgrades by their id that are allowed in this machine
     *
     * @return A list of valid upgrades
     */
    @Override
    public List<String> getAcceptableUpgrades() {
        List<String> list = new ArrayList<>();
        list.add(IUpgradeItem.CPU_SINGLE_CORE);
        list.add(IUpgradeItem.CPU_DUAL_CORE);
        list.add(IUpgradeItem.CPU_QUAD_CORE);
        list.add(IUpgradeItem.CPU_OCT_CORE);
        list.add(IUpgradeItem.MEMORY_DDR1);
        list.add(IUpgradeItem.MEMORY_DDR2);
        list.add(IUpgradeItem.MEMORY_DDR3);
        list.add(IUpgradeItem.MEMORY_DDR4);
        list.add(IUpgradeItem.PSU_250W);
        list.add(IUpgradeItem.PSU_500W);
        list.add(IUpgradeItem.PSU_750W);
        list.add(IUpgradeItem.PSU_960W);
        list.add(IUpgradeItem.TRANSFORMER);
        list.add(IUpgradeItem.REDSTONE_CIRCUIT);
        list.add(IUpgradeItem.NETWORK_CARD);
        return list;
    }

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Used to actually do the processes needed. For processors this should be cooking items and generators should
     * generate RF. This is called every tick allowed, provided redstone mode requirements are met
     */
    @Override
    protected void doWork() {
        if(!isBuildingCache && TimeUtils.onSecond(2) && energyStorage.getEnergyStored() > costToOperate()) {
            if(cache.isEmpty())
                findNextTree();
            else
                chopTree();
            pullInSaplings();
        }

        if(TimeUtils.onSecond(30))
            plantSaplings();
    }

    /**
     * Finds the next tree base and fills the cache with blocks to chop
     */
    protected void findNextTree() {
        isBuildingCache = true;

        BlockPos corner1 = new BlockPos(pos.getX() - getChoppingRange(), pos.getY(), pos.getZ() - getChoppingRange() + 1);
        BlockPos corner2 = new BlockPos(pos.getX() + getChoppingRange(), pos.getY(), pos.getZ() + getChoppingRange());

        BlockPos logPosition = null;

        // Find the base of a tree
        Iterable<BlockPos> blocksWithin = BlockPos.getAllInBox(corner1, corner2);
        for(BlockPos blockPos : blocksWithin) {
            if(worldObj.getBlockState(blockPos).getBlock().isWood(worldObj, blockPos)) {
                logPosition = blockPos;
                break;
            }
        }

        // Graph Search to add tree
        if(logPosition != null) {
            Stack<BlockPos> treeStack = new Stack<>();
            treeStack.push(logPosition);
            while(!treeStack.isEmpty()) {
                BlockPos lookingPosition = treeStack.pop();
                if(worldObj.getBlockState(lookingPosition).getBlock().isWood(worldObj, lookingPosition) ||
                        worldObj.getBlockState(lookingPosition).getBlock() instanceof BlockLeaves) {
                    Iterable<BlockPos> blockAround = BlockPos.getAllInBox(lookingPosition.offset(EnumFacing.DOWN).offset(EnumFacing.SOUTH),
                            lookingPosition.offset(EnumFacing.UP).offset(EnumFacing.NORTH));
                    for(BlockPos attachedPos : blockAround) {
                        if (!cache.contains(attachedPos) &&
                                attachedPos.distanceSq(pos.getX(), pos.getY(), pos.getZ()) <= 1000 &&
                                (worldObj.getBlockState(attachedPos).getBlock().isWood(worldObj, attachedPos) ||
                                    worldObj.getBlockState(attachedPos).getBlock() instanceof BlockLeaves)) {
                            treeStack.push(attachedPos);
                            cache.add(attachedPos);
                        }
                    }
                }
            }
        }

        isBuildingCache = false;
    }

    /**
     * Takes a chunk of the cache and chops it
     */
    protected void chopTree() {
        for(int x = 0; x < getChopCount(); x++) {
            if(cache.isEmpty())
                break;
            else {
                BlockPos logPosition = cache.peek();
                if(worldObj.getBlockState(logPosition).getBlock() != null) {
                    if(worldObj.getBlockState(logPosition).getBlock().isWood(worldObj, logPosition)) {
                        if(chopBlock(logPosition, AXE_SLOT))
                            cache.poll();
                        else
                            break;
                    } else if(worldObj.getBlockState(logPosition).getBlock() instanceof BlockLeaves) {
                        if(chopBlock(logPosition, SHEARS_SLOT))
                            cache.poll();
                        else
                            break;
                    } else
                        cache.poll();
                }
                else
                    cache.poll();
            }
        }
    }

    /**
     * Chop a block
     *
     * @param blockPosition The log position
     * @return True if able to chop
     */
    protected boolean chopBlock(BlockPos blockPosition, int slot) {
        if(getStackInSlot(slot) != null) {
            List<ItemStack> drops;
            if(slot == AXE_SLOT) {
                drops =
                        worldObj.getBlockState(blockPosition).getBlock().getDrops(worldObj, blockPosition,
                                worldObj.getBlockState(blockPosition),
                                EnchantmentHelper.getEnchantmentLevel(
                                        Enchantment.getEnchantmentByLocation("fortune"),
                                        getStackInSlot(slot)));
            } else // We have shears in the slot
                drops = Collections.singletonList(new ItemStack(worldObj.getBlockState(blockPosition).getBlock(), 1,
                        worldObj.getBlockState(blockPosition).getBlock().damageDropped(worldObj.getBlockState(blockPosition))));

            boolean blockAddedToInv = slot != SHEARS_SLOT; // We don't care for leaves, always break
            for(ItemStack drop : drops) {
                if(addHarvestToInventory(drop))
                    blockAddedToInv = true;
            }

            if(blockAddedToInv) {
                worldObj.setBlockToAir(blockPosition);
                if(getStackInSlot(slot).attemptDamageItem(1, worldObj.rand))
                    setStackInSlot(slot, null);
                energyStorage.providePower(costToOperate(), true);
                sendValueToClient(UPDATE_ENERGY_ID, energyStorage.getEnergyStored());
                return true;
            }
        }
        return false;
    }

    /**
     * Pulls in items on ground, in case some decay and drop items
     */
    protected void pullInSaplings() {
        List<EntityItem> itemsOnGround =
                worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(
                        pos.getX() - getChoppingRange() - 4, pos.getY(), pos.getZ() - getChoppingRange() - 4,
                        pos.getX() + getChoppingRange() + 5, pos.getY() + 2, pos.getZ() + getChoppingRange() + 5));

        for(EntityItem item : itemsOnGround) {
            if(addHarvestToInventory(item.getEntityItem())) {
                item.setDead();
            }
        }
    }

    /**
     * Plants saplings
     */
    protected void plantSaplings() {
        if(hasSaplings()) {
            for(int x = pos.getX() - getChoppingRange() + 1; x < pos.getX() + getChoppingRange(); x++) {
                for(int z = pos.getZ() - getChoppingRange() + 1; z < pos.getZ() + getChoppingRange(); z++) {
                    BlockPos blockPos = new BlockPos(x, pos.getY(), z);
                    if(worldObj.isAirBlock(blockPos) && worldObj.getBlockState(blockPos.down()) != null &&
                            (worldObj.getBlockState(blockPos.down()).getBlock() == Blocks.DIRT ||
                            worldObj.getBlockState(blockPos.down()).getBlock() == Blocks.GRASS)) {
                            IBlockState blockState = getNextSaplingAndReduce();
                            if(blockState != null)
                                worldObj.setBlockState(blockPos, blockState);
                            else
                                return;
                    }
                }
            }
        }
    }

    /**
     * Checks if we have sapling to plant
     * @return Do we have things to plant
     */
    protected boolean hasSaplings() {
        for(int x = SAPLING_SLOTS_START; x < SAPLING_SLOTS_END; x++)
            if(getStackInSlot(x) != null)
                return true;
        return false;
    }

    /**
     * Gets the block state to place and reduces the stack
     * @return The state to put
     */
    @Nullable
    protected IBlockState getNextSaplingAndReduce() {
        for(int x = SAPLING_SLOTS_START; x < SAPLING_SLOTS_END; x++) {
            if(getStackInSlot(x) != null) {
                Block block = Block.getBlockFromItem(getStackInSlot(x).getItem());
                int damage = getStackInSlot(x).getItemDamage();
                getStackInSlot(x).stackSize -= 1;
                if(getStackInSlot(x).stackSize <= 0)
                    setStackInSlot(x, null);
                return block.getStateFromMeta(damage);
            }
        }
        return null;
    }

    /**
     * Adds the harvest of the block to the inventory
     * @param stack  The stack
     * @return
     */
    protected boolean addHarvestToInventory(ItemStack stack) {
        boolean sapling = stack.getItem() instanceof IGrowable;
        if(sapling) {
            for(int x = SAPLING_SLOTS_START; x < SAPLING_SLOTS_END; x++) {
                if(getStackInSlot(x) == null) {
                    setStackInSlot(x, stack);
                    return true;
                } else if(InventoryUtils.canStacksMerge(getStackInSlot(x), stack)) {
                    InventoryUtils.tryMergeStacks(stack, getStackInSlot(x));
                    if(stack.stackSize <= 0)
                        return true;
                }
            }
        }

        for (int x = 3; x < SAPLING_SLOTS_START; x++) {
            if(getStackInSlot(x) == null) {
                setStackInSlot(x, stack);
                return true;
            } else if(InventoryUtils.canStacksMerge(getStackInSlot(x), stack)) {
                InventoryUtils.tryMergeStacks(stack, getStackInSlot(x));
                if(stack.stackSize <= 0)
                    return true;
            }
        }
        return false;
    }

    /**
     * This will try to take things from other inventories and put it into ours
     */
    @Override
    public void tryInput() {
        for(EnumFacing dir : EnumFacing.values()) {
            if(canInputFromSide(dir, true))
                InventoryUtils.moveItemInto(worldObj.getTileEntity(pos.offset(dir)), -1, this, AXE_SLOT,
                        64, dir.getOpposite(), true, true, false);
            if(canInputFromSide(dir, false))
                InventoryUtils.moveItemInto(worldObj.getTileEntity(pos.offset(dir)), -1, this, SHEARS_SLOT,
                        64, dir.getOpposite(), true, true, false);
        }
    }

    /**
     * This will try to take things from our inventory and try to place them in others
     */
    @Override
    public void tryOutput() {
        for(EnumFacing dir : EnumFacing.values()) {
            if(canOutputFromSide(dir, true))
                for(Integer x : getOutputSlots(getModeForSide(dir))) {
                    InventoryUtils.moveItemInto(this, x, worldObj.getTileEntity(pos.offset(dir)), -1,
                            64, dir.getOpposite(), true, false, true);
                }
        }
    }

    /**
     * Use this to set all variables back to the default values, usually means the operation failed
     */
    @Override
    public void reset() {

    }

    /**
     * Used to expose the item handler capability, in child classes, manage input and output based on this
     *
     * @param dir The direction
     * @return The item handler
     */
    @Nullable
    @Override
    protected IItemHandler getItemHandlerCapability(EnumFacing dir) {
        return !isDisabled(dir) && (canInputFromSide(dir, true) || canInputFromSide(dir, false)) ?
                super.getItemHandlerCapability(dir) : null;
    }

    /*******************************************************************************************************************
     * Inventory Methods                                                                                               *
     *******************************************************************************************************************/

    /**
     * Used to get what slots are allowed to be input
     *
     * @param mode
     * @return The slots to input from
     */
    @Override
    public int[] getInputSlots(EnumInputOutputMode mode) {
        switch (mode) {
            case INPUT_PRIMARY:
                return new int[] { AXE_SLOT };
            case INPUT_SECONDARY:
                return new int[] { SHEARS_SLOT };
            case DEFAULT:
            case INPUT_ALL:
                return new int[] { AXE_SLOT, SHEARS_SLOT };
            default:
                return new int[0];
        }
    }

    /**
     * Used to get what slots are allowed to be output
     *
     * @param mode
     * @return The slots to output from
     */
    @Override
    public int[] getOutputSlots(EnumInputOutputMode mode) {
        int[] returnArray =  new int[getInitialSize() - 5];
        for(int x = 0; x < returnArray.length; x++)
            returnArray[x] = x + 2;
        return returnArray;
    }

    /**
     * Get the slots for the given face
     *
     * @param face The face
     * @return What slots can be accessed
     */
    @Override
    public int[] getSlotsForFace(EnumFacing face) {
        int[] returnArray =  new int[getInitialSize() - 5];
        for(int x = 0; x < returnArray.length; x++)
            returnArray[x] = x;
        return returnArray;
    }

    /**
     * Can this extract the item
     *
     * @param slot  The slot
     * @param stack The stack
     * @param dir   The dir
     * @return True if can extract
     */
    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing dir) {
        return !isDisabled(dir) && canOutputFromSide(dir, true) &&
                slot > SHEARS_SLOT && slot < getInitialSize() - 3;
    }

    /**
     * Can insert the item into the inventory
     *
     * @param slot        The slot
     * @param itemStackIn The stack to insert
     * @param dir         The dir
     * @return True if can insert
     */
    @Override
    public boolean canInsertItem(int slot, ItemStack itemStackIn, EnumFacing dir) {
        return !isDisabled(dir) && (canInputFromSide(dir, true) || canInputFromSide(dir, false))
                && isItemValidForSlot(slot, itemStackIn);
    }

    /**
     * Used to define if an item is valid for a slot
     *
     * @param index The slot id
     * @param stack The stack to check
     * @return True if you can put this there
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if(index >= getInitialSize() - 3) { // Check if sapling
            return stack != null && stack.getItem() != null &&
                    Block.getBlockFromItem(stack.getItem()) instanceof IGrowable;
        }

        // Can only be tool then
        switch (index) {
            case AXE_SLOT:
                return stack != null && stack.getItem().getToolClasses(stack).contains("axe");
            case SHEARS_SLOT:
                return stack != null && stack.getItem() instanceof ItemShears;
            default:
                return false;
        }
    }

    /*******************************************************************************************************************
     * Energy Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to define the default size of this energy bank
     *
     * @return The default size of the energy bank
     */
    @Override
    protected int getDefaultEnergyStorageSize() {
        return 10000;
    }

    /**
     * Is this tile an energy provider
     *
     * @return True to allow energy out
     */
    @Override
    protected boolean isProvider() {
        return false;
    }

    /**
     * Is this tile an energy reciever
     *
     * @return True to accept energy
     */
    @Override
    protected boolean isReceiver() {
        return true;
    }

    /*******************************************************************************************************************
     * Misc Methods                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Return the container for this tile
     *
     * @param id       Id, probably not needed but could be used for multiple guis
     * @param player   The player that is opening the gui
     * @param worldObj The worldObj
     * @param x        X Pos
     * @param y        Y Pos
     * @param z        Z Pos
     * @return The container to open
     */
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World worldObj, int x, int y, int z) {
        return new ContainerTreeFarm(player.inventory, this);
    }

    /**
     * Return the gui for this tile
     *
     * @param id       Id, probably not needed but could be used for multiple guis
     * @param player   The player that is opening the gui
     * @param worldObj The worldObj
     * @param x        X Pos
     * @param y        Y Pos
     * @param z        Z Pos
     * @return The gui to open
     */
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World worldObj, int x, int y, int z) {
        return new GuiTreeFarm(player, this);
    }

    /**
     * Used to get the description to display on the tab
     *
     * @return The long string with the description
     */
    @Override
    public String getDescription() {
        return "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.energyUsage") + ":\n" +
                GuiColor.WHITE + "  " + costToOperate() + " RF/chop\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.rangeTree") + ":\n" +
                GuiColor.WHITE + "  " + (getChoppingRange() + 1) + "x"  + (getChoppingRange() + 1) + " blocks\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.chopCount") + ":\n" +
                GuiColor.WHITE + "  " + getChopCount() + "  \n\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.treeFarm.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.upgrade") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.treeFarm.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.memory") + ":\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.treeFarm.memoryUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.psu") + ":\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.electricFurnace.psuUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.control") + ":\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.electricFurnace.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.network") + ":\n" +
                GuiColor.WHITE +  ClientUtils.translate("neotech.electricFurnace.networkUpgrade.desc");
    }

    /**
     * Used to output the redstone single from this structure
     * <p>
     * Use a range from 0 - 16.
     * <p>
     * 0 Usually means that there is nothing in the tile, so take that for lowest level. Like the generator has no energy while
     * 16 is usually the flip side of that. Output 16 when it is totally full and not less
     *
     * @return int range 0 - 16
     */
    @Override
    public int getRedstoneOutput() {
        return (energyStorage.getEnergyStored() * 16) / energyStorage.getMaxEnergyStored();
    }

    /**
     * Used to get what particles to spawn. This will be called when the tile is active
     *
     * @param xPos
     * @param yPos
     * @param zPos
     */
    @Override
    public void spawnActiveParticles(double xPos, double yPos, double zPos) {

    }

    /**
     * Used to check if this tile is active or not
     *
     * @return True if active state
     */
    @Override
    public boolean isActive() {
        return false;
    }
}
