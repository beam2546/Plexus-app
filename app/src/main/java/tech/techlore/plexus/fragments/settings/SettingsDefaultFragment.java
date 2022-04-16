package tech.techlore.plexus.fragments.settings;

import static tech.techlore.plexus.preferences.PreferenceManager.THEME_PREF;
import static tech.techlore.plexus.utils.IntentUtils.OpenURL;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import tech.techlore.plexus.R;
import tech.techlore.plexus.activities.SettingsActivity;
import tech.techlore.plexus.databinding.BottomSheetHeaderBinding;
import tech.techlore.plexus.databinding.BottomSheetThemeBinding;
import tech.techlore.plexus.databinding.FragmentSettingsDefaultBinding;
import tech.techlore.plexus.preferences.PreferenceManager;

public class SettingsDefaultFragment extends Fragment {

    private FragmentSettingsDefaultBinding fragmentBinding;
    private PreferenceManager preferenceManager;

    public SettingsDefaultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentBinding = FragmentSettingsDefaultBinding.inflate(inflater, container,  false);
        Objects.requireNonNull(((SettingsActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.menu_settings);
        return fragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        preferenceManager=new PreferenceManager(requireActivity());

    /*############################################################################################*/

        // THEME
        view.findViewById(R.id.settings_theme_holder)
                .setOnClickListener(v1 ->
                        ThemeBottomSheet());

        if (preferenceManager.getInt(THEME_PREF) == 0){
            if (Build.VERSION.SDK_INT >= 29){
                fragmentBinding.settingsThemeSubtitle.setText(R.string.system_default);
            }
            else{
                fragmentBinding.settingsThemeSubtitle.setText(R.string.light);
            }
        }
        else if (preferenceManager.getInt(THEME_PREF) == R.id.sys_default){
            fragmentBinding.settingsThemeSubtitle.setText(R.string.system_default);
        }
        else if (preferenceManager.getInt(THEME_PREF) == R.id.light){
            fragmentBinding.settingsThemeSubtitle.setText(R.string.light);
        }
        else if (preferenceManager.getInt(THEME_PREF) == R.id.dark){
            fragmentBinding.settingsThemeSubtitle.setText(R.string.dark);
        }

        // REPORT AN ISSUE
        fragmentBinding.settingsReportIssueHolder
                .setOnClickListener(v2 ->
                    OpenURL(requireActivity(), "https://github.com/techlore/Plexus-app/issues"));


        // ABOUT
        fragmentBinding.settingsAboutHolder
                .setOnClickListener(v3 ->
                        getParentFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_from_end, R.anim.slide_to_start,
                                        R.anim.slide_from_start, R.anim.slide_to_end)
                                .replace(R.id.activity_host_fragment, new AboutFragment())
                                .addToBackStack(null)
                                .commit());
    }

    // THEME BOTTOM SHEET
    private void ThemeBottomSheet(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.CustomBottomSheetTheme);
        bottomSheetDialog.setCancelable(true);

        final BottomSheetThemeBinding bottomSheetBinding = BottomSheetThemeBinding.inflate(getLayoutInflater());
        final BottomSheetHeaderBinding headerBinding = BottomSheetHeaderBinding.bind(bottomSheetBinding.getRoot());
        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());

        // TITLE
        headerBinding.bottomSheetTitle.setText(R.string.theme_title);

        // DEFAULT CHECKED RADIO
        if (preferenceManager.getInt(THEME_PREF) == 0){
            if (Build.VERSION.SDK_INT >= 29){
                preferenceManager.setInt(THEME_PREF, R.id.sys_default);
            }
            else{
                preferenceManager.setInt(THEME_PREF, R.id.light);
            }
        }
        bottomSheetBinding.themeRadiogroup.check(preferenceManager.getInt(THEME_PREF));

        // SHOW SYSTEM DEFAULT OPTION ONLY ON SDK 29 AND ABOVE
        if (Build.VERSION.SDK_INT >= 29){
            bottomSheetBinding.sysDefault.setVisibility(View.VISIBLE);
        }
        else{
            bottomSheetBinding.sysDefault.setVisibility(View.GONE);
        }

        // ON SELECTING OPTION
        bottomSheetBinding.themeRadiogroup
                .setOnCheckedChangeListener((radioGroup, checkedId) -> {

                    if (checkedId == R.id.sys_default) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }
                    else if (checkedId == R.id.light) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    else if (checkedId == R.id.dark) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }

                    preferenceManager.setInt(THEME_PREF, checkedId);
                    bottomSheetDialog.dismiss();
                    requireActivity().recreate();
                });

        // CANCEL BUTTON
        bottomSheetBinding.cancelButton.setOnClickListener(view12 ->
                bottomSheetDialog.cancel());

        // SHOW BOTTOM SHEET WITH CUSTOM ANIMATION
        Objects.requireNonNull(bottomSheetDialog.getWindow()).getAttributes().windowAnimations = R.style.BottomSheetAnimation;
        bottomSheetDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentBinding = null;
    }

}
