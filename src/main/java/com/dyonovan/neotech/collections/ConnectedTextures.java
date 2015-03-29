package com.dyonovan.neotech.collections;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ConnectedTextures {
    public TextureAtlasSprite noConnections;
    public TextureAtlasSprite anti_corners;
    public TextureAtlasSprite corners;
    public TextureAtlasSprite horizontal;
    public TextureAtlasSprite vertical;

    public ConnectedTextures(TextureAtlasSprite noCon, TextureAtlasSprite anti_corn, TextureAtlasSprite corn, TextureAtlasSprite horiz, TextureAtlasSprite vert) {
        noConnections = noCon;
        anti_corners = anti_corn;
        corners = corn;
        horizontal = horiz;
        vertical = vert;
    }

    /**
     * This is used to get what part should be rendered for each corner
     *
     * The corners are represented by integers:
     * 0 - Top Left
     * 1 - Top Right
     * 2 - Bottom Left
     * 3 - Bottom Right
     *
     * The boolean array is the connections around the block. This should be size 12. 0 is top left and it goes left to right,
     * top to bottom. Exclude the block itself as we don't need to check that
     *
     * Boolean Array
     * 012
     * 3 4
     * 567
     *
     * One Level Up
     *  8
     * 9 10
     *  11
     *
     */
    public TextureAtlasSprite getTextureForCorner(int corner, boolean[] connections) {
        //This could be simplified to have just one set, but with the switch statement its constant as it is and we don't have
        //to worry about finding x, y, z so this is actually faster, just looks like its not
        switch (corner) {
            case 0 :
                if(connections[0] && connections[1] && connections[3])
                    return noConnections;
                else if(connections[0] && connections[1])
                    return vertical;
                else if(connections[0] && connections[3])
                    return horizontal;
                else if(connections[1] && connections[3])
                    return anti_corners;
                else if(connections[1])
                    return vertical;
                else if(connections[3])
                    return horizontal;
                return corners;
            case 1 :
                if(connections[2] && connections[1] && connections[4])
                    return noConnections;
                else if(connections[2] && connections[1])
                    return vertical;
                else if(connections[2] && connections[4])
                    return horizontal;
                else if(connections[1] && connections[4])
                    return anti_corners;
                else if(connections[1])
                    return vertical;
                else if(connections[4])
                    return horizontal;
                return corners;
            case 2 :
                if(connections[5] && connections[6] && connections[3])
                    return noConnections;
                else if(connections[5] && connections[6])
                    return vertical;
                else if(connections[5] && connections[3])
                    return horizontal;
                else if(connections[6] && connections[3])
                    return anti_corners;
                else if(connections[6])
                    return vertical;
                else if(connections[3])
                    return horizontal;
                return corners;
            case 3 :
                if(connections[7] && connections[6] && connections[4])
                    return noConnections;
                else if(connections[7] && connections[6])
                    return vertical;
                else if(connections[7] && connections[4])
                    return horizontal;
                else if(connections[6] && connections[4])
                    return anti_corners;
                else if(connections[6])
                    return vertical;
                else if(connections[4])
                    return horizontal;
                return corners;
            default :
                return noConnections;
        }
    }
}
