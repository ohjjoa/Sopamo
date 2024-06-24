import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.th.novelpartymember.model.EpisodeLikes

class SharedViewModel : ViewModel() {
    private val _episodeLikes = MutableLiveData<EpisodeLikes>()
    val episodeLikes: LiveData<EpisodeLikes> get() = _episodeLikes

    fun setEpisodeLikes(episodeLikes: EpisodeLikes) {
        _episodeLikes.value = episodeLikes
    }
}
