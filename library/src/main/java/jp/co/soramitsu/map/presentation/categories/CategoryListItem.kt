package jp.co.soramitsu.map.presentation.categories

import jp.co.soramitsu.map.model.Category

internal data class CategoryListItem(
    val category: Category,
    val selected: Boolean = false
)
