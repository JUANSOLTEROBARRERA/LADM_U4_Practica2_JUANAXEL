package mx.tecnm.tepic.ladm_u4_practica2_juansoltero_axellopez

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Evento(var nombre:String?=null,var usuario:String?=null,var fecha:String?=null, var estado:String?=null,var visibilidad:String?=null) {

}