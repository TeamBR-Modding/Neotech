package com.teambrmodding.neotech.common.tile;

import com.teambr.nucleus.common.container.IInventoryCallback;
import com.teambr.nucleus.common.tiles.EnergyHandler;
import com.teambr.nucleus.common.tiles.InventoryHandler;
import com.teambr.nucleus.util.ClientUtils;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.CapabilityLoadManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.teambrmodding.neotech.common.traits.IUpgradeItem.ENUM_UPGRADE_CATEGORY.*;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 9/14/19
 */
public abstract class AbstractMachine extends EnergyHandler implements IItemHandlerModifiable, IFluidHandler {

    /*******************************************************************************************************************
     * AbstractMachine Variables                                                                                       *
     *******************************************************************************************************************/

    // Current Redstone Mode
    public int redstone    = 0;
    // Working variable
    public boolean working = false;

    // The reference to this type
    private TileEntityType<?> tileType;

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
    public HashMap<Direction, EnumInputOutputMode> sideModes = new HashMap<>();

    /*******************************************************************************************************************
     * Inventory Variables                                                                                             *
     *******************************************************************************************************************/

    // A list to hold all callback objects
    private List<IInventoryCallback> callBacks = new ArrayList<>();
    // List of Inventory contents
    public NonNullList<ItemStack> inventoryContents = NonNullList.withSize(getInventorySize(), ItemStack.EMPTY);

    // NBT Tags
    protected static final String ITEMS_NBT_TAG          = "Inventory";

    // Side Handlers
    private IItemHandler handlerTop    = new AbstractMachineSidedWrapper(this, Direction.UP);
    private IItemHandler handlerDown   = new AbstractMachineSidedWrapper(this, Direction.DOWN);
    private IItemHandler handlerNorth  = new AbstractMachineSidedWrapper(this, Direction.NORTH);
    private IItemHandler handlerSouth  = new AbstractMachineSidedWrapper(this, Direction.SOUTH);
    private IItemHandler handlerEast   = new AbstractMachineSidedWrapper(this, Direction.EAST);
    private IItemHandler handlerWest   = new AbstractMachineSidedWrapper(this, Direction.WEST);

    /*******************************************************************************************************************
     * FluidHandler Variables                                                                                          *
     *******************************************************************************************************************/

    // NBT Tags
    protected static final String SIZE_FLUID_NBT_TAG = "SizeFluids";
    protected static final String TANK_ID_NBT_TAG = "TankID";
    protected static final String TANKS_NBT_TAG   = "Tanks";

    // Tanks
    public FluidTank[] tanks;

    /*******************************************************************************************************************
     * Upgradeable Variables                                                                                           *
     *******************************************************************************************************************/

    public static final int UPGRADE_INVENTORY_SIZE = 6;

    public InventoryHandler upgradeInventory = new InventoryHandler(tileType) {
        @Override
        protected int getInventorySize() {
            return UPGRADE_INVENTORY_SIZE;
        }

        @Override
        protected boolean isItemValidForSlot(int index, ItemStack stack) {
            return !hasUpgradeAlready(stack);
        }

        @Override
        public void setVariable(int id, double value) {}

        @Override
        public Double getVariable(int id) {
            return null;
        }
    };

    // NBT Tags
    public static final String UPGRADE_INVENTORY_NBT = "UpgradeInventory";

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Main constructor, load things needed here
     */
    public AbstractMachine(TileEntityType<?> type) {
        super(type);
        this.tileType = type;
        // Input Output
        setupValidModes();
        resetIO();

        // FluidHandler
        setupTanks();

        // Upgradeable
        // Add callback for our upgrade inventory
        upgradeInventory.addCallback((inventory, slotNumber) -> {
            if(inventory.getStackInSlot(slotNumber).isEmpty())
                resetValues();
            upgradeInventoryChanged(slotNumber);
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
     * Used to actually do the processes needed. For processors this should be cooking items and generators should
     * generate RF. This is called every tick allowed, provided redstone mode requirements are met
     */
    protected abstract void doWork();

    /**
     * Use this to set all variables back to the default values, usually means the operation failed
     */
    public abstract void reset();

    /**
     * The initial size of the inventory
     */
    public abstract int getInventorySize();

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
    public abstract int[] getSlotsForFace(Direction face);

    /**
     * Can insert the item into the inventory
     * @param slot The slot
     * @param itemStackIn The stack to insert
     * @param dir The dir
     * @return True if can insert
     */
    public abstract boolean canInsertItem(int slot, ItemStack itemStackIn, Direction dir);

    /**
     * Can this extract the item
     * @param slot The slot
     * @param stack The stack
     * @param dir The dir
     * @return True if can extract
     */
    public abstract boolean canExtractItem(int slot, ItemStack stack, Direction dir);

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
    @Nullable
    @OnlyIn(Dist.CLIENT)
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
    public abstract Object getServerGuiElement(int id, PlayerEntity player, World worldObj, int x, int y, int z);

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
    public abstract Object getClientGuiElement(int id, PlayerEntity player, World worldObj, int x, int y, int z);

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
            timeTicker = 20 - getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU);
            tryInput();
            tryOutput();
        }
        timeTicker -= 1;

        // Do the work
        doWork();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);

        // Upgrades
        CompoundNBT upgradeTagCompound = new CompoundNBT();
        ItemStackHelper.saveAllItems(upgradeTagCompound, upgradeInventory.inventoryContents);
        compound.put(UPGRADE_INVENTORY_NBT, upgradeTagCompound);

        // Inventory
        CompoundNBT inventory = new CompoundNBT();
        ItemStackHelper.saveAllItems(inventory, inventoryContents);
        compound.put(ITEMS_NBT_TAG, inventory);

        // FluidHandler
        if(isFluidHandler()) {
            int id = 0;
            compound.putInt(SIZE_FLUID_NBT_TAG, tanks.length);
            ListNBT tagListFluid = new ListNBT();
            for(FluidTank tank : tanks) {
                if(tank != null) {
                    CompoundNBT tankCompound = new CompoundNBT();
                    tankCompound.putByte(TANK_ID_NBT_TAG, (byte) id);
                    id += 1;
                    tank.writeToNBT(tankCompound);
                    tagListFluid.add(tankCompound);
                }
            }
            compound.put(TANKS_NBT_TAG, tagListFluid);
        }

        // Input Output
        for(Direction side : Direction.values())
            compound.putInt(SIDE_MODE_NBT + side.ordinal(), sideModes.get(side).getIntValue());

        // Abstract Machine
        compound.putInt(REDSTONE_NBT, redstone);
        if(updateClient && world != null) {
            compound.putBoolean(UPDATE_ENERGY_NBT, true);
            updateClient = false;
        }

        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        // Upgrades
        CompoundNBT upgradeTag = compound.getCompound(UPGRADE_INVENTORY_NBT);
        ItemStackHelper.loadAllItems(upgradeTag, upgradeInventory.inventoryContents);

        // Inventory
        CompoundNBT inventoryTag = compound.getCompound(ITEMS_NBT_TAG);
        ItemStackHelper.loadAllItems(inventoryTag, inventoryContents);

        // FluidHandler
        if(isFluidHandler()) {
            ListNBT tagListFluid = compound.getList(TANKS_NBT_TAG, 10);
            int size = compound.getInt(SIZE_FLUID_NBT_TAG);
            if(size != tanks.length && compound.hasUniqueId(SIZE_FLUID_NBT_TAG)) tanks = new FluidTank[size];
            for(int x = 0; x < tagListFluid.size(); x++) {
                CompoundNBT tankCompound = tagListFluid.getCompound(x);
                byte position = tankCompound.getByte(TANK_ID_NBT_TAG);
                if(position < tanks.length)
                    tanks[position].readFromNBT(tankCompound);
            }
        }

        // Input Output
        for(Direction side : Direction.values())
            sideModes.put(side, EnumInputOutputMode.getModeFromInt(compound.getInt(SIDE_MODE_NBT + side.ordinal())));

        // Abstract Machine
        if(compound.hasUniqueId(UPDATE_ENERGY_NBT) && world != null)
            changeEnergy(compound.getInt("Energy"));
        redstone = compound.getInt(REDSTONE_NBT);
    }

    private LazyOptional<?> lazyOptional = LazyOptional.of(() -> this);

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        // Check if face is active and accepting input
        if(isDisabled(facing))
            return LazyOptional.empty(); // We don't want to expose anything when disabled

        // Check for items
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getItemHandlerCapability(facing) != null)
            return (LazyOptional<T>) lazyOptional;

        // Check for fluid
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && isFluidHandler()
                && getFluidHandlerCapability(facing) != null)
            return (LazyOptional<T>) lazyOptional;

        // Not in our extended, pass to parent, will either check for energy or return empty
        return super.getCapability(capability, facing);
    }

    /**
     * Used to expose the item handler capability, in child classes, manage input and output based on this
     * @param dir The direction
     * @return The item handler
     */
    @Nullable
    protected IItemHandler  getItemHandlerCapability(Direction dir) {
        switch (dir) {
            case UP :
                return handlerTop;
            case DOWN :
                return handlerDown;
            case NORTH :
                return handlerNorth;
            case SOUTH :
                return handlerSouth;
            case EAST :
                return handlerEast;
            case WEST :
                return handlerWest;
            default :
                return this;
        }
    }

    /**
     * Used to get the fluid handler for exposing
     * @param dir The direction
     * @return The fluid handler
     */
    @Nullable
    protected IFluidHandler getFluidHandlerCapability(Direction dir) {
        return this;
    }

    /**
     * Used to check if this tile is active or not
     *
     * @return True if active state
     */
    public boolean isActive() {
        // If redstone mode is not matched, break our of update
        if(hasUpgradeByID(IUpgradeItem.REDSTONE_CIRCUIT)) {
            if(redstone == -1 && isPowered())
                return false;
            else if(redstone == 1 && !isPowered())
                return false;
        }
        return true;
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
        energyStorage.setMaxInsert(getSupposedEnergy());
        energyStorage.setMaxExtract(getSupposedEnergy());
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
        return getDefaultEnergyStorageSize() * (getModifierForCategory(PSU));
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
        for(Direction dir : Direction.values())
            sideModes.put(dir, validModes.get(0));
    }

    /**
     * Toggles the mode to the next available mode in the list
     *
     * @param dir The face to toggle
     */
    public void toggleMode(Direction dir) {
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
    public boolean canOutputFromSide(Direction dir, boolean isPrimary) {
        if(dir == null)
            return true;
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
    public boolean canInputFromSide(Direction dir, boolean isPrimary) {
        if(dir == null)
            return true;
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
    public boolean isDisabled(Direction dir) {
        return dir != null && sideModes.get(dir) == EnumInputOutputMode.DISABLED;
    }

    /**
     * Used to get the mode for a specific side, probably should use this as there are helper methods but
     * some instances may need this
     * @param dir The face
     * @return The mode for the side
     */
    public EnumInputOutputMode getModeForSide(Direction dir) {
        return sideModes.get(dir);
    }

    /**
     * Get the string used to find the texture for this mode
     */
    public String getDisplayIconForSide(Direction dir) {
        switch (getModeForSide(dir)) {
            case INPUT_ALL:
                return "neotech:block/inputface";
            case INPUT_PRIMARY:
                return "neotech:block/inputfaceprimary";
            case INPUT_SECONDARY:
                return "neotech:block/inputfacesecondary";
            case OUTPUT_ALL:
                return "neotech:block/outputface";
            case OUTPUT_PRIMARY:
                return "neotech:block/outputfaceprimary";
            case OUTPUT_SECONDARY:
                return "neotech:block/outputfacesecondary";
            case ALL_MODES:
                return "neotech:block/inputoutputface";
            case DISABLED:
                return "neotech:block/disabled";
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
        return FluidAttributes.BUCKET_VOLUME * buckets;
    }

    /**
     * Returns true if the given fluid can be inserted
     *
     * More formally, this should return true if fluid is able to enter
     */
    protected boolean canFill(Fluid fluid) {
        for(Integer x : getInputTanks()) {
            if(x < tanks.length)
                if((tanks[x].getFluid().isEmpty() || tanks[x].getFluid().getFluid() == Fluids.EMPTY) ||
                        (!tanks[x].getFluid().isEmpty() && tanks[x].getFluid().getFluid() == fluid))
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
                if(!tanks[x].getFluid().isEmpty() && tanks[x].getFluid().getFluid() != Fluids.EMPTY)
                    return true;
        }
        return false;
    }


    /**
     * Returns the number of fluid storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    @Override
    public int getTanks() {
        return tanks.length;
    }

    /**
     * Returns the FluidStack in a given tank.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
     * </p>
     *
     * @param tank Tank to query.
     * @return FluidStack in a given tank. NULL if the tank is empty.
     */
    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if(tank < tanks.length)
            return tanks[tank].getFluid().copy();

        // Out of bounds
        return FluidStack.EMPTY;
    }

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @param tank Tank to query.
     * @return     The maximum fluid amount held by the tank.
     */
    @Override
    public int getTankCapacity(int tank) {
        if(tank < tanks.length)
            return tanks[tank].getCapacity();
        return 0;
    }

    /**
     * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
     * basically always return TRUE for this.
     *
     * @param tank  Tank to query for validity
     * @param stack Stack to test with for validity
     * @return TRUE if the tank can hold the FluidStack, not considering current state.
     * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
     */
    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tank < tanks.length;
    }

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param action   If false, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if(!resource.isEmpty() && resource.getFluid() != Fluids.EMPTY && canFill(resource.getFluid())) {
            for(Integer x : getInputTanks()) {
                if(x < tanks.length) {
                    if(tanks[x].fill(resource, action) > 0) {
                        int actual = tanks[x].fill(resource, action);
                        if(action.execute()) onTankChanged(tanks[x]);
                        return actual;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction doDrain) {
        FluidStack fluidStack = FluidStack.EMPTY.copy();
        for(Integer x : getOutputTanks()) {
            if(x < tanks.length) {
                fluidStack = tanks[x].drain(maxDrain, doDrain);
                if(fluidStack.isEmpty()) {
                    tanks[x].drain(maxDrain, doDrain);
                    if(doDrain.execute()) onTankChanged(tanks[x]);
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
    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction doDrain) {
        return drain(resource.getAmount(), doDrain);
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
        callBacks.add(iInventoryCallback);
        return this;
    }

    /**
     * Called when the inventory has a change
     *
     * @param slot The slot that changed
     */
    protected void onInventoryChanged(int slot) {
        callBacks.forEach((IInventoryCallback callback) -> {
            callback.onInventoryChanged(this, slot);
        });
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
                if(!stack.isEmpty())
                    inventoryContents.set(i, stack.copy());
                else
                    inventoryContents.set(i, ItemStack.EMPTY);
            }
        }
    }

    /**
     * Makes sure this slot is within our range
     * @param slot Which slot
     */
    protected boolean isInvalidSlot(int slot) {
        return slot > inventoryContents.size();
    }

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be null)
     * @throws RuntimeException if the handler is called in a way that the handler
     * was not expecting.
     **/
    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if(isInvalidSlot(slot))
            return;
        if (ItemStack.areItemStacksEqual(this.inventoryContents.get(slot), stack))
            return;
        this.inventoryContents.set(slot, stack);
        onInventoryChanged(slot);
    }

    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    @Override
    public int getSlots() {
        return inventoryContents.size();
    }

    /**
     * Returns the ItemStack in a given slot.
     *
     * The result's stack size may be greater than the itemstacks max size.
     *
     * If the result is null, then the slot is empty.
     * If the result is not null but the stack size is zero, then it represents
     * an empty slot that will only accept* a specific itemstack.
     *
     * <p/>
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p/>
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. May not be null.
     **/
    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        if(isInvalidSlot(slot))
            return ItemStack.EMPTY;
        return inventoryContents.get(slot);
    }

    /**
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return null).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isItemValidForSlot(slot, stack))
            return ItemStack.EMPTY;

        if(isInvalidSlot(slot))
            return ItemStack.EMPTY;

        ItemStack existing = this.inventoryContents.get(slot);

        int limit = getSlotLimit(slot);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.inventoryContents.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onInventoryChanged(slot);
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    /**
     * Extracts an ItemStack from the given slot. The returned value must be null
     * if nothing is extracted, otherwise it's stack size must not be greater than amount or the
     * itemstacks getMaxStackSize().
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stacks max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be null, if nothing can be extracted
     **/
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        if(isInvalidSlot(slot))
            return ItemStack.EMPTY;
        ItemStack existing = this.inventoryContents.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.inventoryContents.set(slot, ItemStack.EMPTY);
                onInventoryChanged(slot);
            }
            return existing;
        }
        else {
            if (!simulate) {
                this.inventoryContents.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onInventoryChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return The maximum stack size allowed in the slot.
     */
    @Override
    public int getSlotLimit(int slot) {
        return 64;
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
        // Temp value to store result
        AtomicBoolean result = new AtomicBoolean(false);
        // Make sure this is acceptable to insert
        stack.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).ifPresent(
                upgradeItem -> {
                    // Check if valid
                    if(!getAcceptableUpgrades().contains(upgradeItem.getID())) {
                        result.set(true);
                        return; // Well, doesn't have but can't have anyway so fail
                    }

                    // Check against slots
                    for(int x = 0; x < upgradeInventory.getSlots(); x++) {
                        // Grab item in slot
                        ItemStack slottedItem = upgradeInventory.getStackInSlot(x);
                        // If we have an item here, and for type cast security it is valid
                        slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).ifPresent(
                                slottedUpgrade -> {
                                    // Check for matching category, only on tiered objects, otherwise match by ID
                                    switch (slottedUpgrade.getCategory()) {
                                        case CPU :
                                            if(upgradeItem.getCategory() == CPU)
                                                result.set(slottedItem.getCount() == slottedItem.getMaxStackSize());
                                        case HDD :
                                            if(upgradeItem.getCategory() == HDD)
                                                result.set(slottedItem.getCount() == slottedItem.getMaxStackSize());
                                        case MEMORY :
                                            if(upgradeItem.getCategory() == MEMORY)
                                                result.set(slottedItem.getCount() == slottedItem.getMaxStackSize());
                                        case PSU :
                                            if(upgradeItem.getCategory() == PSU)
                                                result.set(slottedItem.getCount() == slottedItem.getMaxStackSize());
                                        case MISC:
                                        default :
                                        case NONE:
                                            if(upgradeItem.getID().equalsIgnoreCase(slottedUpgrade.getID()))
                                                result.set(slottedItem.getCount() == slottedItem.getMaxStackSize());
                                    }
                                });
                    }
                });
        return result.get();
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
            if (slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).isPresent()) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).orElse(null);
                // If we find what we need, return the stack size
                if(slottedUpgrade.getID().equalsIgnoreCase(id))
                    return slottedItem.getCount();
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
            if (slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).isPresent()) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).orElse(null);
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
            if (slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).isPresent()) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).orElse(null);
                // If we find what we need, return the stack size
                if(slottedUpgrade.getCategory() == category)
                    return slottedItem.getCount();
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
            if (slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).isPresent()) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).orElse(null);
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
            if (slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).isPresent()) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).orElse(null);
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
            if (slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).isPresent()) {
                // Cast to our data object
                IUpgradeItem slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null).orElse(null);
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
     * @param value The new value to set to (you can use this however you want, eg using the ordinal of Direction)
     */
    @Override
    public void setVariable(int id, double value) {
        switch (id) {
            case REDSTONE_FIELD_ID:
                redstone = (int) value;
                break;
            case IO_FIELD_ID:
                toggleMode(Direction.byIndex((int) value));
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

    /**
     * Checks if this block is receiving redstone
     *
     * @return True if has power
     */
    public boolean isPowered() {
        return world.isBlockPowered(pos);
    }
}