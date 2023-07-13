package com.mysticmira.ge7.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView?.getSecondVisiblePosition() : Int {
    (this?.layoutManager as LinearLayoutManager).run {
        val first = this.findFirstVisibleItemPosition()
        val first_c = this.findFirstCompletelyVisibleItemPosition()

        if (first == first_c)
            return first + 1
        else
            return first + 2
    }
}


fun RecyclerView?.getThirdVisiblePosition() : Int {
    (this?.layoutManager as LinearLayoutManager).run {
        val first = this.findFirstVisibleItemPosition()
        val first_c = this.findFirstCompletelyVisibleItemPosition()

        if (first == first_c)
            return first + 2
        else
            return first + 3
    }
}