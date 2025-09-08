package com.keak.aishou.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(): ViewModel() {

   init {
       print("test")
   }
    fun test(){
        print("fun-test")
    }
}