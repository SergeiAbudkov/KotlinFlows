package com.kotlinflows.simplemvvmkotlinflows.views.changecolor

import com.kotlinflows.simplemvvmkotlinflows.model.colors.NamedColor

/**
 * Represents list item for the color; it may be selected or not
 */
data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)