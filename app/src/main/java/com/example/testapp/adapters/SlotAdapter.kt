package com.example.testapp.adapters

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.databinding.SlotItemGame1Binding
import com.example.testapp.databinding.SlotItemGame1LandBinding
import com.example.testapp.databinding.SlotItemGame2Binding
import com.example.testapp.databinding.SlotItemGame2LandBinding

class SlotAdapter(
    private val context: Context,
    private val slotItems: List<SlotItem>,
    private val slotType: SlotType
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_GAME1_PORTRAIT = 1
        private const val VIEW_TYPE_GAME1_LANDSCAPE = 2
        private const val VIEW_TYPE_GAME2_PORTRAIT = 3
        private const val VIEW_TYPE_GAME2_LANDSCAPE = 4
    }

    override fun getItemViewType(position: Int): Int {
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        return if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (slotType == SlotType.GAME1){
                VIEW_TYPE_GAME1_PORTRAIT
            } else{
                VIEW_TYPE_GAME2_PORTRAIT
            }
        } else {
            if (slotType == SlotType.GAME1){
                VIEW_TYPE_GAME1_LANDSCAPE
            } else {
                VIEW_TYPE_GAME2_LANDSCAPE
            }
        }
    }

    fun getItemByPosition(position: Int) = slotItems[position % slotItems.size]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GAME1_PORTRAIT -> {
                SlotPortraitViewHolder(
                    SlotItemGame1Binding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_GAME1_LANDSCAPE -> {
                SlotLandscapeViewHolder(
                    SlotItemGame1LandBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_GAME2_PORTRAIT -> {
                SlotPortraitGame2ViewHolder(
                    SlotItemGame2Binding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_GAME2_LANDSCAPE -> {
                SlotLandscapeGame2ViewHolder(
                    SlotItemGame2LandBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = slotItems[position % slotItems.size].image
        when (holder) {
            is SlotPortraitViewHolder -> holder.bind(image)
            is SlotLandscapeViewHolder -> holder.bind(image)
            is SlotPortraitGame2ViewHolder -> holder.bind(image)
            is SlotLandscapeGame2ViewHolder -> holder.bind(image)
        }
    }

    class SlotPortraitViewHolder(private val binding: SlotItemGame1Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Drawable?) {

            with(binding) {
                itemImageView.setImageDrawable(image)
            }
        }
    }

    class SlotLandscapeViewHolder(private val binding: SlotItemGame1LandBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Drawable?) {

            with(binding) {
                itemImageView.setImageDrawable(image)
            }
        }
    }

    class SlotPortraitGame2ViewHolder(private val binding: SlotItemGame2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Drawable?) {

            with(binding) {
                itemImageView.setImageDrawable(image)
            }
        }
    }

    class SlotLandscapeGame2ViewHolder(private val binding: SlotItemGame2LandBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Drawable?) {

            with(binding) {
                itemImageView.setImageDrawable(image)
            }
        }
    }
}

