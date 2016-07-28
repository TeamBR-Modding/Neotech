package com.dyonovan.neotech.achievement

import com.teambr.bookshelf.achievement.AchievementList
import net.minecraft.util.text.translation.I18n

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/8/2016
  */
class ModAchievements extends AchievementList(I18n.translateToLocal("achievement.neotech.title")) {
    lazy val CRAFT_FURNACE = "craftFurnace"
    lazy val CRAFT_CRUSHER = "craftCrusher"
    lazy val CRAFT_FURNACE_GENERATOR = "craftFurnaceGenerator"
    lazy val CRAFT_FLUID_GENERATOR = "craftFluidGenerator"
    lazy val CRAFT_SOLAR_GENERATOR_1 = "craftSolarGenerator1"
    lazy val CRAFT_SOLAR_GENERATOR_2 = "craftSolarGenerator2"
    lazy val CRAFT_SOLAR_GENERATOR_3 = "craftSolarGenerator3"

    override def initAchievements(): Unit = {
        //TODO: Needs more planning, will do later
    }
}
