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
    private static final String TAG = "AFragmentStatePagerAdapter";
    private List<Fragment> fragments;
    private List<CharSequence> pageTitles;

    /**
     * 当给适配器添加 title时是否要刷新适配器？
     * def: false,此情况下，添加完成后，主动调用一次 notifyDataSetChanged()刷新适配器
     */
    private boolean isNeedUpdateWhenAddTitles = false;

    /**
     * 当给适配器添加 Fragment item数据时，是否要自动刷新适配器？
     * def: false,此情况下，添加完成后，主动调用一次 notifyDataSetChanged()刷新适配器
     */
    private boolean isNeedUpdateWhenAddData = false;

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
        //如果ViewPager与TabLayout配合使用，tablayout会从这里来取 tab title
        return pageTitles != null && !pageTitles.isEmpty() ? pageTitles.get(position) : null;
    }

    public AFragmentStatePagerAdapter needUpdateWhenAddData(boolean isNeedUpdateWhenAddData) {
        this.isNeedUpdateWhenAddData = isNeedUpdateWhenAddData;
        return this;
    }

    public AFragmentStatePagerAdapter needUpdateWhenAddTitle(boolean isNeedUpdateWhenAddTitles) {
        this.isNeedUpdateWhenAddTitles = isNeedUpdateWhenAddTitles;
        return this;
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
            if (isNeedUpdateWhenAddData) {
                notifyDataSetChanged();
            }
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
            if (isNeedUpdateWhenAddData) {
                notifyDataSetChanged();
            }
        }
        return this;
    }

    public AFragmentStatePagerAdapter setNewDatas(Collection<? extends Fragment> newFragments) {
        if (newFragments == null || newFragments.isEmpty()) {
            this.fragments = null;
        }
        else {
            this.fragments = new ArrayList<>(newFragments);
        }

        notifyDataSetChanged();
        return this;
    }

    public AFragmentStatePagerAdapter addPageTitle(CharSequence title) {
        if (title != null) {
            addPageTitle(title, -1);
        }
        return this;
    }
    public AFragmentStatePagerAdapter addPageTitle(CharSequence title,int addToIndex) {
        if (title != null) {
            if (pageTitles == null) {
                pageTitles = new ArrayList<>();
            }
            if (addToIndex >= 0) {
                pageTitles.add(addToIndex, title);
            }
            else {
                pageTitles.add(title);
            }
            //添加 标题是否需要刷新？
            if (isNeedUpdateWhenAddTitles) {
                notifyDataSetChanged();
            }
        }
        return this;
    }

    public AFragmentStatePagerAdapter addPageTitle(CharSequence... titles) {
        if (titles != null && titles.length > 0) {
            List<CharSequence> titleList = Arrays.asList(titles);
            addPageTitle(titleList);
        }
        return this;
    }

    public AFragmentStatePagerAdapter addPageTitle(Collection<? extends CharSequence> pageTitles) {
        if (this.pageTitles != null) {
            this.pageTitles.addAll(pageTitles);
        }
        else {
            this.pageTitles = new ArrayList<>(pageTitles);
        }
        if (isNeedUpdateWhenAddTitles) {
            notifyDataSetChanged();
        }
        return this;
    }

    public AFragmentStatePagerAdapter setNewPageTitles(Collection<CharSequence> newPageTitles) {
        if (newPageTitles == null || newPageTitles.isEmpty()) {
            this.pageTitles = null;
        }
        else {
            this.pageTitles = new ArrayList<>(newPageTitles);
        }
        notifyDataSetChanged();
        return this;
    }
}
