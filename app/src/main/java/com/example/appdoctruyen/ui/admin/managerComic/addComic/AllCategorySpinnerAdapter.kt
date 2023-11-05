package com.example.appdoctruyen.ui.admin.managerComic.addComic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.appdoctruyen.model.Category

class AllCategorySpinnerAdapter(
    private val context: Context,
    @LayoutRes private val layoutResource: Int,
    private val category: List<Category>
) :
    ArrayAdapter<Category>(context, layoutResource, category) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }


    private fun createViewFromResource(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context)
            .inflate(layoutResource, parent, false) as TextView
        view.text = category[position].name
        return view
    }
}