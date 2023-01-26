package com.noteapp.feature_detailedscreens.internal.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.noteapp.core.model.CategoryOwnerType
import com.noteapp.feature_detailedscreens.databinding.DialogChangeCategoryBinding
import com.noteapp.feature_detailedscreens.internal.ext.toChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class ChooseCategoryDialog : DialogFragment() {
    private var _binding: DialogChangeCategoryBinding? = null
    private val binding: DialogChangeCategoryBinding get() = _binding!!

    private val viewModel: ChooseCategoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNoteItem()
    }

    private fun observeNoteItem() =
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiCategoryList.collectLatest { uiCategories ->
                    with(binding) {
                        if (uiCategories.isEmpty()) {
                            tvEmptyCategories.isVisible = true
                        } else {
                            tvEmptyCategories.isVisible = false
                            chipgroupCategories.isSingleSelection =
                                viewModel.type == CategoryOwnerType.TODO_TYPE
                            uiCategories.toChipGroup(chipgroupCategories) { categoryId ->
                                viewModel.onEvent(
                                    ChooseCategoryEvent.AddNoteItemCategory(
                                        categoryId
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogChangeCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
