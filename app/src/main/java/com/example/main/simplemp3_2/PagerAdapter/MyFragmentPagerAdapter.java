package com.example.main.simplemp3_2.PagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.main.simplemp3_2.Fragments.AlbumRecycleFragment;
import com.example.main.simplemp3_2.Fragments.ArtistRecycleFragment;
import com.example.main.simplemp3_2.Fragments.FolderRecycleFragment;
import com.example.main.simplemp3_2.Fragments.PlayListRecycleFragment;
import com.example.main.simplemp3_2.Fragments.SongListFrangment;
import com.example.main.simplemp3_2.Fragments.StyleRecycleFragment;
import java.util.ArrayList;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "MyFragmentPagerAdapter";
    private int numOfTab;
    private ArrayList<Fragment> fragments;
    private ArtistRecycleFragment artistRecycleFragment;
    private AlbumRecycleFragment albumRecycleFragment;
    private FolderRecycleFragment folderRecycleFragment;
    private PlayListRecycleFragment playListRecycleFragment;
    private StyleRecycleFragment styleRecycleViewFragment;
    private SongListFrangment songListFrangment;

    public MyFragmentPagerAdapter(FragmentManager fm, int numOfTab) {
        super(fm);
        this.numOfTab = numOfTab;
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                songListFrangment = new SongListFrangment();
                addFragmentToViewPager(songListFrangment);
                return songListFrangment;
            case 1:
                artistRecycleFragment = new ArtistRecycleFragment();
                addFragmentToViewPager(artistRecycleFragment);
                return artistRecycleFragment;
            case 2:
                albumRecycleFragment = new AlbumRecycleFragment();
                addFragmentToViewPager(albumRecycleFragment);
                return albumRecycleFragment;
            case 3:;
                folderRecycleFragment = new FolderRecycleFragment();
                addFragmentToViewPager(folderRecycleFragment);
                return folderRecycleFragment;
            case 4:
                playListRecycleFragment = new PlayListRecycleFragment();
                addFragmentToViewPager(playListRecycleFragment);
                return playListRecycleFragment;
            case 5:
                styleRecycleViewFragment = new StyleRecycleFragment();
                addFragmentToViewPager(songListFrangment);
                return styleRecycleViewFragment;
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

    public void addFragmentToViewPager (Fragment fragment) {
        if (!fragments.contains(fragment)) {
            fragments.add(fragment);
        }
    }

    public void refreshAllFragment() {
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                fragment.onStart();
            }
        }
    }
}
