package common.base.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/12/10<br>
 * Time: 20:21<br>
 * <P>DESC:
 * 给 ViewPager 的适配器提供Fragment的数据集
 * </p>
 * ******************(^_^)***********************
 */
public class AFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private List<CharSequence> pageTitles;

    public AFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public AFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments == null ? new ArrayList<Fragment>() : fragments;
    }


    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        if (fragments != null) {
            if (!fragments.isEmpty()) {
                return fragments.get(position);
            }
        }
        return null;
    }


    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles != null && !pageTitles.isEmpty() ? pageTitles.get(position) : null;
    }

    public AFragmentStatePagerAdapter addData(Fragment fragment) {
        addData(fragment, -1);
        return this;
    }

    public AFragmentStatePagerAdapter addData(Fragment fragment, int addToIndex) {
        if (fragment != null) {
            if (fragments == null) {
                fragments = new ArrayList<>();
            }
            if (addToIndex >= 0) {
                fragments.add(addToIndex, fragment);
            }
            else {
                fragments.add(fragment);
            }
            notifyDataSetChanged();
        }
        return this;
    }

    public AFragmentStatePagerAdapter addData(Fragment... fragments) {
        if (fragments != null && fragments.length > 0) {
            List<Fragment> fragmentList = Arrays.asList(fragments);
            addData(fragmentList);
        }
        return this;
    }


    public AFragmentStatePagerAdapter addData(Collection<Fragment> fragments) {
        if (fragments != null) {
            if (this.fragments != null) {
                this.fragments.addAll(fragments);
            }
            else {
                this.fragments = new ArrayList<>(fragments);
            }
            notifyDataSetChanged();
        }
        return this;
    }

    public AFragmentStatePagerAdapter setNewDatas(Collection<Fragment> newFragments) {
        if (newFragments == null || newFragments.isEmpty()) {
            this.fragments = null;
        }
        else {
            this.fragments = new ArrayList<>(newFragments);
        }
        notifyDataSetChanged();
        return this;
    }
}
