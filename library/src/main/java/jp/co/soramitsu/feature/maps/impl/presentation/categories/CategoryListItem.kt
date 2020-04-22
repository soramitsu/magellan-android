package jp.co.soramitsu.feature.maps.impl.presentation.categories

import jp.co.soramitsu.feature.maps.impl.model.Category

data class CategoryListItem(
    val category: Category,
    val selected: Boolean = false
)