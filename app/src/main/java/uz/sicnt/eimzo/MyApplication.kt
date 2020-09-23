package uz.sicnt.eimzo

import android.app.Application
import android.content.Context
import uz.sicnt.horcrux.Horcrux

class MyApplication : Application() {
    companion object {
        lateinit var instance: Application
        lateinit var context: Context
        lateinit var horcrux: Horcrux
    }

    private val apiKey = "0781DA6FCDFBB9D9BCD412CC46041336AD3E10A490D45A61AB4983FA3FE6BD4B435C5F3525BB8E700F1F0262237A582EB0E84E3AC7DB87C99F809A021371A567"

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        horcrux =
            Horcrux(
                context,
                apiKey
            )
    }
}