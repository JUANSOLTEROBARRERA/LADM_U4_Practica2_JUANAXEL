package mx.tecnm.tepic.ladm_u4_practica2_juansoltero_axellopez

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import mx.tecnm.tepic.ladm_u4_practica2_juansoltero_axellopez.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(FirebaseAuth.getInstance().currentUser!=null){
            invocarOtraVentana()
        }

        binding.inscribir.setOnClickListener {
            val autenticacion = FirebaseAuth.getInstance()
            val dialogo = ProgressDialog(this)

            dialogo.setMessage("CREANDO USUARIO")
            dialogo.setCancelable(false)
            dialogo.show()

            autenticacion.createUserWithEmailAndPassword(
                binding.correo.text.toString(),
                binding.contrasena.text.toString()
            ).addOnCompleteListener {
                dialogo.dismiss()
                if(it.isSuccessful){
                    Toast.makeText(this,"SE INSCRIBIO CORRECTAMENTE", Toast.LENGTH_LONG)
                        .show()
                    binding.correo.text.clear()
                    binding.contrasena.text.clear()
                }else{
                    AlertDialog.Builder(this).setTitle("ATENCION")
                        .setMessage("ERROR! NO SE PUDO CREAR USUARIO")
                        .show()
                }
            }
        }

        binding.autenticar.setOnClickListener {
            val autenticacion = FirebaseAuth.getInstance()
            val dialogo = ProgressDialog(this)

            dialogo.setMessage("AUTENTICANDO...")
            dialogo.setCancelable(false)
            dialogo.show()

            autenticacion.signInWithEmailAndPassword(
                binding.correo.text.toString(),
                binding.contrasena.text.toString()
            ).addOnCompleteListener {
                dialogo.dismiss()
                if(it.isSuccessful) {
                    //Toast.makeText(this, autenticacion.currentUser?.email.toString(),Toast.LENGTH_LONG).show()
                    invocarOtraVentana()
                    return@addOnCompleteListener
                }
                AlertDialog.Builder(this)
                    .setMessage("ERROR! correo/contrasena NO VALIDOS")
                    .show()

            }
        }

    }
    private fun invocarOtraVentana() {
        startActivity(Intent(this,MainActivity2::class.java))
        finish()
    }
}