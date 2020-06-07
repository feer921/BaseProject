package com.flyco.banner.widget.LoopViewPager;

import android.os.Parcelable;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * A PagerAdapter wrapper responsible for providing a proper page to
 * LoopViewPager
 * 
 * This class shouldn't be used directly
 */
public class LoopPagerAdapterWrapper extends PagerAdapter {

	private PagerAdapter mAdapter;

	private SparseArray<ToDestroy> mToDestroy = new SparseArray<ToDestroy>();

	private boolean mBoundaryCaching;

	void setBoundaryCaching(boolean flag) {
		mBoundaryCaching = flag;
	}

	LoopPagerAdapterWrapper(PagerAdapter adapter) {
		this.mAdapter = adapter;
	}

	@Override
	public void notifyDataSetChanged() {
		mToDestroy = new SparseArray<ToDestroy>();
		super.notifyDataSetChanged();
	}

	int toRealPosition(int position) {
		int realCount = getRealCount();
		if (realCount == 0)
			return 0;
		int realPosition = (position - 1) % realCount;
		if (realPosition < 0)
			realPosition += realCount;

		return realPosition;
	}

	public int toInnerPosition(int realPosition) {
		int position = (realPosition + 1);
		return position;
	}

	private int getRealFirstPosition() {
		return 1;
	}

	private int getRealLastPosition() {
		return getRealFirstPosition() + getRealCount() - 1;
	}

	@Override
	public int getCount() {
		return mAdapter.getCount() + 2;
	}

	public int getRealCount() {
		return mAdapter.getCount();
	}

	public PagerAdapter getRealAdapter() {
		return mAdapter;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int realPosition = (mAdapter instanceof FragmentPagerAdapter || mAdapter instanceof FragmentStatePagerAdapter) ? position
				: toRealPosition(position);

		if (mBoundaryCaching) {
			ToDestroy toDestroy = mToDestroy.get(position);
			if (toDestroy != null) {
				mToDestroy.remove(position);
				return toDestroy.object;
			}
		}
		return mAdapter.instantiateItem(container, realPosition);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		int realFirst = getRealFirstPosition();
		int realLast = getRealLastPosition();
		int realPosition = (mAdapter instanceof FragmentPagerAdapter || mAdapter instanceof FragmentStatePagerAdapter) ? position
				: toRealPosition(position);

		if (mBoundaryCaching && (position == realFirst || position == realLast)) {
			mToDestroy.put(position, new ToDestroy(container, realPosition, object));
		} else {
			mAdapter.destroyItem(container, realPosition, object);
		}
	}

	/*
	 * Delegate rest of methods directly to the inner adapter.
	 */

	@Override
	public void finishUpdate(ViewGroup container) {
		mAdapter.finishUpdate(container);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return mAdapter.isViewFromObject(view, object);
	}

	@Override
	public void restoreState(Parcelable bundle, ClassLoader classLoader) {
		mAdapter.restoreState(bundle, classLoader);
	}

	@Override
	public Parcelable saveState() {
		return mAdapter.saveState();
	}

	@Override
	public void startUpdate(ViewGroup container) {
		mAdapter.startUpdate(container);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mAdapter.setPrimaryItem(container, position, object);
	}

	/*
	 * End delegation
	 */

	/**
	 * Container class for caching the boundary views
	 */
	static class ToDestroy {
		ViewGroup container;
		int position;
		Object object;

		public ToDestroy(ViewGroup container, int position, Object object) {
			this.container = container;
			this.position = position;
			this.object = object;
		}
	}

}