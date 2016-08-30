package com.teambrmodding.neotech.utils

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 2/26/2016
  */
object TimeUtils {
    // Ticks counted
    var tick : Long = 0

    /**
      * The seconds online
      *
      * @return How many seconds on, floored
      */
    def seconds = Math.floor(tick / 20).toInt

    /**
      * How many minutes on
      *
      * @return Minutes, floored
      */
    def minutes = Math.floor(seconds / 60).toInt

    /**
      * How many hours on
      *
      * @return Hours, floored
      */
    def hours   = Math.floor(minutes / 60).toInt

    /**
      * Used to check if the current seconds is on the time multiple of the given variable
      * @param second What second, for instance checking on 5 will trigger every 5 seconds
      * @return True if on time
      */
    def onSecond(second : Int) : Boolean = seconds % second == 0

    /**
      * Used to check if the current minutes is on the time multiple of the given variable
      * @param minute What minute, for instance checking on 5 will trigger every 5 minutes
      * @return True if on time
      */
    def onMinute(minute : Int) : Boolean = minutes % minute == 0

    /**
      * Used to check if the current hours is on the time multiple of the given variable
      * @param hour What hour, for instance checking on 5 will trigger every 5 hours
      * @return True if on time
      */
    def onHour(hour : Int) : Boolean = hours % hour == 0

    @SubscribeEvent
    def onWorldTick(worldTick : TickEvent.WorldTickEvent) : Unit =
        if(worldTick.phase == TickEvent.Phase.END)
            tick = worldTick.world.getTotalWorldTime
}
