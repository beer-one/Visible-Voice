package com.example.visiblevoice.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.visiblevoice.Fragment.LyricListViewFragment;
import com.example.visiblevoice.Fragment.WCFragment;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private WCFragment wcFragment;
    private LyricListViewFragment lyricListViewFragment;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        wcFragment = new WCFragment();
        lyricListViewFragment = new LyricListViewFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return wcFragment;
            case 1:
                return lyricListViewFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}