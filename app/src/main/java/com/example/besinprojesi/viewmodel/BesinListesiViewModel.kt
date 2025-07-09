package com.example.besinprojesi.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.besinprojesi.model.Besin
import com.example.besinprojesi.roomdb.BesinDatabase
import com.example.besinprojesi.service.BesinAPIService
import com.example.besinprojesi.util.OzelSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BesinListesiViewModel(application: Application) : AndroidViewModel(application)  {

    val besinler = MutableLiveData<List<Besin>>()
    val besinHataMesaji = MutableLiveData<Boolean>()
    val besinYukleniyor = MutableLiveData<Boolean>()

    private var besinApiServis = BesinAPIService()
    private var ozelSharedPreferences = OzelSharedPreferences(getApplication())

    private val guncellemeZamani = 10 * 60 * 1000 * 1000 * 1000L

     fun refreshData() {
        val kaydedilmeZamani = ozelSharedPreferences.zamaniAl()

        if (kaydedilmeZamani != null && kaydedilmeZamani != 0L && System.nanoTime() - kaydedilmeZamani < guncellemeZamani) {
            verileriSQLitetanAl()
        } else {
            verileriInternetAl()
        }
    }

    private fun verileriSQLitetanAl() {
        besinYukleniyor.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val besinListesi = BesinDatabase(getApplication()).besinDao().getAllBesin()
            withContext(Dispatchers.Main) {
                besineleriGoster(besinListesi)
                Toast.makeText(getApplication(), "Besinleri Roomdan Aldık", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun refreshFromInternet() {
        verileriInternetAl()
    }

    private fun verileriInternetAl() {
        besinYukleniyor.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val besinListesi= besinApiServis.getData()
            withContext(Dispatchers.Main) {
                besinYukleniyor.value = false
                roomaKaydet(besinListesi)
                Toast.makeText(getApplication(), "Besinleri Internetten Aldık", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun besineleriGoster(besinlerListesi: List<Besin>) {
        besinler.value = besinlerListesi
        besinHataMesaji.value = false
        besinYukleniyor.value = false
    }

    private fun roomaKaydet(besinListesi: List<Besin>) {
        viewModelScope.launch {
            val dao = BesinDatabase(getApplication()).besinDao()
            dao.deleteAllBesin()
            val uuidListesi = dao.insertAll(*besinListesi.toTypedArray())
            var i = 0
            while (i < besinListesi.size) {
                besinListesi[i].uuid = uuidListesi[i].toInt()
                i++
            }
            besineleriGoster(besinListesi)
        }
        ozelSharedPreferences.zamaniKaydet(System.nanoTime())
    }
}