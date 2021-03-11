package im.bnw.android.domain.core.dispatcher

import kotlinx.coroutines.Dispatchers

object DispatchersProviderImpl : DispatchersProvider {
    override val default = Dispatchers.Default
    override val main = Dispatchers.Main.immediate
    override val unconfined = Dispatchers.Unconfined
    override val io = Dispatchers.IO
}
