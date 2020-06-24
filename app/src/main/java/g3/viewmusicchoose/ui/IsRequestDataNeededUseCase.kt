package g3.viewmusicchoose.ui

import g3.viewmusicchoose.repo.featured.IMusicRepository
import javax.inject.Inject

class IsRequestDataNeededUseCase @Inject constructor(
    private val repo: IMusicRepository
) {

}
