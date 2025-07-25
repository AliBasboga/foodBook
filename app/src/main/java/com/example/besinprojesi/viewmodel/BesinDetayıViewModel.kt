package com.example.besinprojesi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.besinprojesi.model.Besin
import com.example.besinprojesi.roomdb.BesinDatabase
import kotlinx.coroutines.launch

class BesinDetayıViewModel(application: Application) :  AndroidViewModel(application) {

    val besinLiveData = MutableLiveData<Besin>()

    fun roomVerisiniAl (uuid: Int) {
        viewModelScope.launch {
            val dao = BesinDatabase(getApplication()).besinDao()
            val besin = dao.getBesin(uuid)
            besinLiveData.value =besin
        }
    }
}