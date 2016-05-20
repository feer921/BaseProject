package common.base.adapters;

import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 通用适配器视图持有者对象
 * <br/>
 * 2015年10月22日-下午7:52:16
 * @author lifei
 */
public class AdapterViewHolder {
    private final SparseArray<View> mViews;
    private final int mPosition;
    private final View mConvertView;

    private AdapterViewHolder(ViewGroup parent, int itemLayoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false);
        // setTag
        mConvertView.setTag(this);
    }

    /**
     * 拿到全部View
     * 
     * @return
     */
    public SparseArray<View> getAllView() {
        return mViews;
    }

    public static AdapterViewHolder getViewHolder(View convertView, ViewGroup parent,int itemLayoutId, int position) {
        if (convertView == null) {
            return new AdapterViewHolder(parent, itemLayoutId, position);
        } 
        else {
            return (AdapterViewHolder) convertView.getTag();
        }
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对应的控件，如果没有则加入views
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     * @param viewId
     * @param text
     * @return
     */
    public AdapterViewHolder setText(int viewId, CharSequence text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     * @param viewId
     * @param drawableId
     * @return
     */
    public AdapterViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     * 
     * @param viewId
     * @param bm
     * @return
     */
    public AdapterViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }
    public int getPosition() {
        return mPosition;
    }
}
