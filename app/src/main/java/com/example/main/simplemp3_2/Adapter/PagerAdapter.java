package com.example.main.simplemp3_2.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.example.main.simplemp3_2.Fragment.AlbumFragment;
import com.example.main.simplemp3_2.Fragment.ArtistFragment;
import com.example.main.simplemp3_2.Fragment.FolderFragment;
import com.example.main.simplemp3_2.Fragment.PlayFragment;
import com.example.main.simplemp3_2.Fragment.PlayListFragment;
import com.example.main.simplemp3_2.Fragment.SongFragment;
import com.example.main.simplemp3_2.Fragment.SongStyleFragment;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {
    private int numOfTab;
    private PlayFragment playFragment;
    private ArtistFragment artistFragment;
    private AlbumFragment albumFragment;
    private FolderFragment folderFragment;
    private PlayListFragment playListFragment;
    private SongStyleFragment songStyleFragment;

    public PagerAdapter(FragmentManager fm, int numOfTab) {
        super(fm);
        this.numOfTab = numOfTab;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                playFragment = new PlayFragment();
                return playFragment;
            case 1:
                artistFragment = new ArtistFragment();
                return artistFragment;
            case 2:
                albumFragment = new AlbumFragment();
                return albumFragment;
            case 3:
                folderFragment = new FolderFragment();
                return folderFragment;
            case 4:
                playListFragment = new PlayListFragment();
                return playListFragment;
            case 5:
                songStyleFragment = new SongStyleFragment();
                return songStyleFragment;
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
                return "專輯";
            case 3:
                return "資料夾";
            case 4:
                return "播放列表";
            case 5:
                return "風格";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTab;
    }

    public void refreshAllFragment() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(playFragment);
        fragments.add(artistFragment);
        fragments.add(albumFragment);
        fragments.add(folderFragment);
        fragments.add(playListFragment);
        fragments.add(songStyleFragment);
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                fragment.onStart();
            }
        }
    }
}
