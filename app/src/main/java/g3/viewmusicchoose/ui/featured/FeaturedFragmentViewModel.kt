package g3.viewmusicchoose.ui.featured

import androidx.lifecycle.ViewModel
import g3.viewmusicchoose.repo.featured.CheckInternetConnectionUseCase
import g3.viewmusicchoose.repo.featured.GetRemoteAudioDataUseCase
import javax.inject.Inject

class FeaturedFragmentViewModel @Inject constructor(
    private val checkInternetConnectionUseCase: CheckInternetConnectionUseCase,
    private val getRemoteAudioDataUseCase: GetRemoteAudioDataUseCase
) : ViewModel() {

}