package com.example.besinprojesi.service

import com.example.besinprojesi.model.Besin
import retrofit2.http.GET

interface BesinAPI {
    // Base Url -> https://raw.githubusercontent.com/
    //END POINT -> atilsamancioglu/BTK20-JSONVeriSeti/refs/heads/master/besinler.json
    @GET("atilsamancioglu/BTK20-JSONVeriSeti/refs/heads/master/besinler.json")
    suspend fun getBesin() : List<Besin>
}