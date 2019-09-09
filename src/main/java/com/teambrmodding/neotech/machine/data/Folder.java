package com.teambrmodding.neotech.machine.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 9/8/2019
 */
public class Folder extends Entry {

    // A list of children in this folder
    private List<Entry> children = new ArrayList<>();

    private String folderName;

    /**
     * Creates an entry in a directory
     *
     * @param parent The parent folder
     */
    public Folder(@Nullable Folder parent, String label) {
        super(parent);
        this.folderName =
                (parent == null ? "" : parent.getFolderName()) + "\\" + label;
    }

    /**
     * Write the data for this entry to the compound
     *
     * @param nbt The nbt, clean tag for this entry only
     */
    @Override
    public void write(CompoundNBT nbt) {
        nbt.putString("name", folderName);

        // Create the list for child's nbt
        ListNBT childrenList = new ListNBT();
        for(Entry child : children) {
            CompoundNBT childTag = new CompoundNBT();
            child.write(childTag);
            childrenList.add(10, childTag);
        }

        // Put the list
        nbt.put("child_list", childrenList);
    }

    /**
     * Read the data from the nbt
     *
     * @param nbt The nbt for just this entry
     */
    @Override
    public Entry read(CompoundNBT nbt) {
        children.clear();
        folderName = nbt.getString("name");

        ListNBT childList = nbt.getList("child_list", 9);
        for(INBT tag : childList) {
            if(tag instanceof CompoundNBT) {
                CompoundNBT childTag = (CompoundNBT) tag;
                int typeClassID = childTag.getInt("entry_type");
                if(Entry.typeToClassMap.containsKey(typeClassID)) {
                    try {
                        children.add(Entry.typeToClassMap.get(typeClassID).newInstance().read(childTag));
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return this;
    }

    /*******************************************************************************************************************
     * Folder                                                                                                          *
     *******************************************************************************************************************/

    /**
     * Deletes an entry from the folder
     * @param entry The entry to remove
     */
    public void delete(Entry entry) {
        children.remove(entry);
    }

    /**
     * Adds a child to this directory
     * @param entry The child to add
     */
    public void addChild(Entry entry) {
        children.add(entry);
    }

    /**
     * Get the name of this folder
     * @return The name for this folder
     */
    public String getFolderName() {
        return folderName;
    }

    @Nullable
    public Folder getSubFolder(String name) {
        // Go through children
        for(Entry childEntry : children) {

            // If child is another folder, search it
            if(childEntry instanceof Folder) {
                // Check if the entry we are trying to get is a folder itself
                if(((Folder) childEntry).getFolderName().equals(name))
                    return (Folder) childEntry;

                // Recursive call to see if this folder has the entry or its folders have the entry
                Folder testEntry = ((Folder) childEntry).getSubFolder(name);
                if(testEntry != null)
                    return testEntry;
            }
        }

        // Unable to find entry
        return null;
    }

    /**
     * Gets a stream of folders
     * @return A stream of folders, not entries
     */
    public Stream<Entry> getFolders() {
        return children.stream().filter(entry -> entry instanceof Folder);
    }

    /**
     * Get all non-folder entries
     * @return A steam of not folders
     */
    public Stream<Entry> getEntries() {
        return children.stream().filter(entry -> !(entry instanceof Folder));
    }
}
