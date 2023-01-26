package com.example.mainlist.internal

import com.example.domain.model.Category
import com.example.domain.model.NoteItem

// separated because both list can have 3 states: Loading, Success, Error
internal data class ListFragmentState(
    val noteItems: List<NoteItem> = emptyList(),
    val categories: List<Category> = emptyList()
)
