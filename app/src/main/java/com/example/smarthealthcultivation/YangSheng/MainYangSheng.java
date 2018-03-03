package com.example.smarthealthcultivation.YangSheng;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarthealthcultivation.R;

import java.util.ArrayList;

public class MainYangSheng extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;
    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<String> title=new ArrayList<String>();
    ArrayList<Fragment> fragments=new ArrayList<Fragment>();

    public MainYangSheng() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainYangSheng.
     */
    // TODO: Rename and change types and number of parameters
    public static MainYangSheng newInstance(String param1, String param2) {
        MainYangSheng fragment = new MainYangSheng();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view=inflater.inflate(R.layout.fragment_main_yang_sheng, container, false);
        viewPager=(ViewPager)view.findViewById(R.id.viewpager);

        tabLayout=(TabLayout)view.findViewById(R.id.tab);

        title.add("食疗");title.add("运动");
        fragments.add(new DietTyerapy());fragments.add(new SportView());

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    class MyAdapter extends FragmentStatePagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return title.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position%fragments.size());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position%title.size());
        }
    }

}
