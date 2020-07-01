package g3.viewmusicchoose.repo

import g3.viewmusicchoose.repo.featured.IMusicRepository
import io.reactivex.Single
import javax.inject.Inject

class CheckInternetConnectionUseCase @Inject constructor(
    private val repo: IMusicRepository
){
    fun request(): Single<Boolean> {
        return repo.isWifiConnected()
    }
}
