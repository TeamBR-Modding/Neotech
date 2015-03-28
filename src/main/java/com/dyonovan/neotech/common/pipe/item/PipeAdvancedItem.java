package com.dyonovan.neotech.common.pipe.item;

public class PipeAdvancedItem extends PipeBasicItem {

    @Override
    public int getMaximumTransferRate() {
        return 32;
    }

    @Override
    public int getOperationDelay() {
        return 5;
    }
}
