package common.base.popupwindow

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author fee
 * <P> DESC:
 * </P>
 */
class ARecyclerViewPopWindow(context: Context) : BasePopupWindow<ARecyclerViewPopWindow>(context) {
    protected var mRecyclerView: RecyclerView? = null

    init {
    }
    override fun providerContentLayoutRes(): Int? {
        return null
    }

    override fun providerContentView(): View? {
        mRecyclerView = RecyclerView(mContext)
        return mRecyclerView
    }


    fun <VH : RecyclerView.ViewHolder> setAdapter(adapter: RecyclerView.Adapter<VH>?) {
        mRecyclerView?.adapter = adapter
    }











}