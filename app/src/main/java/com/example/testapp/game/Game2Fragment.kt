package com.example.testapp.game

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.MainActivity
import com.example.testapp.R
import com.example.testapp.adapters.SlotAdapter
import com.example.testapp.adapters.SlotItem
import com.example.testapp.adapters.SlotType
import com.example.testapp.databinding.FragmentGame2Binding
import com.example.testapp.extensions.setGradient
import com.mysticmira.ge7.utils.getSecondVisiblePosition
import timber.log.Timber
import kotlin.random.Random

class Game2Fragment : Fragment() {

    private lateinit var binding: FragmentGame2Binding

    private lateinit var adapter1: SlotAdapter
    private lateinit var adapter2: SlotAdapter
    private lateinit var adapter3: SlotAdapter

    private lateinit var item1: SlotItem
    private lateinit var item2: SlotItem
    private lateinit var item3: SlotItem

    private var scrollCount: Int = 0

    private var bid = 10
    private var balance = 0
    private var win = 0

    private var observableBid = ObservableInt()
    private var observableBalance = ObservableInt()
    private var observableWin = ObservableInt()

    companion object {
        private const val KEY_BID = "bid"
        private const val KEY_WIN = "win"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_BID, bid)
        outState.putInt(KEY_WIN, win)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            bid = savedInstanceState.getInt(KEY_BID)
            win = savedInstanceState.getInt(KEY_WIN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR

        // Check if there's a saved instance state
        if (savedInstanceState != null) {
            // Restore the counter value
            bid = savedInstanceState.getInt(KEY_BID)
            win = savedInstanceState.getInt(KEY_WIN)
        }

        adapter1 = SlotAdapter(requireContext(), getGameItems(1), SlotType.GAME2)
        adapter2 = SlotAdapter(requireContext(), getGameItems(2), SlotType.GAME2)
        adapter3 = SlotAdapter(requireContext(), getGameItems(3), SlotType.GAME2)

        binding = FragmentGame2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        balance = (activity as MainActivity).getBalance()
        observableBalance.set(balance)
        observableBid.set(bid)
        observableWin.set(win)

        setGradients()

        with(binding) {

            balance = (activity as MainActivity).getBalance()
            myBalance = observableBalance
            myBid = observableBid
            myWin = observableWin
            adapterSlot1 = adapter1
            adapterSlot2 = adapter2
            adapterSlot3 = adapter3

            setOnScrollListenerOnSlots()

            plusButton.setOnClickListener {
                (activity as MainActivity).playOnClickSoundOrVibrate()
                if (bid != balance) {
                    bid += 10
                    observableBid.set(bid)
                }
            }

            minusButton.setOnClickListener {
                (activity as MainActivity).playOnClickSoundOrVibrate()
                if (bid != 0) {
                    bid -= 10
                    observableBid.set(bid)
                }
            }

            playButton.setOnClickListener {
               (activity as MainActivity).vibrate(context)
                if (bid != 0) {
                    activity?.requestedOrientation = getCurrentScreenOrientation(requireContext())
                    playButton.isClickable = false
                    balance -= bid
                    if (balance == 0) {
                        balance = 1000
                    }
                    observableBalance.set(balance)
                    (activity as MainActivity).saveBalance(balance)
                    scrollCount = 0
                    scrollSlots()
                }
            }

            backButton.setOnClickListener {
                (activity as MainActivity).playOnClickSoundOrVibrate()
                findNavController().navigate(R.id.action_game2Fragment_to_chooseGameFragment)
            }
        }
    }

    private fun setOnScrollListenerOnSlots(){
        with(binding){
            setOnScrollListener(slot1RecyclerView, adapter1, 1)
            setOnScrollListener(slot2RecyclerView, adapter2, 2)
            setOnScrollListener(slot3RecyclerView, adapter3, 3)

            slot1RecyclerView.setOnTouchListener{ v, event -> true }
            slot2RecyclerView.setOnTouchListener{ v, event -> true }
            slot3RecyclerView.setOnTouchListener{ v, event -> true }
        }
    }

    private fun scrollSlots(){
        with(binding){
            scrollSlot(slot1RecyclerView)
            scrollSlot(slot2RecyclerView)
            scrollSlot(slot3RecyclerView)
        }
    }

    private fun setGradients() {
        with(binding) {
            totalTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.yellow),
                ContextCompat.getColor(requireContext(), R.color.light_yellow)
            )
            balanceTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.yellow),
                ContextCompat.getColor(requireContext(), R.color.light_yellow)
            )
            winTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.yellow),
                ContextCompat.getColor(requireContext(), R.color.light_yellow)
            )
            winValueTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.yellow),
                ContextCompat.getColor(requireContext(), R.color.light_yellow)
            )
            bidTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.yellow),
                ContextCompat.getColor(requireContext(), R.color.light_yellow)
            )
        }
    }

    private fun scrollSlot(recyclerView: RecyclerView) {
        var randomPosition = (0..40).random()
        while (randomPosition == recyclerView.getSecondVisiblePosition()
            || randomPosition == recyclerView.getSecondVisiblePosition() + 1
            || randomPosition == recyclerView.getSecondVisiblePosition() - 1
        ) {
            randomPosition = (0..40).random()
        }
        recyclerView.smoothScrollToPosition(randomPosition)
    }

    private fun onAllScrollComplete() {
        if (scrollCount == 3) {
            Timber.d(item1.toString())
            Timber.d(item2.toString())
            Timber.d(item3.toString())

            val currentGameItems = mutableListOf(item1, item2, item3)

            val currentGameItemsSet = currentGameItems.distinctBy { it.id }

            val dublicates = currentGameItems.size - currentGameItemsSet.size

            var coef = 0

            when (dublicates) {
                1 -> coef = 2
                2 -> coef = 4
            }

            win = bid * coef

            if (win != 0) {
                (activity as MainActivity).playOnWin()
            }

            balance += win

            (activity as MainActivity).saveBalance(balance)

            observableWin.set(win)
            observableBalance.set(balance)

            binding.playButton.isClickable = true

            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
    }

    private fun setOnScrollListener(
        recyclerView: RecyclerView,
        adapter: SlotAdapter,
        slotIndex: Int
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    when (slotIndex) {
                        1 -> item1 = adapter.getItemByPosition(recyclerView.getSecondVisiblePosition())
                        2 -> item2 = adapter.getItemByPosition(recyclerView.getSecondVisiblePosition())
                        3 -> item3 = adapter.getItemByPosition(recyclerView.getSecondVisiblePosition())
                    }
                    scrollCount += 1
                    onAllScrollComplete()
                }
            }
        })
    }

    private fun getGameItems(seed: Int): List<SlotItem> {
        val slotItems = mutableListOf<SlotItem>()
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game2_img1, requireContext().theme
                ), "wild"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game2_img2, requireContext().theme
                ), "cubes"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game2_img3, requireContext().theme
                ), "flower"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game2_img4, requireContext().theme
                ), "guitar"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game2_img5, requireContext().theme
                ), "rattle"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game2_img6, requireContext().theme
                ), "skull"
            )
        )
        return slotItems.shuffled(Random(seed))
    }

    private fun getCurrentScreenOrientation(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = windowManager.defaultDisplay.rotation
        return when (rotation) {
            // Check rotation and return corresponding orientation constants
            Surface.ROTATION_0 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Surface.ROTATION_180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}