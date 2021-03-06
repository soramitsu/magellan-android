package jp.co.soramitsu.map.presentation.places.add.schedule

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.databinding.SmFragmentAddScheduleBinding
import jp.co.soramitsu.map.ext.intervals

@ExperimentalStdlibApi
internal class AddScheduleFragment : Fragment(R.layout.sm_fragment_add_schedule) {

    // shared between PlaceProposalFragment and AddScheduleFragment to
    // apply schedule changes and display them to user
    private lateinit var placeProposalViewModel: PlaceProposalViewModel

    private var _binding: SmFragmentAddScheduleBinding? = null
    private val binding get() = _binding!!

    private var currentScheduleSection: ScheduleSectionView? = null
        set(value) {
            field = value
            value?.removeButtonVisibility = binding.scheduleLinearLayout.childCount > 1
            value?.setOnRemoveButtonClickListener {
                placeProposalViewModel.removeSectionWithId(value.tag as Int)
                currentScheduleSection =
                    binding.scheduleLinearLayout.children.last() as? ScheduleSectionView
            }
            value?.setOnWorkingDaysSelected {
                placeProposalViewModel.onSectionChanged(value.getSectionData())
                updateButtonTitle()
            }
        }

    private var addSectionTransitionListener = object : TransitionListenerAdapter() {
        override fun onTransitionEnd(transition: Transition) {
            super.onTransitionEnd(transition)
            binding.scrollView.post {
                binding.scrollView.smoothScrollBy(0, currentScheduleSection?.height ?: 0)
            }
        }
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SmFragmentAddScheduleBinding.bind(view)

        placeProposalViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.NewInstanceFactory()
        )[PlaceProposalViewModel::class.java]

        placeProposalViewModel.sections.observe(viewLifecycleOwner) { sections ->
            val alreadyPresentedSectionIds: List<Int> = binding.scheduleLinearLayout
                .children
                .filterIsInstance(ScheduleSectionView::class.java)
                .mapNotNull { it.tag as Int }
                .toList()

            val targetSectionIds: List<Int> = sections.map { it.id }

            val sectionsToRemove = alreadyPresentedSectionIds - targetSectionIds
            val sectionsToAdd = targetSectionIds - alreadyPresentedSectionIds
            val sectionsToRebind = targetSectionIds.intersect(alreadyPresentedSectionIds)

            sectionsToRemove.forEach { sectionId ->
                binding.scheduleLinearLayout
                    .children
                    .filterIsInstance(ScheduleSectionView::class.java)
                    .find { it.tag == sectionId }?.let { sectionToRemove ->
                        binding.scheduleLinearLayout.removeView(sectionToRemove)
                    }
            }

            sectionsToAdd.forEach { sectionId -> addSection(sections.first { it.id == sectionId }) }

            sectionsToRebind.forEach { sectionId ->
                binding.scheduleLinearLayout
                    .children
                    .filterIsInstance(ScheduleSectionView::class.java)
                    .find { it.tag == sectionId }?.let { sectionToRebind ->
                        val sectionData = sections.first { it.id == sectionId }
                        sectionToRebind.bindWithSectionData(sectionData)
                    }
            }

            binding.scheduleLinearLayout.children
                .filterIsInstance(ScheduleSectionView::class.java)
                .forEach { it.editable = false }

            currentScheduleSection = binding.scheduleLinearLayout.children
                .filterIsInstance(ScheduleSectionView::class.java)
                .last()
            currentScheduleSection?.editable = true

            updateButtonTitle()
        }

        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        binding.addScheduleSection.setOnClickListener {
            currentScheduleSection?.getSectionData()?.let { currentSectionData ->
                placeProposalViewModel.addSection(currentSectionData)
            }
        }

        binding.saveButton.setOnClickListener {
            currentScheduleSection?.getSectionData()?.let { currentSectionData ->
                placeProposalViewModel.onSaveButtonClick(currentSectionData)
            }
            activity?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentScheduleSection = null
        TransitionManager.endTransitions(binding.scheduleLinearLayout)

        _binding = null
    }

    private fun addSection(newSectionData: SectionData) {
        val newSection = ScheduleSectionView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { layoutParams ->
                val top = resources.getDimension(R.dimen.sm_margin_padding_size_medium).toInt()
                layoutParams.setMargins(0, top, 0, 0)
            }
        }
        newSection.bindWithSectionData(newSectionData)

        val transition = Fade().apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            addTarget(newSection.id)
            addListener(addSectionTransitionListener)
        }
        TransitionManager.beginDelayedTransition(binding.scheduleLinearLayout, transition)

        binding.scheduleLinearLayout.addView(newSection)
    }

    private fun updateButtonTitle() {
        currentScheduleSection?.getSectionData()?.let { currentSectionData ->
            val postfix = currentSectionData.daysMap
                .filterValues { it is SelectionState.NotSelected }
                .map { it.key }
                .intervals()
                .joinToString { interval ->
                    if (interval.first == interval.second) {
                        interval.first.shortLocalisedName(resources)
                    } else {
                        val from = interval.first.shortLocalisedName(resources)
                        val to = interval.second.shortLocalisedName(resources)
                        resources.getString(R.string.sm_working_days_interval, from, to)
                    }
                }

            binding.addScheduleSection.text = resources.getString(R.string.sm_add_opening_hours, postfix)

            if (currentSectionData.daysMap.containsValue(SelectionState.NotSelected)) {
                binding.addScheduleSection.visibility = View.VISIBLE
            } else {
                binding.addScheduleSection.visibility = View.GONE
            }
        }
    }
}
