package later.brenohff.com.later.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import later.brenohff.com.later.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomMenu = (BottomNavigationView) findViewById(R.id.bottom_nav_view);

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });

        changeStatusBarColor(R.color.background);
    }

    //region FRAGMENT

    private void setFragment(Integer position) {
        switch (position) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;

        }
    }

    private void pushFragmentWithStack(String tag, Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit)
                .replace(R.id.main_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    private void pushFragmentWithNoStack(Fragment fragment, String tag) {
        this.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit)
                .replace(R.id.main_container, fragment, tag)
                .commit();

//        val last_id: Int = bottom_nav_view.selectedItemId
//        if (new_id > last_id) {
//            supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(R.animator.fragment_slide_left_enter,
//                            R.animator.fragment_slide_left_exit,
//                            R.animator.fragment_slide_right_enter,
//                            R.animator.fragment_slide_right_exit)
//                    .replace(R.id.main_container, fragment, tag)
//                    .commit()
//        } else if (new_id < last_id) {
//            supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(R.animator.fragment_slide_right_enter,
//                            R.animator.fragment_slide_right_exit,
//                            R.animator.fragment_slide_left_enter,
//                            R.animator.fragment_slide_left_exit)
//                    .replace(R.id.main_container, fragment, tag)
//                    .commit()
//        } else {
//            supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//                    .replace(R.id.main_container, fragment, tag)
//                    .addToBackStack(tag)
//                    .commit()
//        }
    }

    private void popFragment(Integer qtd) {
        final FragmentManager mFragmentManager = this.getSupportFragmentManager();
        for (int i = 0; i < qtd; i++) {
            mFragmentManager.popBackStack();
        }
    }

    //endregion

    private void changeStatusBarColor(Integer color) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            final Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(color));
        }
    }
}
