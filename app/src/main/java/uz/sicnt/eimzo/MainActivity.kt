package uz.sicnt.eimzo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import uz.sicnt.eimzo.MyApplication.Companion.horcrux
import uz.sicnt.horcrux.Constants.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.sicnt.eimzo.connection.ApiInterface
import uz.sicnt.eimzo.connection.ResponseData
import uz.sicnt.eimzo.connection.ServiceBuilder

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bu metod ma'lumotlarni sign qilish uchun ishlatiladi Message ga jo'natilsihi kerak bo'lgan ma'lumot jo'natiladi


        btn_send.setOnClickListener {
            horcrux.createPKCS7(this, "MESSAGE")
        }
        btn_append.setOnClickListener {
            horcrux.appendPkcs7(this, "HASH")
        }

        // Bu metod oldin hashlangan ma'lumotga qo'shimcha sign qilish uchun ishlatiladi, HASH bu kelayotgan ma'lumot
        //horcrux.appendPkcs7(this, "HASH")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CREATE_PKCS7 -> onCreate(resultCode, data)
            APPEND_CODE -> onAppend(resultCode, data)
            else -> onAttached(resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun attachTimeStampToPkcs7() {
        val signatureHex = horcrux.getSignatureHex()
        Log.d("signatureHex", signatureHex)
        // https://edo.uzcardtrade.uz/api/factura/timestamp?signatureHex={signatureHex}
        // bu url get metod orqali request jo'natiladi, kelgan  response'dan data key'lik qismini ma'lumotini olib timestamp biriktiriladi
        val request = ServiceBuilder.buildService(ApiInterface::class.java)
        val call = request.getMovies(getString(R.string.api_key))

        call.enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    val timestamp = response.body()?.data ?: ""
                    horcrux.attachPkcs7(
                        this@MainActivity,
                        serialNumber = horcrux.getSerialNumber(),
                        timeStamp = timestamp,
                        pkcs7String = horcrux.getSubjectName()
                    )
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun onCreate(
        resultCode: Int,
        data: Intent?
    ) {
        when (resultCode) {
            RESULT_ACCESS_DENIED -> {
                Toast.makeText(this, "Доступ запрещен", Toast.LENGTH_SHORT).show()
                Log.e(horcrux.tag, "Проверьте, правильно ли вы ввели API_KEY")
                return
            }
            Activity.RESULT_OK -> {
                //  Здесь вы можете соответствующим образом проанализировать данные (используя этот RegEx `horcrux.regex`). Проверьте res / strings для определения регулярных выражений
                //  Вы можете использовать данные, как вы хотите. Но для использования методов horcrux вы ДОЛЖНЫ вызывать `parsePFX (data)`
                horcrux.parsePFX(data)
                // Log.e("parsePFX",horcrux.parsePFX(data))

                //  Timestamp call
                attachTimeStampToPkcs7()
                return
            }
            RESULT_ERROR -> return
            else -> return
        }
    }

    private fun onAppend(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_ACCESS_DENIED -> {
                Toast.makeText(this, "Доступ запрещен", Toast.LENGTH_SHORT).show()
                Log.e(horcrux.tag, "Проверьте, правильно ли вы ввели API_KEY")
                return
            }
            Activity.RESULT_OK -> {
                //  Здесь вы можете соответствующим образом проанализировать данные (используя этот RegEx `horcrux.regex`). Проверьте res / strings для определения регулярных выражений
                //  Вы можете использовать данные, как вы хотите. Но для использования методов horcrux вы ДОЛЖНЫ вызывать `parsePFX (data)`
                horcrux.parsePFX(data)

                //  Timestamp call
                attachTimeStampToPkcs7()
                return
            }
            RESULT_ERROR -> return
            else -> return
        }

    }

    private fun onAttached(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_ACCESS_DENIED -> {
                Toast.makeText(this, "Доступ запрещен", Toast.LENGTH_SHORT).show()
                Log.e(horcrux.tag, "Проверьте, правильно ли вы ввели API_KEY")
                return
            }
            Activity.RESULT_OK -> {
                //  Ваш готовый подписанный PKCS7. Готов к отправке
                val pkcs = data?.getByteArrayExtra(EXTRA_RESULT_PKCS7) //  ByteArray
                val pkcsString = Base64.encodeToString(pkcs, Base64.NO_WRAP)    //  String


                Log.e("pkcsString", pkcsString)
                // shu yerdagi pkcsString serverga jo'natish kerak bo'ladi, bu bizning HASH'imiz
                // 
                return
            }
            RESULT_ERROR -> return
            else -> return
        }

    }
}
