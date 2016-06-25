package common.base.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import java.util.ArrayList;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-25
 * Time: 11:52
 * DESC: 一个给ViewPager提供碎片页面数据的适配器,即针对ViewPager的页面来自Fragment的适配器
 */
public class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter{
    private final ArrayList<ViewPagerFragmentInfo> fragmentPagers = new ArrayList<ViewPagerFragmentInfo>();
    private Context mContext;
    public ViewPagerFragmentAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        ViewPagerFragmentInfo curFragmentInfo = fragmentPagers.get(position);
        return Fragment.instantiate(mContext, curFragmentInfo.fragmentClass.getName(), curFragmentInfo.extraBundleData);
    }

    @Override
    public int getCount() {
        return fragmentPagers.size();
    }
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentPagers.get(position).fragmentTitle;
    }
    /**
     * 添加一个碎片页
     * @param fragmentTitle
     * @param tag
     * @param fragmentClass
     * @param extraData
     */
    public void addFragmentPager(String fragmentTitle,String tag,Class<?> fragmentClass,Bundle extraData){
        ViewPagerFragmentInfo newFragmentPageInfo = new ViewPagerFragmentInfo(fragmentTitle, tag, fragmentClass, extraData);
        addFragmentPager(newFragmentPageInfo);
    }
    private void addFragmentPager(ViewPagerFragmentInfo toAddOne){
        if(toAddOne == null) return;
        fragmentPagers.add(toAddOne);
        notifyDataSetChanged();
    }
    public void updateTitles(String... newTitles){
        //TODO 后续为了增强通用性（以通用分页数据来自服务端的情况）如果无分类则Activity界面应作相应的处理
        if(newTitles == null || newTitles.length == 0){
            return;
        }
        int len = newTitles.length;
        for(int i = 0; i < len ; i++){
            fragmentPagers.get(i).fragmentTitle = newTitles[i];
        }
    }
}
