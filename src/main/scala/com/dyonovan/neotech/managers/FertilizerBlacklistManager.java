package com.dyonovan.neotech.managers;

import net.minecraft.block.Block;

import java.util.ArrayList;

public class FertilizerBlacklistManager {

    private static ArrayList<Block> blacklist = new ArrayList<>();

    public static void blacklistBlock(Block block) {
        if (!blacklist.contains(block))
            blacklist.add(block);
    }

    public static void removeFromBlacklist(Block block) {
        if (blacklist.contains(block))
            blacklist.remove(block);
    }

    public static ArrayList<Block> getBlacklist() {
        return blacklist;
    }
}
