package com.example.qrcs_device

import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar

class Settings : AppCompatActivity() {
    private val TAG = "SettingsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        window.statusBarColor = resources.getColor(R.color.black)

        val toolbar: Toolbar = findViewById(R.id.toolbar_settings)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = resources.getString(R.string.settings)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))
        toolbar.setBackgroundColor(resources.getColor(R.color.main_color))

        val sw_cac = findViewById<Switch>(R.id.sw_cac)
        val sw_chk = findViewById<Switch>(R.id.sw_chk)
        val sw_dbg = findViewById<Switch>(R.id.sw_dbg)
        val sw_cli = findViewById<Switch>(R.id.sw_cli)
        val sw_abc = findViewById<Switch>(R.id.sw_abc)
        val sw_aac = findViewById<Switch>(R.id.sw_aac)
        val sw_cal = findViewById<Switch>(R.id.sw_cal)


//        sw_cac.background.setTint(resources.getColor(R.color.main_color))
        sw_cac.thumbDrawable.setTint(resources.getColor(R.color.main_color))
        sw_chk.thumbDrawable.setTint(resources.getColor(R.color.main_color))
        sw_dbg.thumbDrawable.setTint(resources.getColor(R.color.main_color))
        sw_cli.thumbDrawable.setTint(resources.getColor(R.color.main_color))
        sw_abc.thumbDrawable.setTint(resources.getColor(R.color.main_color))
        sw_aac.thumbDrawable.setTint(resources.getColor(R.color.main_color))
        sw_cal.thumbDrawable.setTint(resources.getColor(R.color.main_color))

        val pref = SharedPreference(this)
        val cac = pref.get_bool("CAC")  // Проверка после заливки
        val chk = pref.get_bool("CHK") // Проверка
        val dbg = pref.get_bool("DBG") // Отладка
        val cli = pref.get_bool("CLI") // Климат
        val abc = pref.get_bool("ABC") // Сборка до заливки
        val aac = pref.get_bool("AAC") // Сборка после заливки
        val cal = pref.get_bool("CAL") // Калибровка

        sw_cac.isChecked = cac
        sw_chk.isChecked = chk
        sw_dbg.isChecked = dbg
        sw_cli.isChecked = cli
        sw_abc.isChecked = abc
        sw_aac.isChecked = aac
        sw_cal.isChecked = cal

        changeSwitchColor(sw_cac)
        changeSwitchColor(sw_chk)
        changeSwitchColor(sw_dbg)
        changeSwitchColor(sw_cli)
        changeSwitchColor(sw_abc)
        changeSwitchColor(sw_aac)
        changeSwitchColor(sw_cal)


        sw_cac.setOnClickListener {
            Log.d(TAG, "CAC: ${sw_cac.isChecked}")
            pref.set_bool("CAC", sw_cac.isChecked)
            changeSwitchColor(sw_cac)
        }

        sw_chk.setOnClickListener {
            Log.d(TAG, "CHK: ${sw_chk.isChecked}")
            pref.set_bool("CHK", sw_chk.isChecked)
            changeSwitchColor(sw_chk)
        }

        sw_dbg.setOnClickListener {
            Log.d(TAG, "DBG: ${sw_dbg.isChecked}")
            pref.set_bool("DBG", sw_dbg.isChecked)
            changeSwitchColor(sw_dbg)
        }

        sw_cli.setOnClickListener {
            Log.d(TAG, "CLI: ${sw_cli.isChecked}")
            pref.set_bool("CLI", sw_cli.isChecked)
            changeSwitchColor(sw_cli)
        }

        sw_abc.setOnClickListener {
            Log.d(TAG, "ABC: ${sw_abc.isChecked}")
            pref.set_bool("ABC", sw_abc.isChecked)
            changeSwitchColor(sw_abc)
        }

        sw_aac.setOnClickListener {
            Log.d(TAG, "AAC: ${sw_aac.isChecked}")
            pref.set_bool("AAC", sw_aac.isChecked)
            changeSwitchColor(sw_aac)
        }

        sw_cal.setOnClickListener {
            Log.d(TAG, "CAL: ${sw_cal.isChecked}")
            pref.set_bool("CAL", sw_cal.isChecked)
            changeSwitchColor(sw_cal)
        }





    }

    private fun changeSwitchColor(sw: Switch){
        val x = sw.isChecked
        if (x){
            sw.thumbDrawable.setTint(resources.getColor(R.color.main_color))
        } else {
            sw.thumbDrawable.setTint(resources.getColor(R.color.gray))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}