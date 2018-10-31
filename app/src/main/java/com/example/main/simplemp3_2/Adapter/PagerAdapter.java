package com.example.main.simplemp3_2.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.example.main.simplemp3_2.Fragment.ArtistFragment;
import com.example.main.simplemp3_2.Fragment.FolderFragment;
import com.example.main.simplemp3_2.Fragment.PlayFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    private int numOfTab;

    public PagerAdapter(FragmentManager fm, int numOfTab) {
        super(fm);
        this.numOfTab = numOfTab;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                PlayFragment playFragment = new PlayFragment();
                return playFragment;
            case 1:
                ArtistFragment artistFragment = new ArtistFragment();
                return artistFragment;
            case 2:
                FolderFragment folderFragment = new FolderFragment();
                return folderFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "音樂";
            case 1:
                return "演出者";
            case 2:
                return "資料夾";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTab;
    }


}
