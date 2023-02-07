package com.example.feature_notelist.api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.domain.model.Note
import com.example.domain.model.NoteSortOrder
import com.example.feature_notelist.databinding.FragmentNotesListBinding
import com.example.feature_notelist.internal.ListNoteEvent
import com.example.feature_notelist.internal.ListNoteViewModel
import com.example.feature_notelist.internal.navigation.toDetailedNote
import com.noteapp.ui.UiStateObserver
import com.noteapp.ui.databinding.StateLoadingBinding
import com.noteapp.ui.ext.*
import com.noteapp.ui.recycler.note.NoteAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesListFragment : Fragment(), UiStateObserver {
    private var _binding: FragmentNotesListBinding? = null
    private val binding: FragmentNotesListBinding get() = _binding!!

    private var _stateLoadingBinding: StateLoadingBinding? = null
    private val stateLoadingBinding: StateLoadingBinding get() = _stateLoadingBinding!!

    private val viewModel: ListNoteViewModel by viewModels()
    private val notesAdapter: NoteAdapter = NoteAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeNotes()
        initRecyclerView()
        initClickListeners()
    }

    private fun initClickListeners() {
        with(binding) {
            btnAdd.setOnClickListener {
                findNavController().toDetailedNote()
            }
            btnClearAll.setOnClickListener {
                viewModel.onEvent(ListNoteEvent.ClearAll)
            }
            spinnerSort.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    adapter: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    // todo
                    val order: NoteSortOrder = when (position) {
                        1 -> NoteSortOrder.BY_DATE
                        2 -> NoteSortOrder.BY_ALPHABET
                        else -> NoteSortOrder.DEFAULT
                    }
                    viewModel.onEvent(
                        ListNoteEvent.UpdateSortOrder(
                            order
                        )
                    )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
        }
    }

    private fun initRecyclerView() =
        binding.recyclerViewNotes.run {
            val itemTouchHelper =
                ItemTouchHelper(com.noteapp.ui.recycler.SwipeCallback(notesAdapter) { removedItem ->
                    viewModel.onEvent(ListNoteEvent.DeleteItem(removedItem as Note))
                    // undo listener
                    val listener =
                        View.OnClickListener { viewModel.onEvent(ListNoteEvent.RestoreItem) }
                    showSnackbar(com.noteapp.ui.R.string.success_delete, listener)
                })
            initStandardVerticalRecyclerView(itemTouchHelper)
            adapter = notesAdapter
        }

    private fun observeNotes() =
        lifecycleScope.launch {
            viewModel.notes.collectState(
                lifecycleOwner = viewLifecycleOwner,
                onSuccess = ::showNotes,
                onError = ::showError,
                onLoading = stateLoadingBinding::loadingStarted
            )

        }

    private fun showError(throwable: Throwable) =
        stateLoadingBinding.errorOccurred(throwable) {
            viewModel.onEvent(ListNoteEvent.TryAgain)
        }

    private fun showNotes(notes: List<Note>) {
        stateLoadingBinding.loadingFinished()
        notesAdapter.submitList(notes)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        _stateLoadingBinding = StateLoadingBinding.bind(binding.root)
        initAdapter()
        return binding.root
    }

    private fun initAdapter() {
        notesAdapter.apply {
            onNoteClick = { noteId ->
                findNavController().toDetailedNote(noteId)
            }
            submitList(emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _stateLoadingBinding = null
        _binding = null
    }
}
