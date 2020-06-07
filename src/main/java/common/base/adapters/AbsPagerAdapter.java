package common.base.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/2/18<br>
 * Time: 14:57<br>
 * <P>DESC:
 * PagerAdapter的封装的基类,加入items data数据
 * </p>
 * ******************(^_^)***********************
 */
public abstract class AbsPagerAdapter<T> extends PagerAdapter {
    protected final String TAG = getClass().getSimpleName();
    /**
     * 任何类型的item数据，组合成item view
     */
    protected List<T> datas;
    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    public T getItemData(int itemPosition) {
        if (itemPosition >= 0) {
            int dataCount = getCount();
            if (itemPosition < dataCount) {
                return datas.get(itemPosition);
            }
        }
        return null;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object itemObj = convertAndInitItem(getItemData(position), container, position);
        if (itemObj != null) {
            return itemObj;
        }
        return super.instantiateItem(container, position);
    }

    protected abstract Object convertAndInitItem(T itemData, ViewGroup container, int position);
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        if (container != null) {
            if (object != null) {
                View itemView = (View) object;
                container.removeView(itemView);
            }
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return getPageTitleFromData(position);
    }

    protected CharSequence getPageTitleFromData(int position) {

        return null;
    }

    public void addData(T itemData) {
        if (itemData != null) {
            if (datas == null) {
                datas = new ArrayList<>();
            }
            datas.add(itemData);
            notifyDataSetChanged();
        }
    }
    public void addDatas(T... itemDatas) {
        if (itemDatas != null) {
            boolean hasDataChanged = false;
            for (T itemData : itemDatas) {
                if (datas == null) {
                    datas = new ArrayList<>();
                }
                datas.add(itemData);
                hasDataChanged = true;
            }
            if (hasDataChanged) {
                notifyDataSetChanged();
            }
        }
    }

    public void addDatas(List<T> itemDatas) {
        if (itemDatas != null && !itemDatas.isEmpty()) {
            if (datas == null) {
                datas = new ArrayList<>(itemDatas);
            }
            else{
                datas.addAll(itemDatas);
            }
            notifyDataSetChanged();
        }
    }

    public void removeData(T toRemoveData) {
        if (toRemoveData != null) {
            if (datas != null) {
                boolean ok = datas.remove(toRemoveData);
                if (ok) {
                    notifyDataSetChanged();
                }
            }
        }
    }

    public void removeData(int toRemovePosition) {
        if (toRemovePosition >= 0) {
            if (datas != null) {
                if (toRemovePosition < datas.size()) {
                    datas.remove(toRemovePosition);
                    notifyDataSetChanged();
                }
            }
        }
    }
}
