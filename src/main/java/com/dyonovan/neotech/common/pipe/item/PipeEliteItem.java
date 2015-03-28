package com.dyonovan.neotech.common.pipe.item;

public class PipeEliteItem extends PipeBasicItem {
    @Override
    public int getMaximumTransferRate() {
        return 64;
    }

    @Override
    public int getOperationDelay() {
        return 5;
    }
}
