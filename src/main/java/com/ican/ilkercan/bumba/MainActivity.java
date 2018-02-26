package com.ican.ilkercan.bumba;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import Utilities.General.Constants;
import Utilities.General.UtilityFunctions;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity
        implements FragmentHome.OnFragmentInteractionListener, FragmentTerms.OnFragmentInteractionListener, FragmentTasks.OnFragmentInteractionListener{

    public static final String CURRENTFRAGMENT = "CURRENTFRAGMENT";
    private Fragment openedFragment;
    public LinearLayout container;

    public void HideKeyBoard() {
        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    UtilityFunctions.hideSoftKeyboard(MainActivity.this);
                } catch (Exception e) {

                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ;
        container = (LinearLayout) findViewById(R.id.a_main_outer_layout);
        InitBottomBar();

        HideKeyBoard();


    }


    private void RemoveCurrent() {
        if (getSupportFragmentManager().findFragmentByTag(CURRENTFRAGMENT) != null)
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(CURRENTFRAGMENT)).commit();
    }

    private void InitBottomBar() {

        // https://github.com/roughike/BottomBar :

        BottomBar bottomBar = (BottomBar) findViewById(R.id.a_bottomBar);

        final FloatingActionButton btnAddItems = (FloatingActionButton)findViewById(R.id.btnAddItems);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                switch (tabId) {
                    case R.id.tab_terms:
                        if (!getSupportActionBar().isShowing()) {
                            getSupportActionBar().show();
                        }
                        getSupportFragmentManager().beginTransaction().replace(container.getId(), new FragmentTerms(), null).commit();

                        btnAddItems.setVisibility(View.VISIBLE);
                        btnAddItems.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(MainActivity.this, NewTermActivity.class);

                                startActivity(intent);

                            }
                        });

                        break;
                    case R.id.tab_tasks:
                        if (!getSupportActionBar().isShowing()) {
                            getSupportActionBar().show();
                        }
                        getSupportFragmentManager().beginTransaction().replace(container.getId(), new FragmentTasks(), null).commit();

                        btnAddItems.setVisibility(View.VISIBLE);
                        btnAddItems.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);

                                startActivity(intent);

                            }
                        });

                        break;
                    case R.id.tab_home:
                        if (!getSupportActionBar().isShowing()) {
                            getSupportActionBar().show();
                        }
                        getSupportFragmentManager().beginTransaction().replace(container.getId(), new FragmentHome(), null).commit();

                        btnAddItems.setVisibility(View.INVISIBLE);


                        break;

                }
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
