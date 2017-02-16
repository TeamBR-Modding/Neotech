package com.teambrmodding.neotech.common.tiles;

import com.teambr.bookshelf.common.container.IInventoryCallback;
import com.teambr.bookshelf.common.tiles.EnergyHandler;
import com.teambr.bookshelf.common.tiles.IRedstoneAware;
import com.teambr.bookshelf.common.tiles.InventoryHandler;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.CapabilityLoadManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import static com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem.ENUM_UPGRADE_CATEGORY.*;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public abstract class AbstractMachine extends EnergyHandler implements IRedstoneAware, IItemHandlerModifiable, IFluidHandler {

    /*******************************************************************************************************************
     * AbstractMachine Variables                                                                                       *
     *******************************************************************************************************************/

    // Current Redstone Mode
    public int redstone    = 0;
    // Working variable
    public boolean working = false;

    // NBT Tags
    public static final String REDSTONE_NBT      = "RedstoneMode";
    public static final String UPDATE_ENERGY_NBT = "UpdateEnergy";
    public static final String SIDE_MODE_NBT     = "Side: ";

    // Syncing Variables
    public static final int REDSTONE_FIELD_ID = 0;
    public static final int IO_FIELD_ID       = 1;
    public static final int UPDATE_CLIENT_ID  = 2;

    /*******************************************************************************************************************
     * Input Output Variables                                                                                          *
     *******************************************************************************************************************/

    // Holds valid modes for this instance
    public List<EnumInputOutputMode> validModes = new ArrayList<>();
    // Holds the current mode for each side
    public HashMap<EnumFacing, EnumInputOutputMode> sideModes = new HashMap<>();

    /*******************************************************************************************************************
     * Inventory Variables                                                                                             *
     *******************************************************************************************************************/

    // A list to hold all callback objects
    private List<IInventoryCallback> inventoryCallbacks = new ArrayList<>();
    // List of Inventory contents
    public Stack<ItemStack> inventoryContents = new Stack<>();

    // NBT Tags
    protected static final String SIZE_INVENTORY_NBT_TAG = "Size:";
    protected static final String SLOT_INVENTORY_NBT_TAG = "Slot:";
    protected static final String ITEMS_NBT_TAG          = "Items:";

    // Side Handlers
    private IItemHandler handlerTop    = new AbstractMachineSidedWrapper(this, EnumFacing.UP);
    private IItemHandler handlerDown   = new AbstractMachineSidedWrapper(this, EnumFacing.DOWN);
    private IItemHandler handlerNorth  = new AbstractMachineSidedWrapper(this, EnumFacing.NORTH);
    private IItemHandler handlerSouth  = new AbstractMachineSidedWrapper(this, EnumFacing.SOUTH);
    private IItemHandler handlerEast   = new AbstractMachineSidedWrapper(this, EnumFacing.EAST);
    private IItemHandler handlerWest   = new AbstractMachineSidedWrapper(this, EnumFacing.WEST);

    /*******************************************************************************************************************
     * FluidHandler Variables                                                                                          *
     *******************************************************************************************************************/

    // NBT Tags
    protected static final String SIZE_FLUID_NBT_TAG = "Size";
    protected static final String TANK_ID_NBT_TAG = "TankID";
    protected static final String TANKS_NBT_TAG   = "Tanks";

    // Tanks
    public FluidTank[] tanks;

    /*******************************************************************************************************************
     * Upgradeable Variables                                                                                           *
     *******************************************************************************************************************/

    public InventoryHandler upgradeInventory = new InventoryHandler() {
        @Override
        protected int getInitialSize() {
            return 6;
        }

        @Override
        protected boolean isItemValidForSlot(int index, ItemStack stack) {
            return !hasUpgradeAlready(stack) && stack.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);
        }

        @Override
        public void setVariable(int id, double value) {}

        @Override
        public Double getVariable(int id) {
            return null;
        }
    };

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Main constructor, load things needed here
     */
    public AbstractMachine() {
        // Input Output
        setupValidModes();
        resetIO();

        // FluidHandler
        setupTanks();

        // Upgradeable
        // Add callback for our upgrade inventory
        upgradeInventory.addCallback(new IInventoryCallback() {
            @Override
            public void onInventoryChanged(IItemHandler inventory, int slotNumber) {
                if(inventory.getStackInSlot(slotNumber) == null)
                    resetValues();
                upgradeInventoryChanged(slotNumber);
            }
        });
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Used to get what particles to spawn. This will be called when the tile is active
     */
    public abstract void spawnActiveParticles(double xPos, double yPos, double zPos);

    /**
     * Used to get what slots are allowed to be output
     *
     * @return The slots to output from
     */
    public abstract int[] getOutputSlots(EnumInputOutputMode mode);

    /**
     * Used to get what slots are allowed to be input
     *
     * @return The slots to input from
     */
    public abstract int[] getInputSlots(EnumInputOutputMode mode);

    /**
     * Used to output the redstone single from this structure
     *
     * Use a range from 0 - 16.
     *
     * 0 Usually means that there is nothing in the tile, so take that for lowest level. Like the generator has no energy while
     * 16 is usually the flip side of that. Output 16 when it is totally full and not less
     *
     * @return int range 0 - 16
     */
    public abstract int getRedstoneOutput();

    /**
     * Used to define the base energy storage size
     * @return Starting energy size
     */
    public abstract int getBaseEnergySize();

    /**
     * Used to actually do the processes needed. For processors this should be cooking items and generators should
     * generate RF. This is called every tick allowed, provided redstone mode requirements are met
     */
    protected abstract void doWork();

    /**
     * Use this to set all variables back to the default values, usually means the operation failed
     */
    public abstract void reset();

    /**
     * Used to check if this tile is active or not
     *
     * @return True if active state
     */
    public abstract boolean isActive();

    /**
     * The initial size of the inventory
     */
    public abstract int getInitialSize();

    /**
     * Used to define if an item is valid for a slot
     *
     * @param index The slot id
     * @param stack The stack to check
     * @return True if you can put this there
     */
    public abstract boolean isItemValidForSlot(int index, ItemStack stack);

    /**
     * Get the slots for the given face
     * @param face The face
     * @return What slots can be accessed
     */
    public abstract int[] getSlotsForFace(EnumFacing face);

    /**
     * Can insert the item into the inventory
     * @param slot The slot
     * @param itemStackIn The stack to insert
     * @param dir The dir
     * @return True if can insert
     */
    public abstract boolean canInsertItem(int slot, ItemStack itemStackIn, EnumFacing dir);

    /**
     * Can this extract the item
     * @param slot The slot
     * @param stack The stack
     * @param dir The dir
     * @return True if can extract
     */
    public abstract boolean canExtractItem(int slot, ItemStack stack, EnumFacing dir);

    /**
     * Add all modes you want, in order, here
     */
    public abstract void addValidModes();

    /**
     * This will try to take things from other inventories and put it into ours
     */
    public abstract void tryInput();

    /**
     * This will try to take things from our inventory and try to place them in others
     */
    public abstract void tryOutput();

    /**
     * Used to get the description to display on the tab
     * @return The long string with the description
     */
    public abstract String getDescription();

    /**
     * Return the container for this tile
     *
     * @param id Id, probably not needed but could be used for multiple guis
     * @param player The player that is opening the gui
     * @param worldObj The worldObj
     * @param x X Pos
     * @param y Y Pos
     * @param z Z Pos
     * @return The container to open
     */
    public abstract Object getServerGuiElement(int id, EntityPlayer player, World worldObj, int x, int y, int z);

    /**
     * Return the gui for this tile
     *
     * @param id Id, probably not needed but could be used for multiple guis
     * @param player The player that is opening the gui
     * @param worldObj The worldObj
     * @param x X Pos
     * @param y Y Pos
     * @param z Z Pos
     * @return The gui to open
     */
    public abstract Object getClientGuiElement(int id, EntityPlayer player, World worldObj, int x, int y, int z);

    /*******************************************************************************************************************
     * TileEntity Methods                                                                                              *
     *******************************************************************************************************************/

    // Make sure we don't keep rendering back and forth
    private int updateCooldown = 60;

    @Override
    protected void onClientTick() {
        if(getSupposedEnergy() != energyStorage.getMaxStored())
            sendValueToClient(UPDATE_CLIENT_ID, 0);

        // Mark for render update if needed
        updateCooldown -= 1;
        if(updateCooldown < 0 && working != isActive()) {
            markForUpdate(3);
            working = isActive();
            updateCooldown = 60;
        }
    }

    private int timeTicker = 0;

    @Override
    protected void onServerTick() {
        super.onServerTick();
        // Make sure our energy storage is correct
        if(getSupposedEnergy() != energyStorage.getMaxStored())
            changeEnergy(energyStorage.getEnergyStored());

        // If redstone mode is not matched, break our of update
        if(hasUpgradeByID(IUpgradeItem.REDSTONE_CIRCUIT)) {
            if(redstone == -1 && isPowered())
                return;
            else if(redstone == 1 && !isPowered())
                return;
        }

        // We want to try automatic IO if we are able to
        if(shouldHandleIO() && timeTicker <= 0 && hasUpgradeByID(IUpgradeItem.NETWORK_CARD)) {
            timeTicker = 20;
            tryInput();
            tryOutput();
        }
        timeTicker -= 1;

        // Do the work
        doWork();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        upgradeInventory.writeToNBT(compound, "upgrade");

        // Inventory
        String inventoryName = "";
        // Set the size
        compound.setInteger(SIZE_INVENTORY_NBT_TAG + inventoryName, inventoryContents.size());

        // Write the inventory
        NBTTagList tagList = new NBTTagList();
        for(int i = 0; i < inventoryContents.size(); i++) {
            if(inventoryContents.get(i) != null) {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte(SLOT_INVENTORY_NBT_TAG + inventoryName, (byte) i);
                inventoryContents.get(i).writeToNBT(stackTag);
                tagList.appendTag(stackTag);
            }
        }
        compound.setTag(ITEMS_NBT_TAG + inventoryName, tagList);

        // FluidHandler
        if(isFluidHandler()) {
            int id = 0;
            compound.setInteger(SIZE_FLUID_NBT_TAG, tanks.length);
            NBTTagList tagListFluid = new NBTTagList();
            for(FluidTank tank : tanks) {
                if(tank != null) {
                    NBTTagCompound tankCompound = new NBTTagCompound();
                    tankCompound.setByte(TANK_ID_NBT_TAG, (byte) id);
                    id += 1;
                    tank.writeToNBT(tankCompound);
                    tagListFluid.appendTag(tankCompound);
                }
            }
            compound.setTag(TANKS_NBT_TAG, tagListFluid);
        }

        // Input Output
        for(EnumFacing side : EnumFacing.values())
            compound.setInteger(SIDE_MODE_NBT + side.ordinal(), sideModes.get(side).getIntValue());

        // Abstract Machine
        compound.setInteger(REDSTONE_NBT, redstone);
        if(updateClient && worldObj != null) {
            compound.setBoolean(UPDATE_ENERGY_NBT, true);
            updateClient = false;
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        upgradeInventory.readFromNBT(compound, "upgrade");

        // Inventory
        String inventoryName = "";
        // Read Items
        NBTTagList tagList = compound.getTagList(ITEMS_NBT_TAG + inventoryName, 10);
        inventoryContents = new Stack<>();
        if(compound.hasKey(SIZE_INVENTORY_NBT_TAG + inventoryName))
            inventoryContents.setSize(compound.getInteger(SIZE_INVENTORY_NBT_TAG + inventoryName));
        for(int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound stackTag = tagList.getCompoundTagAt(i);
            int slot = stackTag.getByte(SLOT_INVENTORY_NBT_TAG + inventoryName);
            if(slot >= 0 && slot < inventoryContents.size())
                inventoryContents.set(slot, ItemStack.loadItemStackFromNBT(stackTag));
        }

        // FluidHandler
        if(isFluidHandler()) {
            NBTTagList tagListFluid = compound.getTagList(TANKS_NBT_TAG, 10);
            int size = compound.getInteger(SIZE_FLUID_NBT_TAG);
            if(size != tanks.length && compound.hasKey(SIZE_FLUID_NBT_TAG)) tanks = new FluidTank[size];
            for(int x = 0; x < tagListFluid.tagCount(); x++) {
                NBTTagCompound tankCompound = tagListFluid.getCompoundTagAt(x);
                byte position = tankCompound.getByte(TANK_ID_NBT_TAG);
                if(position < tanks.length)
                    tanks[position].readFromNBT(tankCompound);
            }
        }

        // Input Output
        for(EnumFacing side : EnumFacing.values())
            sideModes.put(side, EnumInputOutputMode.getModeFromInt(compound.getInteger(SIDE_MODE_NBT + side.ordinal())));

        // Abstract Machine
        if(compound.hasKey(UPDATE_ENERGY_NBT) && worldObj != null)
            changeEnergy(compound.getInteger("Energy"));
        redstone = compound.getInteger(REDSTONE_NBT);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return !isDisabled(facing) &&
                (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
                        (!isFluidHandler() || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) ||
                        super.hasCapability(capability, facing));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            switch (facing) {
                case UP :
                    return (T) handlerTop;
                case DOWN :
                    return (T) handlerDown;
                case NORTH :
                    return (T) handlerNorth;
                case SOUTH :
                    return (T) handlerSouth;
                case EAST :
                    return (T) handlerEast;
                case WEST :
                    return (T) handlerWest;
                default :
            }
        }
        if(isFluidHandler() && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this;
        }
        return super.getCapability(capability, facing);
    }

    /*******************************************************************************************************************
     * Energy Methods                                                                                                  *
     *******************************************************************************************************************/

    // Tag to let us know if we need to push update
    private boolean updateClient = false;

    /**
     * Used to change the energy to a new storage with a different size
     *
     * @param initial How much was in the old storage
     */
    public void changeEnergy(int initial) {
        energyStorage.setMaxStored(getSupposedEnergy());
        updateClient = true;
        energyStorage.setCurrentStored(initial);
        if(energyStorage.getCurrentStored() > energyStorage.getMaxEnergyStored())
            energyStorage.setCurrentStored(energyStorage.getMaxEnergyStored());
        markForUpdate(3);
    }

    /**
     * Used to determine how much energy should be in this tile
     *
     * @return How much energy should be available
     */
    public int getSupposedEnergy() {
        return getBaseEnergySize() * (getModifierForCategory(PSU) *
                (getModifierForCategory(PSU) == 1 ? 1 : 0));
    }

    /**
     * Used to get how much power maxed by tier
     * @param tier Tier
     * @return Max output
     */
    @Override
    protected int lookupMaxByTier(int tier) {
        switch (getUpgradeCountByID(IUpgradeItem.TRANSFORMER) + 1) {
            case 1 : return 32;
            case 2 : return 128;
            case 3 : return 512;
            case 4 : return 2048;
            case 5 : return 8192;
            default : return 32;
        }
    }

    /**
     * Determine the tier of this energy source.
     * 1 = LV, 2 = MV, 3 = MHV, 4 = HV, 5 = EV etc.
     *
     * @note Modifying the energy net from this method is disallowed.
     * @return tier of this energy source
     */
    @Optional.Method(modid = "IC2")
    @Override
    public int getSourceTier() {
        return getUpgradeCountByID(IUpgradeItem.TRANSFORMER) + 1;
    }

    /*******************************************************************************************************************
     * Input Output Methods                                                                                            *
     *******************************************************************************************************************/

    /**
     * Used to specify if this tile should handle IO, and render in GUI
     *
     * @return False to prevent
     */
    public boolean shouldHandleIO() {
        return true;
    }

    /**
     * Used to manually disable the IO rendering on tile, true by default
     *
     * @return False to prevent rendering
     */
    public boolean shouldRenderInputOutputOnTile() {
        return shouldHandleIO();
    }

    /**
     * Used to set the default modes for this, calls the helper function to the child
     */
    public void setupValidModes() {
        // Set to default as the first
        validModes.add(EnumInputOutputMode.DEFAULT);

        // Add in specific modes
        addValidModes();

        // Add disabled to end
        validModes.add(EnumInputOutputMode.DISABLED);
    }

    /**
     * Resets everything to default mode
     */
    public void resetIO() {
        for(EnumFacing dir : EnumFacing.values())
            sideModes.put(dir, validModes.get(0));
    }

    /**
     * Toggles the mode to the next available mode in the list
     *
     * @param dir The face to toggle
     */
    public void toggleMode(EnumFacing dir) {
        // Loop to find the next value
        EnumInputOutputMode nextMode = null;
        boolean selectNext = false;

        for(EnumInputOutputMode mode : validModes) {
            if(selectNext) {
                nextMode = mode;
                break;
            }
            if(mode == sideModes.get(dir))
                selectNext = true;
        }

        // Need to loop back around
        if(nextMode == null)
            nextMode = validModes.get(0);

        // Update Collection
        sideModes.put(dir, nextMode);
    }

    /**
     * Used to check if the side is set to a mode that allows output
     * @param dir The face to output from
     * @param isPrimary Is this a primary output or false for secondary
     * @return True if you can move
     */
    public boolean canOutputFromSide(EnumFacing dir, boolean isPrimary) {
        if(isDisabled(dir))
            return false;

        if(isPrimary)
            return sideModes.get(dir) == EnumInputOutputMode.ALL_MODES || sideModes.get(dir) == EnumInputOutputMode.OUTPUT_ALL ||
                    sideModes.get(dir) == EnumInputOutputMode.OUTPUT_PRIMARY;
        else
            return sideModes.get(dir) == EnumInputOutputMode.ALL_MODES || sideModes.get(dir) == EnumInputOutputMode.OUTPUT_ALL ||
                    sideModes.get(dir) == EnumInputOutputMode.OUTPUT_SECONDARY;
    }

    /**
     * Used to check if the side is set to a mode that allows input
     * @param dir The face to input from
     * @param isPrimary Is this a primary input or false for secondary
     * @return True if you can move
     */
    public boolean canInputFromSide(EnumFacing dir, boolean isPrimary) {
        if(isDisabled(dir))
            return false;

        if(isPrimary)
            return sideModes.get(dir) == EnumInputOutputMode.ALL_MODES || sideModes.get(dir) == EnumInputOutputMode.INPUT_ALL ||
                    sideModes.get(dir) == EnumInputOutputMode.INPUT_PRIMARY;
        else
            return sideModes.get(dir) == EnumInputOutputMode.ALL_MODES || sideModes.get(dir) == EnumInputOutputMode.INPUT_ALL ||
                    sideModes.get(dir) == EnumInputOutputMode.INPUT_SECONDARY;
    }

    /**
     * Used to check if the side has been set to disabled
     * @param dir The face
     * @return True if disabled
     */
    public boolean isDisabled(EnumFacing dir) {
        return sideModes.get(dir) == EnumInputOutputMode.DISABLED;
    }

    /**
     * Used to get the mode for a specific side, probably should use this as there are helper methods but
     * some instances may need this
     * @param dir The face
     * @return The mode for the side
     */
    public EnumInputOutputMode getModeForSide(EnumFacing dir) {
        return sideModes.get(dir);
    }

    /**
     * Get the string used to find the texture for this mode
     */
    public String getDisplayIconForSide(EnumFacing dir) {
        switch (getModeForSide(dir)) {
            case INPUT_ALL:
                return "neotech:blocks/inputFace";
            case INPUT_PRIMARY:
                return "neotech:blocks/inputFacePrimary";
            case INPUT_SECONDARY:
                return "neotech:blocks/inputFaceSecondary";
            case OUTPUT_ALL:
                return "neotech:blocks/outputFace";
            case OUTPUT_PRIMARY:
                return "neotech:blocks/outputFacePrimary";
            case OUTPUT_SECONDARY:
                return "neotech:blocks/outputFaceSecondary";
            case ALL_MODES:
                return "neotech:blocks/inputOutputFace";
            case DISABLED:
                return "neotech:blocks/disabled";
            default : return null;
        }
    }

    /*******************************************************************************************************************
     * FluidHandler Methods                                                                                            *
     *******************************************************************************************************************/

    /**
     * Method to define if this tile is a fluid handler
     * @return False by default, override in child classes to enable fluid handling
     */
    public boolean isFluidHandler() {
        return false;
    }

    /**
     * Used to set up the tanks needed. You can insert any number of tanks
     *
     * MUST OVERRIDE IN CHILD CLASSES IF isFluidHandler RETURNS TRUE
     */
    protected void setupTanks() {}

    /**
     * Which tanks can input
     *
     * MUST OVERRIDE IN CHILD CLASSES IF isFluidHandler RETURNS TRUE
     *
     * @return An array with the indexes of the input tanks
     */
    protected int[] getInputTanks() {
        return new int[0];
    }

    /**
     * Which tanks can output
     *
     * MUST OVERRIDE IN CHILD CLASSES IF isFluidHandler RETURNS TRUE
     *
     * @return An array with the indexes of the output tanks
     */
    protected int[] getOutputTanks() {
        return new int[0];
    }

    /**
     * Called when something happens to the tank, you should mark the block for update here if a tile
     */
    public void onTankChanged(FluidTank tank) {
        markForUpdate(3);
    }

    /**
     * Used to convert a number of buckets into MB
     *
     * @param buckets How many buckets
     * @return The amount of buckets in MB
     */
    public int bucketsToMB(int buckets) {
        return Fluid.BUCKET_VOLUME * buckets;
    }

    /**
     * Returns true if the given fluid can be inserted
     *
     * More formally, this should return true if fluid is able to enter
     */
    protected boolean canFill(Fluid fluid) {
        for(Integer x : getInputTanks()) {
            if(x < tanks.length)
                if((tanks[x].getFluid() == null || tanks[x].getFluid().getFluid() == null) ||
                        (tanks[x].getFluid() != null && tanks[x].getFluid().getFluid() == fluid))
                    return true;
        }
        return false;
    }

    /**
     * Returns true if the given fluid can be extracted
     *
     * More formally, this should return true if fluid is able to leave
     */
    protected boolean canDrain(Fluid fluid) {
        for(Integer x : getOutputTanks()) {
            if(x < tanks.length)
                if(tanks[x].getFluid() != null && tanks[x].getFluid().getFluid() != null)
                    return true;
        }
        return false;
    }

    /**
     * Returns an array of objects which represent the internal tanks.
     * These objects cannot be used to manipulate the internal tanks.
     *
     * @return Properties for the relevant internal tanks.
     */
    @Override
    public IFluidTankProperties[] getTankProperties() {
        IFluidTankProperties[] properties = new IFluidTankProperties[tanks.length];
        for(int x = 0; x < tanks.length; x++) {
            final FluidTank tank = tanks[x];
            properties[x] = new IFluidTankProperties() {
                @Nullable
                @Override
                public FluidStack getContents() {
                    return tank.getFluid();
                }

                @Override
                public int getCapacity() {
                    return tank.getCapacity();
                }

                @Override
                public boolean canFill() {
                    return tank.canFill();
                }

                @Override
                public boolean canDrain() {
                    return tank.canDrain();
                }

                @Override
                public boolean canFillFluidType(FluidStack fluidStack) {
                    return tank.canFillFluidType(fluidStack);
                }

                @Override
                public boolean canDrainFluidType(FluidStack fluidStack) {
                    return tank.canDrainFluidType(fluidStack);
                }
            };
        }
        return properties;
    }

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param doFill   If false, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if(resource != null && resource.getFluid() != null && canFill(resource.getFluid())) {
            for(Integer x : getInputTanks()) {
                if(x < tanks.length) {
                    if(tanks[x].fill(resource, false) > 0) {
                        int actual = tanks[x].fill(resource, doFill);
                        if(doFill) onTankChanged(tanks[x]);
                        return actual;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * <p/>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack fluidStack = null;
        for(Integer x : getOutputTanks()) {
            if(x < tanks.length) {
                fluidStack = tanks[x].drain(maxDrain, false);
                if(fluidStack != null) {
                    tanks[x].drain(maxDrain, doDrain);
                    if(doDrain) onTankChanged(tanks[x]);
                    return fluidStack;
                }
            }
        }
        return fluidStack;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return drain(resource.amount, doDrain);
    }

    /*******************************************************************************************************************
     * InventoryHandler Methods                                                                                        *
     *******************************************************************************************************************/

    /**
     * Add a callback to this inventory
     * @param iInventoryCallback The callback you wish to add
     * @return This object, to enable chaining
     */
    public AbstractMachine addCallback(IInventoryCallback iInventoryCallback) {
        inventoryCallbacks.add(iInventoryCallback);
        return this;
    }

    /**
     * Called when the inventory has a change
     *
     * @param slot The slot that changed
     */
    protected void onInventoryChanged(int slot) {
        inventoryCallbacks.forEach((IInventoryCallback callback) -> {
            callback.onInventoryChanged(this, slot);
        });
    }

    /**
     * Used to add just one stack into the end of the inventory
     *
     * @param stack The stack to push
     */
    public void addInventorySlot(ItemStack stack) {
        inventoryContents.push(stack);
    }

    /**
     * Used to push x amount of slots into the inventory
     *
     * @param count How many slots to add
     */
    public void addInventorySlots(int count) {
        for(int i = 0; i < count; i++)
            addInventorySlot(null);
    }

    /**
     * Used to remove the last element of the stack
     *
     * @return The last stack, now popped
     */
    public ItemStack removeInventorySlot() {
        return inventoryContents.pop();
    }

    /**
     * Used to remove a specific amount of items
     *
     * @param count The count of slots to remove
     * @return The array of the stacks that were there
     */
    public ItemStack[] removeInventorySlots(int count) {
        ItemStack[] poppedStacks = new ItemStack[count];
        for(int i = 0; i < count; i++)
            poppedStacks[i] = removeInventorySlot();
        return poppedStacks;
    }

    /**
     * Used to copy from an existing inventory
     *
     * @param inventory The inventory to copy from
     */
    public void copyFrom(IItemHandler inventory) {
        for(int i = 0; i < inventory.getSlots(); i++) {
            if(i < inventoryContents.size()) {
                ItemStack stack = inventory.getStackInSlot(i);
                if(stack != null)
                    inventoryContents.set(i, stack.copy());
                else
                    inventoryContents.set(i, null);
            }
        }
    }

    /**
     * Makes sure this slot is within our range
     * @param slot Which slot
     */
    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= inventoryContents.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + inventoryContents.size() + ")");
    }

    /**
     * Gets the stack limit of a stack
     * @param slot The slot
     * @param stack The stack
     * @return Max stack size
     */
    protected int getStackLimit(int slot, ItemStack stack)
    {
        return stack.getMaxStackSize();
    }

    @Override
    public int getSlots() {
        return inventoryContents.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return inventoryContents.get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null || stack.stackSize == 0 || !isItemValidForSlot(slot, stack))
            return null;

        validateSlotIndex(slot);

        ItemStack existing = this.inventoryContents.get(slot);

        int limit = getStackLimit(slot, stack);

        if (existing != null) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.stackSize;
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.stackSize > limit;

        if (!simulate) {
            if (existing == null) {
                this.inventoryContents.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else {
                existing.stackSize += reachedLimit ? limit : stack.stackSize;
            }
            onInventoryChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return null;

        validateSlotIndex(slot);

        ItemStack existing = this.inventoryContents.get(slot);

        if (existing == null)
            return null;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.stackSize <= toExtract) {
            if (!simulate) {
                this.inventoryContents.set(slot, null);
                onInventoryChanged(slot);
            }
            return existing;
        }
        else {
            if (!simulate) {
                this.inventoryContents.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract));
                onInventoryChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    /*******************************************************************************************************************
     * Upgradable Methods                                                                                              *
     *******************************************************************************************************************/

    /**
     * Called when the upgrade inventory changes, reset to default values for any upgrades
     */
    public void resetValues() {
        if(!hasUpgradeByID(IUpgradeItem.NETWORK_CARD))
            resetIO();
        if(!hasUpgradeByID(IUpgradeItem.REDSTONE_CIRCUIT))
            redstone = 0;
    }

    /**
     * Called when upgrade inventory is changed
     */
    public void upgradeInventoryChanged(int slot) { }

    /**
     * Return the list of upgrades by their id that are allowed in this machine
     * @return A list of valid upgrades
     */
    public List<String> getAcceptableUpgrades() {
        return new ArrayList<>();
    }

    /**
     * Checks the inventory for existing cases of a tiered upgrade, also checks for existing by ID
     * @param stack The stack to check
     * @return True if we have this already
     */
    public boolean hasUpgradeAlready(ItemStack stack) {
        // Make sure this is acceptable to insert
        if(stack.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
            // Cast to our data object
            IUpgradeItem upgradeItem = stack.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);

            // Check if valid
            if(!getAcceptableUpgrades().contains(upgradeItem.getID()))
                return true; // Well, doesn't have but can't have anyway so fail

            // Check against slots
            for(int x = 0; x < upgradeInventory.getSlots(); x++) {
                // Grab item in slot
                ItemStack slottedItem = upgradeInventory.getStackInSlot(x);
                // If we have an item here, and for type cast security it is valid
                if(slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                    // Cast to our data object
                    IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);
                    // Check for matching category, only on tiered objects, otherwise match by ID
                    switch (slottedUpgrade.getCategory()){
                        case CPU :
                            if(upgradeItem.getCategory() == CPU)
                                return slottedItem.stackSize == slottedItem.getMaxStackSize();
                        case HDD :
                            if(upgradeItem.getCategory() == HDD)
                                return slottedItem.stackSize == slottedItem.getMaxStackSize();
                        case MEMORY :
                            if(upgradeItem.getCategory() == MEMORY)
                                return slottedItem.stackSize == slottedItem.getMaxStackSize();
                        case PSU :
                            if(upgradeItem.getCategory() == PSU)
                                return slottedItem.stackSize == slottedItem.getMaxStackSize();
                        case MISC:
                            break;
                        default :
                        case NONE:
                            if(upgradeItem.getID().equalsIgnoreCase(slottedUpgrade.getID()))
                                return slottedItem.stackSize == slottedItem.getMaxStackSize();
                            break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Used to get the upgrade count by id
     * @param id The id of the upgrade
     * @return How many are present, 0 for none found
     */
    public int getUpgradeCountByID(String id) {
        // Cycle Inventory
        for(int x = 0; x < upgradeInventory.getSlots(); x++) {
            // Grab item in slot
            ItemStack slottedItem = upgradeInventory.getStackInSlot(x);
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);
                // If we find what we need, return the stack size
                if(slottedUpgrade.getID().equalsIgnoreCase(id))
                    return slottedItem.stackSize;
            }
        }
        return 0;
    }

    /**
     * Used to check if this upgrade exists by category
     * @param id The category
     * @return If the upgrade is present
     */
    public boolean hasUpgradeByID(String id) {
        // Cycle Inventory
        for(int x = 0; x < upgradeInventory.getSlots(); x++) {
            // Grab item in slot
            ItemStack slottedItem = upgradeInventory.getStackInSlot(x);
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);
                // If we find what we need, return the stack size
                if(slottedUpgrade.getID().equalsIgnoreCase(id))
                    return true;
            }
        }
        return false;
    }

    /**
     * Used to get the upgrade count by category
     * @param category The category of the upgrade
     * @return How many are present, 0 for none found
     */
    public int getUpgradeCountByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY category) {
        // Cycle Inventory
        for(int x = 0; x < upgradeInventory.getSlots(); x++) {
            // Grab item in slot
            ItemStack slottedItem = upgradeInventory.getStackInSlot(x);
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);
                // If we find what we need, return the stack size
                if(slottedUpgrade.getCategory() == category)
                    return slottedItem.stackSize;
            }
        }
        return 0;
    }

    /**
     * Used to check if this upgrade exists by category
     * @param category The category
     * @return Shouldn't really be using this, categories are made for numbered upgrades
     */
    public boolean hasUpgradeByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY category) {
        // Cycle Inventory
        for(int x = 0; x < upgradeInventory.getSlots(); x++) {
            // Grab item in slot
            ItemStack slottedItem = upgradeInventory.getStackInSlot(x);
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);
                // If we find what we need, return the stack size
                if(slottedUpgrade.getCategory() == category)
                    return true;
            }
        }
        return false;
    }

    /**
     * Get modifier for a category
     * @param category The category
     * @return The modifier to apply
     */
    public int getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY category) {
        // Cycle Inventory
        for(int x = 0; x < upgradeInventory.getSlots(); x++) {
            // Grab item in slot
            ItemStack slottedItem = upgradeInventory.getStackInSlot(x);
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);
                // If we find what we need, return the stack size
                if(slottedUpgrade.getCategory() == category)
                    return slottedUpgrade.getMultiplier(slottedItem);
            }
        }
        return 1;
    }

    /**
     * Used to get the upgrade count by id
     * @param id The id of the upgrade
     * @return How many are present, 0 for none found
     */
    public int getModifierByID(String id) {
        // Cycle Inventory
        for(int x = 0; x < upgradeInventory.getSlots(); x++) {
            // Grab item in slot
            ItemStack slottedItem = upgradeInventory.getStackInSlot(x);
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);
                // If we find what we need, return the stack size
                if(slottedUpgrade.getID().equalsIgnoreCase(id))
                    return slottedUpgrade.getMultiplier(slottedItem);
            }
        }
        return 1;
    }

    /**
     * Used mainly by GUI to check for changes
     * @param inventory The inventory to compare against
     * @return True if something changed
     */
    public boolean hasChangedFromLast(InventoryHandler inventory) {
        for(int x = 0; x < upgradeInventory.getSlots(); x++)
            if(!ItemStack.areItemStacksEqual(upgradeInventory.getStackInSlot(x), inventory.getStackInSlot(x)))
                return true;
        return false;
    }

    /*******************************************************************************************************************
     * Syncable Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Used to set the variable for this tile, the Syncable will use this when you send a value to the server
     *
     * @param id The ID of the variable to send
     * @param value The new value to set to (you can use this however you want, eg using the ordinal of EnumFacing)
     */
    @Override
    public void setVariable(int id, double value) {
        switch (id) {
            case REDSTONE_FIELD_ID:
                redstone = (int) value;
                break;
            case IO_FIELD_ID:
                toggleMode(EnumFacing.getFront((int) value));
                break;
            case UPDATE_CLIENT_ID:
                updateClient = true;
                break;
            default :
                super.setVariable(id, value);
        }
    }

    /**
     * Used to get the variable
     *
     * @param id The variable ID
     * @return The value of the variable
     */
    @Override
    public Double getVariable(int id) {
        return super.getVariable(id);
    }

    /**
     * Moves the current redstone mode in either direction
     *
     * @param mod The direction to move. This will move it in that direction as many as provided, usually 1
     *            Positive : To the right
     *            Negative : To the left
     */
    public void moveRedstoneMode(int mod) {
        redstone += mod;
        if(redstone < -1)
            redstone = 1;
        else if (redstone > 1)
            redstone = -1;
    }

    /**
     * Get's the display name for the current mode
     *
     * @return The translated name to display
     */
    public String getRedstoneModeName() {
        switch (redstone) {
            case -1:
                return ClientUtils.translate("neotech.text.low");
            case 0:
                return ClientUtils.translate("neotech.text.disabled");
            case 1:
                return ClientUtils.translate("neotech.text.high");
            default:
                return ClientUtils.translate("neotech.text.error");
        }
    }

    /**
     * Set the mode manually
     *
     * @param newMode The new mode to set to
     */
    public void setRedstoneMode(int newMode) {
        redstone = newMode;
    }
}
