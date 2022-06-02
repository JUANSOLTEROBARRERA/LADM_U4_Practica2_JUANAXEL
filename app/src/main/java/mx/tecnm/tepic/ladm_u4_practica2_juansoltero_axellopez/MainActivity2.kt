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
    var ultimoid = ""
    var listaIDs = ArrayList<String>()
    var listaEstados = ArrayList<String>()
    var listaVisibilidad = ArrayList<String>()
    val autenticacion = FirebaseAuth.getInstance()
    var usuariologeado = autenticacion.currentUser?.email.toString()
    val arreglo = ArrayList<String>()
    var estadoregistro = ""


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
                listaEstados.clear()
                listaVisibilidad.clear()
                arreglo.clear()
                for(data in snapshot.children!!){
                    if (usuariologeado == data.getValue<Evento>()!!.usuario) {
                        val id = data.key
                        listaIDs.add(id!!)
                        val nombre = data.getValue<Evento>()!!.nombre
                        val fecha = data.getValue<Evento>()!!.fecha
                        val estado = data.getValue<Evento>()!!.estado
                        val visibilidad = data.getValue<Evento>()!!.visibilidad
                        listaEstados.add(estado!!.toString())
                        listaVisibilidad.add(visibilidad!!.toString())
                        datos.add(
                            " Nombre: ${nombre}\n Fecha: ${fecha}\n" +
                                    " Visibilidad: ${visibilidad.toString()}\n"+
                                    " Estado: ${estado.toString()}"
                        )
                        arreglo.add(" Nombre: ${nombre}\n Fecha: ${fecha}\n" +
                                " Visibilidad: ${visibilidad.toString()}\n"+
                                " Estado: ${estado.toString()}")
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
        var fecha = cadena4[2]+":"+cadena4[3]+":"+cadena4[4].replace("Visibilidad","")

        fecha = fecha.substring(1,fecha.length-2)
        var visible = cadena4[5].replace("Estado","")
        visible = visible.substring(1,visible.length-2)

        var estado = cadena4[6]

        var estado2 = "cerrado"

        if(estado.replace(" ","")=="abierto"){

        }else{
            estado2 = "abierto"
        }

        actualizaEstado(nombre,fecha,estado2,idElegido,visible)
    }
    fun actualizaEstado(nombre:String,fecha:String,estado:String,idElegido: String,visibilidad:String){
        var database = FirebaseDatabase.getInstance().getReference("eventos")


        val evento = mapOf<String,String>(
            "estado" to estado,
            "fecha" to fecha,
            "nombre" to nombre,
            "visibilidad" to visibilidad,
            "usuario" to usuariologeado
        )

        database.child(idElegido)
            .updateChildren(evento)
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

    //--------------------------------ACTUALIZA VISIBILIDAD--------------------------------------
    fun actualizar2(idElegido: String, posicion: Int){
        var basedatos = FirebaseDatabase.getInstance().getReference("eventos")

        var cadena3 =  binding.lista.getItemAtPosition(posicion).toString()
        var cadena4 = cadena3.split(":")
        var nombre = cadena4[1].replace("Fecha","")
        nombre = nombre.substring(1,nombre.length-2)
        var fecha = cadena4[2]+":"+cadena4[3]+":"+cadena4[4].replace("Visibilidad","")

        fecha = fecha.substring(1,fecha.length-2)
        var visible = cadena4[5].replace("Estado","")
        visible = visible.substring(1,visible.length-2)

        var estado = cadena4[6].replace(" ","")

        var visible2 = "oculto"

        if(visible.replace(" ","")=="visible"){

        }else{
            visible2 = "visible"
        }

        actualizaEstado(nombre,fecha,estado,idElegido,visible2)
    }
    //-------------------------------------------------------------------------------------------
    private fun dialogo(posicion: Int) {
        var idElegido = listaIDs.get(posicion)

        var cadena3 =  binding.lista.getItemAtPosition(posicion).toString()
        var cadena4 = cadena3.split(":")
        var estado = cadena4[6]
        var visible = cadena4[5].replace("Estado","")
        visible = visible.substring(1,visible.length-2)

        var estado2 = "Cerrar"

        if(estado.replace(" ","")=="abierto"){

        }else{
            estado2 = "Abrir"
        }

        var visible2 = "Ocultar"

        if(visible.replace(" ","")=="visible"){

        }else{
            visible2 = "Hacer Visible"
        }

            AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage("¿QUÉ DESEAS HACER CON\n${arreglo.get(posicion)}?")
                .setNegativeButton("ELIMINAR") { d, i ->
                    eliminar(idElegido)
                }
                    //EN ESTADO2 SE ALMACENA SI ESTA EN FALSE O TRUE EL ESTADO
                .setPositiveButton("Visibilidad / Estado") { d, i ->
                    //EXTRA ALERT---------------------------------
                    binding.album.setText("")
                    AlertDialog.Builder(this).setTitle("ATENCION")
                        .setMessage("Visibilidad / Estado")
                        .setPositiveButton(estado2) { d, i ->
                            actualizar(idElegido, posicion)
                            if(listaEstados.get(posicion).toString()=="abierto"){
                                listaEstados.set(posicion,"cerrado")
                            }else{
                                listaEstados.set(posicion,"abierto")
                            }
                        }
                        .setNeutralButton(visible2) { d, i ->
                            actualizar2(idElegido, posicion)
                            if(listaVisibilidad.get(posicion).toString()=="visible"){
                                listaVisibilidad.set(posicion,"oculto")
                            }else{
                                listaVisibilidad.set(posicion,"visible")
                            }
                        }
                        .show()
                    //--------------------------------------------
                }
                .setNeutralButton("INGRESAR A ALBUM") { d, i ->
                    binding.album.setText(listaIDs.get(posicion))
                }
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
            usuariologeado,asist2.get(0)+" "+asist2.get(1),"abierto","visible")//equivalente a hashmapof

        basedatos.child("eventos")
            .push().setValue(evento)
            .addOnSuccessListener {
                setTitle("SE INSERTO")

                var consulta = FirebaseDatabase.getInstance().getReference().child("eventos")

                var postListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children){
                            ultimoid = data.key.toString()
                        }
                        mandamensaje(ultimoid)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                }
                consulta.addValueEventListener(postListener)


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
    fun mandamensaje(a:String){
        //Toast.makeText(this,"${a}",Toast.LENGTH_LONG).show()
        binding.album.setText(a)
    }
    private fun invocarOtraVentana(a:String) {
        var intent = Intent(this,MainActivity3::class.java)
        intent.putExtra("idEvento",a)
        startActivity(intent)
    }
    fun mostrarMensaje(a:String){
        Toast.makeText(this,"${a}",Toast.LENGTH_LONG).show()
    }

}