package com.example.kotlindemos

import android.os.Bundle
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.UrlTileProvider
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * This demonstrates how to add a tile overlay to a map.
 */
class TileOverlayDemoActivity : AppCompatActivity(), OnSeekBarChangeListener, OnMapReadyCallback {

    private lateinit var mMoonTiles: TileOverlay
    private lateinit var mTransparencyBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tile_overlay_demo)
        mTransparencyBar = findViewById(R.id.transparencySeekBar)
        mTransparencyBar.max = TRANSPARENCY_MAX
        mTransparencyBar.progress = 0
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        map.mapType = GoogleMap.MAP_TYPE_NONE
        val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
            @Synchronized
            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {
                // The moon tile coordinate system is reversed.  This is not normal.
                val reversedY = (1 shl zoom) - y - 1
                val s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, reversedY)
                var url: URL? = null
                url = try {
                    URL(s)
                } catch (e: MalformedURLException) {
                    throw AssertionError(e)
                }
                return url
            }
        }
        mMoonTiles = map.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        mTransparencyBar.setOnSeekBarChangeListener(this)
    }

    fun setFadeIn(checkBox: CheckBox) {
        mMoonTiles.fadeIn = checkBox.isChecked
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        mMoonTiles.transparency = progress.toFloat() / TRANSPARENCY_MAX.toFloat()
    }

    companion object {
        private const val TRANSPARENCY_MAX = 100

        /** This returns moon tiles.  */
        private const val MOON_MAP_URL_FORMAT = "https://mw1.google.com/mw-planetary/lunar/lunarmaps_v1/clem_bw/%d/%d/%d.jpg"
    }
}