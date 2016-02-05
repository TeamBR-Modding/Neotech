package com.dyonovan.neotech

import java.awt.Color

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/4/2016
  */
object ColorUtils {

    def getColorBetween(x : Color, y : Color, blending : Float) : Color = {
        val inverseBlending = 1 - blending

        val red     = y.getRed   * blending + x.getRed   * inverseBlending
        val green   = y.getGreen * blending + x.getGreen * inverseBlending
        val blue    = y.getBlue  * blending + x.getBlue  * inverseBlending
        val alpha   = y.getAlpha * blending + x.getAlpha * inverseBlending

        new Color(red / 255, green / 255, blue / 255, alpha / 255)
    }
}
