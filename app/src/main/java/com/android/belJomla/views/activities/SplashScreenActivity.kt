package com.android.belJomla.views.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.android.belJomla.R
import com.android.belJomla.utils.Constants as c
import com.android.belJomla.utils.LoggerUtils as l
import com.android.belJomla.viewmodels.MainViewModel
import com.android.belJomla.views.fragments.SettingsFragment
import java.util.*
import android.os.LocaleList
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.android.belJomla.application.BelJomlAApplication
import com.android.belJomla.viewmodels.SettingsViewModel


class SplashScreenActivity : AppCompatActivity() {

    val viewModel by viewModels<MainViewModel>()
    val settingsViewModel by viewModels<SettingsViewModel>()
    private var mLocale : Locale? = null
    val preferences :SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(BelJomlAApplication.getAppContext())
    val language = preferences!!.getString(c.SHARED_PREF_LOCALE_KEY,BelJomlAApplication.getAppContext()!!.getString(R.string.locale))!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Toast.makeText(this,"$language",Toast.LENGTH_SHORT).show()

        /*if (language == "ar") {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        }
        if (language == "en") {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR

        }*/

        viewModel.categories.observe(this, Observer { categories ->
            if (categories == null){
                Handler().postDelayed({
                    goToLoginActivity()
                },3000)
            }
            else
            if (categories.size>0){
                 Handler().postDelayed({
                    goToLoginActivity()
                 },3000)
                viewModel.getProducts()
            }

        })



    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale = Locale(language)

        super.attachBaseContext(ContextWrapper.wrap(newBase!!,newLocale)
        )
    }





    private fun goToLoginActivity() {
        l.logMessage(this,"Going to LoginActivity")
        var loginIntent = Intent(this,LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        viewModel.categories.removeObservers(this)
        finish()

    }

}
class ContextWrapper(base: Context) : android.content.ContextWrapper(base) {
    companion object {

        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
            var context = context
            val res = context.resources
            val configuration = res.configuration
            l.logErrorMessage(this,"SDK ${Build.VERSION.SDK_INT}")
            when {
                Build.VERSION.SDK_INT >= 26/*BuildUtils.isAtLeast24Api()*/ -> {
                    configuration.setLocale(newLocale)

                    val localeList = LocaleList(newLocale)
                    LocaleList.setDefault(localeList)
                    configuration.setLocales(localeList)

                    context = context.createConfigurationContext(configuration)

                }
                else /*Build.VERSION.SDK_INT >= 21*//*BuildUtils.isAtLeast17Api()*/ -> {
                   /* configuration.setLocale(newLocale)
                    context = context.createConfigurationContext(configuration)*/
                    //////
                    configuration.locale = newLocale
                    res.updateConfiguration(configuration, res.displayMetrics)
                }
               /* true -> {
                    configuration.locale = newLocale
                    res.updateConfiguration(configuration, res.displayMetrics)
                }*/
            }

            return ContextWrapper(context)
        }
    }
}