package com.example.smarthealthcultivation.YangSheng;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarthealthcultivation.R;

public class DietTyerapy extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;
    String dietclass[]={"家常菜","凉菜","养生粥","热菜","小吃","饮品","蒸菜","主食"};
    TabLayout tab;
    ViewPager viewPager;

    public DietTyerapy() {
        // Required empty public constructor
    }

    public static DietTyerapy newInstance(String param1, String param2) {
        DietTyerapy fragment = new DietTyerapy();
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
        view=inflater.inflate(R.layout.fragment_diet_tyerapy, container, false);

        tab=(TabLayout) view.findViewById(R.id.dietclass);
        viewPager=(ViewPager) view.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tab.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    class MyAdapter extends FragmentStatePagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle=new Bundle();
            bundle.putString("dietclass",dietclass[position]);
            Fragment f=new DietClass();
            f.setArguments(bundle);
            return f;
        }

        @Override
        public int getCount() {
            return dietclass.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return dietclass[position];
        }
    }
}
