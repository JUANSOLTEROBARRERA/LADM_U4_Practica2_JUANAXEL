package mx.tecnm.tepic.ladm_u4_practica2_juansoltero_axellopez

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import mx.tecnm.tepic.ladm_u4_practica2_juansoltero_axellopez.databinding.ActivityMain2Binding
import java.time.LocalDateTime


class MainActivity2() : AppCompatActivity() {
    lateinit var binding:ActivityMain2Binding

    var listaIDs = ArrayList<String>()
    val autenticacion = FirebaseAuth.getInstance()
    var usuariologeado = autenticacion.currentUser?.email.toString()
    val arreglo = ArrayList<String>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.crear.setOnClickListener {
            crearEvento()
        }
        binding.ingresar.setOnClickListener {
            var consulta = FirebaseDatabase.getInstance().getReference().child("eventos")

            var postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children){
                        if(binding.album.text.toString()==data.key.toString()){
                            invocarOtraVentana(data.key.toString())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            consulta.addValueEventListener(postListener)
        }


        //---------------------------
        val consulta = FirebaseDatabase.getInstance().getReference().child("eventos")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var datos = ArrayList<String>()
                listaIDs.clear()
                arreglo.clear()
                for(data in snapshot.children!!){
                    if (usuariologeado == data.getValue<Evento>()!!.usuario) {
                        val id = data.key
                        listaIDs.add(id!!)
                        val nombre = data.getValue<Evento>()!!.nombre
                        val fecha = data.getValue<Evento>()!!.fecha
                        val estado = data.getValue<Evento>()!!.estado
                        datos.add(
                            "Nombre: ${nombre}\n Fecha: ${fecha}\n" +
                                    " estado: ${estado.toString()}"
                        )
                        arreglo.add("Nombre: ${nombre}\n Fecha: ${fecha}\n" +
                                " estado: ${estado.toString()}")
                    }
                }
                mostrarLista(datos)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        consulta.addValueEventListener(postListener)
        //---------------------------


    }
    fun actualizar(idElegido: String, posicion: Int){
        var basedatos = FirebaseDatabase.getInstance().getReference("eventos")

        var cadena3 =  binding.lista.getItemAtPosition(posicion).toString()
        var cadena4 = cadena3.split(":")
        var nombre = cadena4[1].replace("Fecha","")
        nombre = nombre.substring(1,nombre.length-2)
        var fecha = cadena4[2]+":"+cadena4[3]+":"+cadena4[4].replace("estado","")

        fecha = fecha.substring(1,fecha.length-2)

        var estado = cadena4[5]

        var estado2 = false

        if(estado.replace(" ","")=="true"){

        }else{
            estado2 = true
        }

        actualizaEstado(nombre,fecha,estado2,idElegido)
    }
    fun actualizaEstado(nombre:String,fecha:String,estado:Boolean,idElegido: String){
        var database = FirebaseDatabase.getInstance().getReference("eventos")

        val evento1 = mapOf<String,Boolean>(
            "estado" to estado
        )
        val evento = mapOf<String,String>(
            "fecha" to fecha,
            "nombre" to nombre,
            "usuario" to usuariologeado
        )

        database.child(idElegido)
            .updateChildren(evento1+evento)
            .addOnSuccessListener {
                setTitle("SE CAMBIO EL ESTADO")
            }
            .addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .setPositiveButton("OK"){d,i->}
                    .show()
            }
    }
    private fun dialogo(posicion: Int) {
        var idElegido = listaIDs.get(posicion)

        var cadena3 =  binding.lista.getItemAtPosition(posicion).toString()
        var cadena4 = cadena3.split(":")
        var estado = cadena4[5]

        var estado2 = "Desactivar"

        if(estado.replace(" ","")=="true"){

        }else{
            estado2 = "Activar"
        }


            AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage("¿QUÉ DESEAS HACER CON\n${arreglo.get(posicion)}?")
                .setNegativeButton("ELIMINAR") { d, i ->
                    eliminar(idElegido)
                }
                .setPositiveButton(estado2) { d, i ->
                    actualizar(idElegido, posicion)
                }
                .setNeutralButton("CANCELAR") { d, i -> }
                .show()

    }
    fun eliminar(idElegido: String){
        var basedatos = FirebaseDatabase.getInstance().getReference("eventos")


        basedatos.child(idElegido)
            .removeValue()
            .addOnSuccessListener {
                setTitle("SE ELIMINO")
            }
            .addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .setPositiveButton("OK"){d,i->}
                    .show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun crearEvento() {
        var basedatos = Firebase.database.reference

        val current = LocalDateTime.now()
        val asist2 = current.toString().split("T")

        val evento = Evento(binding.evento.text.toString(),
            usuariologeado,asist2.get(0)+" "+asist2.get(1),true)//equivalente a hashmapof

        basedatos.child("eventos")
            .push().setValue(evento)
            .addOnSuccessListener {
                setTitle("SE INSERTO")

                var consulta = FirebaseDatabase.getInstance().getReference().child("eventos")
                var ultimoid = ""
                var postListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children){
                            ultimoid = data.key.toString()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                }
                consulta.addValueEventListener(postListener)

                //COPIAR A PORTAPAPELES
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", ultimoid)
                clipboard.setPrimaryClip(clip)
                    Toast.makeText(this, "El código de evento se copio a tu portapapeles.", Toast.LENGTH_LONG).show()
                //---------------------------------
                binding.evento.text.clear()
            }
            .addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .setPositiveButton("OK"){d,i->}
                    .show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuoculto, menu)
        return true
    }

    fun mostrarLista(datos:ArrayList<String>){


        try{
            binding.lista.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,datos)

            binding.lista.setOnItemClickListener { adapterView, view, posicion, l ->
                dialogo(posicion)
            }
        }catch (err: NullPointerException){

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.salir->{

            }
            R.id.acerca->{
                AlertDialog.Builder(this).setMessage("Juan Antonio Soltero Barrera\nAxel López Rentería").setTitle("Bina").show()
            }
            R.id.session->{
                FirebaseAuth.getInstance().signOut() //Cierra sesion
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        return true
    }
    fun mandamensaje(){
        Toast.makeText(this,"SE ENCONTRO EL ID",Toast.LENGTH_LONG).show()
    }
    private fun invocarOtraVentana(a:String) {
        var intent = Intent(this,MainActivity3::class.java)
        intent.putExtra("idEvento",a)
        startActivity(intent)
    }

}