package mx.tecnm.tepic.ladm_u4_practica2_juansoltero_axellopez

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import mx.tecnm.tepic.ladm_u4_practica2_juansoltero_axellopez.databinding.ActivityMain3Binding
import java.io.File
import java.util.*

class MainActivity3 : AppCompatActivity() {

    lateinit var binding: ActivityMain3Binding
    lateinit var imagen: Uri
    var muestrabotones = false
    var muestrabotones2 = false
    var elegido = ""
    var estregistro = ""
    var contador = 1
    var listaNombres = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.oculto.visibility = View.GONE

        elegido = intent.extras!!.getString("idEvento")!!

        //Toast.makeText(this, "${estregistro}",Toast.LENGTH_LONG).show()

        //consulta

        //---------------------------
        val consulta = FirebaseDatabase.getInstance().getReference().child("eventos")
        var contadoor = 1
        var verdad = "abierto"
        var visible = "visible"
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for(data in snapshot.children!!){

                    var id = data.key
                    var estadoo = data.getValue<Evento>()!!.estado
                    var visibilidad = data.getValue<Evento>()!!.visibilidad

                    if(id.toString()==elegido){

                        //var ok1 = estadoo.toString().substring(0,4)
                        System.out.println("${contadoor++}    ${estadoo}")
                        //mostrarMensaje("${contadoor++}","true")

                        if(verdad?.equals(estadoo.toString())   ?: (estadoo === null)) {
                            System.out.println("CAMBIO ESTADO DE BANDERA")
                            muestrabotones = true
                        }
                        if(visible?.equals(visibilidad.toString() ?: (visibilidad === null)) ){
                                muestrabotones2 = true
                        }
                    }


                }
                visibilidadbotones()
                visibilidadbotones2()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        consulta.addValueEventListener(postListener)
        //---------------------------
        /////////////////////////////



        binding.numeroevento.setText("${elegido}")

        cargarLista()



        binding.elegir.setOnClickListener {
            val galeria = Intent(Intent.ACTION_GET_CONTENT)

            galeria.type = "image/*"

            startActivityForResult(galeria, 202)
        }
        binding.subir.setOnClickListener {
            var nombreArchivo = ""
            val cal = GregorianCalendar.getInstance()
            val dialogo = ProgressDialog(this)

            dialogo.setMessage("SUBIENDO ARCHIVO...")
            dialogo.setCancelable(false)
            dialogo.show()

            nombreArchivo = cal.get(Calendar.YEAR).toString() +
                    cal.get(Calendar.MONTH).toString() +
                    cal.get(Calendar.DAY_OF_MONTH).toString() +
                    cal.get(Calendar.HOUR).toString() +
                    cal.get(Calendar.MINUTE).toString() +
                    cal.get(Calendar.SECOND).toString() +
                    cal.get(Calendar.MILLISECOND).toString()

            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child("imagenes/${nombreArchivo}")

            storageRef.putFile(imagen)
                .addOnSuccessListener {
                    Toast.makeText(this, "EXITO!, SE SUBIO", Toast.LENGTH_LONG)
                        .show()
                    binding.imagen.setImageBitmap(null)
                    dialogo.dismiss()
                    cargarLista()
                }
                .addOnFailureListener {
                    dialogo.dismiss()
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .show()
                }

            //AGREGAR REGISTRO DE IMAGEN EN REALTIME
            var basedatos = Firebase.database.reference

            val album = Album(nombreArchivo,elegido)//equivalente a hashmapof

            basedatos.child("albumes")
                .push().setValue(album)
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .setPositiveButton("OK"){d,i->}
                        .show()
                }
            //--------------------------------------

        }

    }
    fun mostrarLista(datos: ArrayList<String>){
        binding.lista.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datos)
    }


    private fun cargarLista() {
        //---------------------------


        val consulta = FirebaseDatabase.getInstance().getReference().child("albumes")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var datos = ArrayList<String>()
                listaNombres.clear()
                for(data in snapshot.children!!){
                    val idevento = data.getValue<Album>()!!.eventoid

                    val nimagen = data.getValue<Album>()!!.nombreimagen

                    //binding.subir.setText("${idevento.toString()} == ${binding.numeroevento.text.toString()}")
                    //System.out.println("${id.toString()} == ${binding.numeroevento.text.toString()}")
                    if(idevento.toString()==binding.numeroevento.text.toString()){
                        datos.add("${nimagen}")
                        listaNombres.add(nimagen.toString())
                    }
                }
                mostrarLista(datos)

                binding.lista.setOnItemClickListener { adapterView, view, i, l ->
                    cargarImagenRemota(listaNombres.get(i))
                }
                binding.regresar.setOnClickListener {
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        consulta.addValueEventListener(postListener)
        //---------------------------
    }
    fun visibilidadbotones(){
        if(muestrabotones){
            binding.elegir.visibility = View.VISIBLE
            binding.subir.visibility = View.VISIBLE
            System.out.println("SE PONEN VISIBLES")
        }else{
            System.out.println("SE PONEN INVISIBLES")
            binding.elegir.visibility = View.GONE
            binding.subir.visibility = View.GONE
        }
    }
    fun visibilidadbotones2(){
        if(muestrabotones2){
            System.out.println("SE PONEN VISIBLES")
        }else{
            binding.numeroevento.visibility = View.GONE
            binding.oculto.visibility = View.VISIBLE
            binding.imagen.visibility = View.GONE
            binding.lista.visibility = View.GONE
        }
    }
    private fun cargarImagenRemota(nombreArchivoRemoto: String) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("imagenes/${nombreArchivoRemoto}")
        val archivoTemporal = File.createTempFile("imagenTemp", "jpg")

        storageRef.getFile(archivoTemporal)
            .addOnSuccessListener {
                val mapadeBits = BitmapFactory.decodeFile(archivoTemporal.absolutePath)
                binding.imagen.setImageBitmap(mapadeBits)
            }
            .addOnFailureListener {
                AlertDialog.Builder(this).setMessage(it.message).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 202) {
            imagen = data!!.data!!
            binding.imagen.setImageURI(imagen)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuoculto, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.salir -> {

            }
            R.id.acerca -> {

            }
            R.id.session -> {
                FirebaseAuth.getInstance().signOut() //Cierra sesion
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        return true
    }
    fun mostrarMensaje(a:String, b:String){
        Toast.makeText(this,"${a} == ${b}",Toast.LENGTH_LONG).show()
    }
}