package androidx.fragment.app

fun isExecutionActions(fragmentManager: FragmentManager?): Boolean {
    return fragmentManager is FragmentManagerImpl && fragmentManager.mExecutingActions
}
