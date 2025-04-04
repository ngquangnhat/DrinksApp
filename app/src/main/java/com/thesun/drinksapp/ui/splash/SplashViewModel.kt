import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesun.drinksapp.prefs.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashNavigationState {
    data object GoToAdmin : SplashNavigationState()
    data object GoToUser : SplashNavigationState()
    data object GoToLogin : SplashNavigationState()
    data object Idle : SplashNavigationState()
}

class SplashViewModel  : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _navigationState = MutableStateFlow<SplashNavigationState>(SplashNavigationState.Idle)
    val navigationState = _navigationState.asStateFlow()

    init {
        viewModelScope.launch {
            _isReady.value = true
            delay(3000)
            decideNextScreen()
        }
    }

    private fun decideNextScreen() {
        val user = DataStoreManager.user
        _navigationState.value = when {
            user != null && !user.email.isNullOrEmpty() -> {
                if (user.isAdmin) SplashNavigationState.GoToAdmin else SplashNavigationState.GoToUser
            }
            else -> SplashNavigationState.GoToLogin
        }
    }
}

