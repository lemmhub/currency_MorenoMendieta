package mx.ipn.cic.geo.currency_app

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import mx.ipn.cic.geo.currency_app.databinding.ActivityMainBinding
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Se coloca como comentario, cambio por usar viewbinding.
        // setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //cargar seekbar
        val viewMonedaBase: TextView = findViewById(R.id.textMonedaBase) as TextView
        val seekBarMio = findViewById<SeekBar>(R.id.currencySeek)
        var seekbar_value = 1.0

        seekBarMio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                seekbar_value = (progress.toFloat()/2.toFloat()).toDouble()
                viewMonedaBase.text="MXN: $ $seekbar_value"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                getCurrencyData(seekbar_value).start()
            }
        })


        // Invocar el método para equivalencia de monedas.

    }

    private fun getCurrencyData(valorSeek: Double): Thread
    {
        return Thread {
            val url = URL("https://open.er-api.com/v6/latest/mxn")
            val connection = url.openConnection() as HttpsURLConnection

            Log.d("Resultado Petición: ", connection.responseCode.toString())

            if(connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, Request::class.java)
                updateUI(request, valorSeek)
                inputStreamReader.close()
                inputSystem.close()
            }
            else {
                binding.textMonedaBase.text = "PROBLEMA EN CONEXIÓN"
            }
        }
    }

    private fun updateUI(request: Request, valorSeek: Double)
    {
        runOnUiThread {
            kotlin.run {
                binding.textUltimActualizacion.text = request.time_last_update_utc.dropLast(5)
                binding.textMonedaEuro.text = String.format("EUR: %.2f", request.rates.EUR*valorSeek)
                binding.textMonedaDolar.text = String.format("USD: %.2f", request.rates.USD*valorSeek)
                binding.textMonedaLibra.text = String.format("GBP: %.2f", request.rates.GBP*valorSeek)
                binding.textMonedaReal.text = String.format("BRL: %.2f", request.rates.BRL*valorSeek)
                binding.textMonedaYen.text = String.format("JPY: %.2f", request.rates.JPY*valorSeek)
                binding.textMonedaYuan.text = String.format("CNY: %.2f", request.rates.CNY*valorSeek)
            }
        }
        println("NEEEEEEEEEEEW CHECK")
        println(request.rates.EUR)
        println(request.rates.BRL)
        println(request.rates.JPY)
        println(request.rates.CNY)
    }
}