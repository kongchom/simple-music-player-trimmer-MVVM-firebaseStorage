package g3.viewmusicchoose;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

public class MusicViewpagerAdapter extends FragmentPagerAdapter {
    private Activity mAct;
    OnReceiveMusicListener listener;

    MusicViewpagerAdapter(Activity act, FragmentManager fm, OnReceiveMusicListener listener) {
        super(fm);
        this.mAct = act;
        this.listener = listener;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = MusicOnlineFragment.initialize(mAct,"Online", listener);

        } else {
            fragment = MusicLocalFragment.initialize(mAct,"Mobile", listener);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String name;
        if (position == 0) {
            name = "Music Online";
        } else {
            name = "Music Local";
        }
        return name;
    }
}