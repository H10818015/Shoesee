package com.example.shoesee;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.shoesee.ViewHolder.DrawerAdapter;
import com.example.shoesee.Fragment.AboutUsFragment;
import com.example.shoesee.Fragment.DashBoardFragment;
import com.example.shoesee.Fragment.IdentifyFragment;
import com.example.shoesee.Fragment.ProfileFragment;
import com.example.shoesee.Fragment.ShoppingCartFragment;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class Navigation extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{

    private static final int POS_CLOSE = 0;
    private static final int POS_DASHBOARD = 1;
    private static final int POS_IDENTIFY = 2;
    private static final int POS_SHOPPING_CART = 3;
    private static final int POS_PROFILE = 4;
    private static final int POS_ABOUT_US = 5;
    private static final int POS_LOGOUT = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();
        
        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CLOSE),
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_PROFILE),
                createItemFor(POS_ABOUT_US),
                new SpaceItem(260),
                createItemFor(POS_LOGOUT)
        ));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);


    }



    private DrawerItem createItemFor(int position){
        return new SimpleItem(screenIcons[position],screenTitles[position])
                .withIconTint(color(R.color.blue))
                .withTextTint(color(R.color.black))
                .withSelectedIconTint(color(R.color.blue))
                .withSelectedTextTint(color(R.color.blue));
    }

    @ColorInt
    private int color(@ColorRes int res){
        return ContextCompat.getColor(this,res);
    }


    private String[] loadScreenTitles() {

        return getResources().getStringArray(R.array.id_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++){
            int id = ta.getResourceId(i,0);
            if (id!=0){
                icons[i] = ContextCompat.getDrawable(this,id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (position == POS_DASHBOARD){
            DashBoardFragment dashBoardFragment = new DashBoardFragment();
            transaction.replace(R.id.container,dashBoardFragment);
        }
        else if (position == POS_IDENTIFY){
           IdentifyFragment identifyFragment = new IdentifyFragment();
            transaction.replace(R.id.container,identifyFragment);
        }
        else if (position == POS_SHOPPING_CART){
            ShoppingCartFragment shoppingCartFragment = new ShoppingCartFragment();
            transaction.replace(R.id.container,shoppingCartFragment);
        }
        else if (position == POS_PROFILE){
            ProfileFragment profileFragment = new ProfileFragment();
            transaction.replace(R.id.container,profileFragment);
        }
        else if (position == POS_ABOUT_US){
            AboutUsFragment aboutUsFragment = new AboutUsFragment();
            transaction.replace(R.id.container,aboutUsFragment);
        }
        else if (position == POS_LOGOUT){
            finish();
        }

        slidingRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }
}