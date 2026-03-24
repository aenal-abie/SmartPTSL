package smartgis.project.app.smartgis.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import smartgis.project.app.smartgis.repository.base.IFeatureRepository
import smartgis.project.app.smartgis.state.ResponseState
//import smartgis.project.app.smartgis.repository.base.IFeatureRepository
//import smartgis.project.app.smartgis.state.ResponseState
import smartgis.project.app.smartgis.utils.currentUser
import smartgis.project.app.smartgis.utils.getDate
import smartgis.project.app.smartgis.utils.toDate
import javax.inject.Inject

@HiltViewModel
class FeatureViewModel @Inject constructor(private val featureRepository: IFeatureRepository) :
  ViewModel() {

  private val _isFeaturePro = MutableLiveData(true)
  val isFeaturePro: LiveData<Boolean> = _isFeaturePro

  fun getStatusPro(features: String) {
    viewModelScope.launch {
      currentUser()?.email?.let { email ->
        featureRepository.isProFeature(email, features).collect { result ->
          when (result) {
            is ResponseState.Success -> {
              if (result.data.isNotEmpty()) {
                _isFeaturePro.postValue(result.data.toDate() > getDate().toDate())
              } else {
                _isFeaturePro.postValue(false)
              }
            }
            else -> Unit
          }

        }
      }
    }
  }

  fun hideButtonPro() {
    _isFeaturePro.postValue(true)
  }

}