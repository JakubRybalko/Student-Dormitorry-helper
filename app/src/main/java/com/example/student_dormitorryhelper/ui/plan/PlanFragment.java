package com.example.student_dormitorryhelper.ui.plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.student_dormitorryhelper.databinding.FragmentPlanBinding;

public class PlanFragment extends Fragment {

    private FragmentPlanBinding binding;
    private boolean isOpenDesc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlanViewModel planViewModel =
                new ViewModelProvider(this).get(PlanViewModel.class);

        binding = FragmentPlanBinding.inflate(inflater, container, false);
        isOpenDesc = false;

        setListeners();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setListeners() {
        binding.funRoom.setOnClickListener(v -> { openDescription("Pokój gier i zabaw"); });
        binding.laundry.setOnClickListener(v -> { openDescription("Pralnia"); });
        binding.studyRoom.setOnClickListener(v -> { openDescription("Pokój cichej nauki"); });
        binding.banquet.setOnClickListener(v -> { openDescription("Sala bankietowa"); });
        binding.bikeRoom.setOnClickListener(v -> { openDescription("Rowerownia"); });
        binding.administration.setOnClickListener(v -> { openDescription("Administracja"); });
    }

    private void openDescription(String title) {
        if(!isOpenDesc) {
            isOpenDesc = true;
            binding.fragmentContainerPlan.setVisibility(View.VISIBLE);
            binding.grayBackground.setVisibility(View.VISIBLE);
            PlanDescriptionFragment planDescriptionFragment = (PlanDescriptionFragment)
                getChildFragmentManager().findFragmentById(binding.fragmentContainerPlan.getId());
            planDescriptionFragment.initData(title);
        }

    }

    public void setVisibleGone() {
        isOpenDesc = false;
        binding.fragmentContainerPlan.setVisibility(View.GONE);
        binding.grayBackground.setVisibility(View.GONE);
    }
}
