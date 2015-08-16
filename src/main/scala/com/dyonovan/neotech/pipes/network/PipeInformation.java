package com.dyonovan.neotech.pipes.network;

import java.util.Arrays;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 */
public class PipeInformation {
    protected boolean[] connections = new boolean[6];
    protected boolean isSink = false;

    public PipeInformation(boolean[] con, boolean sink) {
        connections = con;
        isSink = sink;
    }

    public boolean[] getConnections() {
        return connections;
    }

    public void setConnections(boolean[] connections) {
        this.connections = connections;
    }

    public boolean isSink() {
        return isSink;
    }

    public void setIsSink(boolean isSink) {
        this.isSink = isSink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PipeInformation that = (PipeInformation) o;

        return isSink == that.isSink && Arrays.equals(connections, that.connections);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(connections);
        result = 31 * result + (isSink ? 1 : 0);
        return result;
    }
}
