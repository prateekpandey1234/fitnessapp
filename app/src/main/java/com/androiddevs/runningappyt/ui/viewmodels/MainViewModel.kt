package com.androiddevs.runningappyt.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.runningappyt.db.Run
import com.androiddevs.runningappyt.other.SortType
import com.androiddevs.runningappyt.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(//so what this here means that we here try to inject the repository here using viewmodel
    //we don't need to create a another function inside our moduleapp for injection here  because dragger will only need the run dao object for injection of
    //repository
    val mainRepository: MainRepository
): ViewModel() {
    private val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
    private val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runsSortedByCaloriesBurned = mainRepository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMillis = mainRepository.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()

    val runs = MediatorLiveData<List<Run>>()
// MutableLiveData is a subclass of LiveData thats exposes the setValue and postValue methods (the second one is thread safe),
// so you can dispatch a value to any active observers.
// MediatorLiveData can observe other LiveData objects (sources) and react to their onChange events, this will give you control
// on when you want to propagate the event, or do something in particular
    var sortType = SortType.DATE
//MediatorLiveData is a subclass of MutableLiveData that can observe other LiveData objects and react to OnChanged events from them.
    init {
        runs.addSource(runsSortedByDate) { result ->
            if(sortType == SortType.DATE) {
                result?.let { runs.value = it }
            }
        }
// You add 2 liveData to mediatorLiveData using addSource() method of mediatorLiveData. The definition of addSource() method is as the following:
//
//addSource(LiveData<S> source, Observer<S> onChanged)
//onChanged observer will be called when source value was changed. In this observer, you can emit values into mediatorLiveData(you can invoke setValue(),
// postValue() methods). In this way, you have 1 mediatorLiveData which listens 2 liveData. When the data hold in liveData1 or liveData2 changes,
// the observer of mediatorLiveData invoked! Why? Because you made emissions into mediatorLiveData in the second argument of addSource() method of
// MediatorLiveData.
        runs.addSource(runsSortedByAvgSpeed) { result ->//lambda function is added here
            if(sortType == SortType.AVG_SPEED) {//result here refers to the List which is emitted by observer LifeCycle in RunFragement we created
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByCaloriesBurned) { result ->
            if(sortType == SortType.CALORIES_BURNED) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByDistance) { result ->
            if(sortType == SortType.DISTANCE) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByTimeInMillis) { result ->
            if(sortType == SortType.RUNNING_TIME) {
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType) {
        SortType.DATE -> runsSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { runs.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }



    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
}