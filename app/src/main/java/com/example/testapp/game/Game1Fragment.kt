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
import com.example.testapp.databinding.FragmentGame1Binding
import com.example.testapp.extensions.setGradient
import com.mysticmira.ge7.utils.getSecondVisiblePosition
import com.mysticmira.ge7.utils.getThirdVisiblePosition
import timber.log.Timber
import kotlin.random.Random


class Game1Fragment : Fragment() {

    private lateinit var binding: FragmentGame1Binding

    private lateinit var adapter1: SlotAdapter
    private lateinit var adapter2: SlotAdapter
    private lateinit var adapter3: SlotAdapter
    private lateinit var adapter4: SlotAdapter
    private lateinit var adapter5: SlotAdapter

    private lateinit var item1_1: SlotItem
    private lateinit var item1_2: SlotItem
    private lateinit var item1_3: SlotItem
    private lateinit var item1_4: SlotItem
    private lateinit var item1_5: SlotItem

    private lateinit var item2_1: SlotItem
    private lateinit var item2_2: SlotItem
    private lateinit var item2_3: SlotItem
    private lateinit var item2_4: SlotItem
    private lateinit var item2_5: SlotItem

    private var scrollCount: Int = 0

    private var bid = 10
    private var observableBid = ObservableInt()

    private var balance = 0
    private var observableBalance = ObservableInt()

    private var win = 0
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

        adapter1 = SlotAdapter(requireContext(), getGameItems(1), SlotType.GAME1)
        adapter2 = SlotAdapter(requireContext(), getGameItems(2), SlotType.GAME1)
        adapter3 = SlotAdapter(requireContext(), getGameItems(3), SlotType.GAME1)
        adapter4 = SlotAdapter(requireContext(), getGameItems(4), SlotType.GAME1)
        adapter5 = SlotAdapter(requireContext(), getGameItems(5), SlotType.GAME1)

        binding = FragmentGame1Binding.inflate(inflater, container, false)
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

            myBalance = observableBalance
            myBid = observableBid
            myWin = observableWin
            adapterSlot1 = adapter1
            adapterSlot2 = adapter2
            adapterSlot3 = adapter3
            adapterSlot4 = adapter4
            adapterSlot5 = adapter5
            lifecycleOwner = this@Game1Fragment

            setOnScrollListenersOnSlots()

            plusButton.setOnClickListener {
                if (bid != balance) {
                    (activity as MainActivity).playOnClickSoundOrVibrate()
                    bid += 10
                    observableBid.set(bid)
                }
            }

            minusButton.setOnClickListener {
                if (bid != 0) {
                    (activity as MainActivity).playOnClickSoundOrVibrate()
                    bid -= 10
                    observableBid.set(bid)
                }
            }

            playButton.setOnClickListener {
                (activity as MainActivity).vibrate(context)
                Timber.d("Bid: $bid")
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
                findNavController().navigate(R.id.action_game1Fragment_to_chooseGameFragment)
            }
        }
    }

    private fun setOnScrollListenersOnSlots(){
        with(binding){
            setOnScrollListener(slot1RecyclerView, adapter1, 1)
            setOnScrollListener(slot2RecyclerView, adapter2, 2)
            setOnScrollListener(slot3RecyclerView, adapter3, 3)
            setOnScrollListener(slot4RecyclerView, adapter4, 4)
            setOnScrollListener(slot5RecyclerView, adapter5, 5)

            slot1RecyclerView.setOnTouchListener { v, event -> true }
            slot2RecyclerView.setOnTouchListener { v, event -> true }
            slot3RecyclerView.setOnTouchListener { v, event -> true }
            slot4RecyclerView.setOnTouchListener { v, event -> true }
            slot5RecyclerView.setOnTouchListener { v, event -> true }
        }
    }

    private fun scrollSlots(){
        with(binding){
            scrollSlot(slot1RecyclerView)
            scrollSlot(slot2RecyclerView)
            scrollSlot(slot3RecyclerView)
            scrollSlot(slot4RecyclerView)
            scrollSlot(slot5RecyclerView)
        }
    }

    private fun setGradients() {
        with(binding) {

            spinTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.burgundy),
                ContextCompat.getColor(requireContext(), R.color.red)
            )
            plusTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.burgundy),
                ContextCompat.getColor(requireContext(), R.color.red)
            )
            minusTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.burgundy),
                ContextCompat.getColor(requireContext(), R.color.red)
            )
        }
    }

    private fun scrollSlot(recyclerView: RecyclerView) {
        var randomPosition = (0..45).random()
        while (randomPosition == recyclerView.getSecondVisiblePosition()
            || randomPosition == recyclerView.getSecondVisiblePosition() + 1
            || randomPosition == recyclerView.getSecondVisiblePosition() - 1
            || randomPosition == recyclerView.getSecondVisiblePosition() + 2
            || randomPosition == recyclerView.getSecondVisiblePosition() - 2
        ) {
            randomPosition = (0..45).random()
        }
        Timber.d("Scroll slot: $recyclerView")
        recyclerView.smoothScrollToPosition(randomPosition)
    }

    private fun onAllScrollComplete() {
        Timber.d("Scroll count: $scrollCount")
        if (scrollCount == 5) {
            Timber.d(item1_1.toString())
            Timber.d(item1_2.toString())
            Timber.d(item1_3.toString())
            Timber.d(item1_4.toString())
            Timber.d(item1_5.toString())

            Timber.d(item2_1.toString())
            Timber.d(item2_2.toString())
            Timber.d(item2_3.toString())
            Timber.d(item2_4.toString())
            Timber.d(item2_5.toString())

            val currentGameItems = mutableListOf(item1_1, item1_2, item1_3, item1_4, item1_5)
            val currentGameItemsSet = currentGameItems.distinctBy { it.id }
            val dublicates = currentGameItems.size - currentGameItemsSet.size
            var firstCoef = 0.0

            when (dublicates) {
                1 -> firstCoef = 1.3
                2 -> firstCoef = 2.0
                3 -> firstCoef = 4.0
                4 -> firstCoef = 10.0
            }

            val currentGameItemsSecondRaw =
                mutableListOf(item2_1, item2_2, item2_3, item2_4, item2_5)
            val currentGameItemsSetSecondRaw = currentGameItemsSecondRaw.distinctBy { it.id }
            val dublicatesRecondRaw =
                currentGameItemsSecondRaw.size - currentGameItemsSetSecondRaw.size
            var secondCoef = 0.0

            when (dublicatesRecondRaw) {
                1 -> secondCoef = 1.3
                2 -> secondCoef = 2.0
                3 -> secondCoef = 4.0
                4 -> secondCoef = 10.0
            }

            win = (bid * (firstCoef + secondCoef)).toInt()

            if (win != 0) {
                (activity as MainActivity).playOnWin()
            }
            balance += win
            observableWin.set(win)
            observableBalance.set(balance)

            (activity as MainActivity).saveBalance(balance)

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
                    Timber.d("On scrolled: $recyclerView")
                    when (slotIndex) {
                        1 -> {
                            item1_1 =
                                adapter.getItemByPosition(recyclerView.getSecondVisiblePosition())
                            item2_1 =
                                adapter.getItemByPosition(recyclerView.getThirdVisiblePosition())
                        }
                        2 -> {
                            item1_2 =
                                adapter.getItemByPosition(recyclerView.getSecondVisiblePosition())
                            item2_2 =
                                adapter.getItemByPosition(recyclerView.getThirdVisiblePosition())
                        }
                        3 -> {
                            item1_3 =
                                adapter.getItemByPosition(recyclerView.getSecondVisiblePosition())
                            item2_3 =
                                adapter.getItemByPosition(recyclerView.getThirdVisiblePosition())
                        }
                        4 -> {
                            item1_4 =
                                adapter.getItemByPosition(recyclerView.getSecondVisiblePosition())
                            item2_4 =
                                adapter.getItemByPosition(recyclerView.getThirdVisiblePosition())
                        }
                        5 -> {
                            item1_5 =
                                adapter.getItemByPosition(recyclerView.getSecondVisiblePosition())
                            item2_5 =
                                adapter.getItemByPosition(recyclerView.getThirdVisiblePosition())
                        }
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
                    requireContext().resources, R.drawable.game1_img1, requireContext().theme
                ), "blue"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game1_img2, requireContext().theme
                ), "green"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game1_img3, requireContext().theme
                ), "wild_tomato"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game1_img4, requireContext().theme
                ), "red"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game1_img5, requireContext().theme
                ), "wild_ketchup"
            )
        )
        slotItems.add(
            SlotItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.game1_img6, requireContext().theme
                ), "pink"
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