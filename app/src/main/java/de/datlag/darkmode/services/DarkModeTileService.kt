package de.datlag.darkmode.services

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import de.datlag.darkmode.helper.NightModeHelper.NightMode
import de.datlag.darkmode.helper.NightModeHelper.Util

@RequiresApi(Build.VERSION_CODES.N)
class DarkModeTileService : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile?.state = getTileState()
        qsTile?.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.state = getTileState()
        qsTile?.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val util = Util(this)
        when(qsTile?.state) {
            Tile.STATE_ACTIVE -> {
                util.applyNightMode(NightMode.SYSTEM, true)
            }
            Tile.STATE_INACTIVE -> {
                util.applyNightMode(NightMode.DARK, true)
            }
        }
        qsTile.state = getTileState(util)
    }

    private fun getTileState(util: Util = Util(this)): Int {
        return if(util.getMode() == NightMode.DARK) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
    }

}