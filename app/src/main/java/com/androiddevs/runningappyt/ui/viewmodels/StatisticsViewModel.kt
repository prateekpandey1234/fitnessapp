package com.androiddevs.runningappyt.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.androiddevs.runningappyt.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class StatisticsViewModel @Inject constructor(//so what this here means that we here try to inject the repository here using viewmodel
    //we don't need to create a another function inside our moduleapp for injection here  because dragger will only need the run dao object for injection of
    //repository
    val mainRepository: MainRepository
): ViewModel() {
    val totalTimeRun = mainRepository.getTotalTimeInMillis()
    val totalDistance = mainRepository.getTotalDistance()
    val totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()
    val totalAvgSpeed = mainRepository.getTotalAvgSpeed()

    val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
}