package glailton.io.github.pocgranterxml

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private lateinit var signalStrengthText: TextView
    private var telephonyCallback: TelephonyCallback? = null
    private var telephonyManager: TelephonyManager? = null
    private var phoneStateListener: PhoneStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signalStrengthText = findViewById(R.id.signalStrengthText)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                observeSignalStrength()
            } else {
                signalStrengthText.text = "Permissão negada"
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            observeSignalStrength()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun observeSignalStrength() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Usando TelephonyCallback para Android 12 ou superior
            telephonyCallback = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
                override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                    signalStrengthText.text = "Nível do Sinal: ${signalStrength.level}"
                }
            }

            telephonyCallback?.let {
                telephonyManager?.registerTelephonyCallback(
                    ContextCompat.getMainExecutor(this),
                    it
                )
            }
        } else {
            // Usando PhoneStateListener para Android 11 ou inferior
            //Observação .: PhoneStateListener esta deprecated
            phoneStateListener = object : PhoneStateListener() {
                override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
                    super.onSignalStrengthsChanged(signalStrength)
                    val level = signalStrength?.level ?: 0
                    signalStrengthText.text = "Nível do Sinal: $level"
                }
            }

            phoneStateListener?.let {
                telephonyManager?.listen(it, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Desregistrar o callback ou listener quando a atividade for destruída
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback?.let {
                telephonyManager?.unregisterTelephonyCallback(it)
            }
        } else {
            phoneStateListener?.let {
                telephonyManager?.listen(it, PhoneStateListener.LISTEN_NONE)
            }
        }
    }

}
