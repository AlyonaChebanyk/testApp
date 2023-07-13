package com.example.testapp.game

import android.animation.Animator
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ObservableInt
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.testapp.MainActivity
import com.example.testapp.R
import com.example.testapp.databinding.FragmentBonusGameBinding
import com.example.testapp.extensions.setGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BonusGameFragment : Fragment() {

    private lateinit var binding: FragmentBonusGameBinding

    lateinit var front_animation: AnimatorSet
    lateinit var back_animation: AnimatorSet

    private val groupAnimatorSet = AnimatorSet()
    private val currentAnimatorSet = mutableListOf<Animator>()

    var scale: Float = 0f

    private var balance = 0
    private var observableBalance = ObservableInt()
    private var observableBid = ObservableInt(10)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = FragmentBonusGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scale = requireActivity().applicationContext.resources.displayMetrics.density

        setGradients()

        front_animation = AnimatorInflater.loadAnimator(
            requireActivity().applicationContext,
            R.animator.front_animator
        ) as AnimatorSet
        back_animation = AnimatorInflater.loadAnimator(
            requireActivity().applicationContext,
            R.animator.back_animator
        ) as AnimatorSet

        balance = (activity as MainActivity).getBalance()
        observableBalance.set(balance)

        with(binding) {

            myBid = observableBid
            myBalance = observableBalance

            plusButton.setOnClickListener {
                observableBid.set(observableBid.get() + 10)
            }
            minusButton.setOnClickListener {
                if (observableBid.get() != 0){
                    observableBid.set(observableBid.get() - 10)
                }
            }

            for (card in getCardViews()) {
                card.cameraDistance = 8000 * scale
            }

            for (frontCard in getFrontCardViews()) {
                frontCard.cameraDistance = 8000 * scale
            }

            setNewGame()

            backButton.setOnClickListener {
                (activity as MainActivity).playOnClickSoundOrVibrate()
                findNavController().navigate(R.id.action_bonusGameFragment_to_chooseGameFragment)
            }
        }
    }

    private fun setGradients() {
        with(binding) {
            bonusGameTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.burgundy),
                ContextCompat.getColor(requireContext(), R.color.red)
            )
            bidTextView.setGradient(
                ContextCompat.getColor(requireContext(), R.color.dark_yellow),
                ContextCompat.getColor(requireContext(), R.color.light_grey)
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

    private fun disableAllCardClick() {
        with(binding) {
            cardOneImageView.isClickable = false
            cardTwoImageView.isClickable = false
            cardThreeImageView.isClickable = false
            cardFourImageView.isClickable = false
        }
    }

    private fun enableAllCardClick() {
        with(binding) {
            cardOneImageView.isClickable = true
            cardTwoImageView.isClickable = true
            cardThreeImageView.isClickable = true
            cardFourImageView.isClickable = true
        }
    }

    private fun getCardViews(): MutableList<ImageView> {
        with(binding) {
            return mutableListOf(
                cardOneImageView,
                cardTwoImageView,
                cardThreeImageView,
                cardFourImageView
            )
        }
    }

    private fun getFrontCardViews(): MutableList<ImageView> {
        with(binding) {
            return mutableListOf(
                cardOneFrontalImageView,
                cardTwoFrontalImageView,
                cardThreeFrontalImageView,
                cardFourFrontalImageView
            )
        }
    }
    private fun setOnClickListenerOnCard(
        card: ImageView,
        frontalCard: ImageView,
        currentCardItem: CardItem,
        chosenIndex: Int
    ) {
        frontalCard.setImageDrawable(currentCardItem.image)

        card.setOnClickListener {
            if (observableBid.get() != 0){
                (activity as MainActivity).vibrateIfMusicOff()
                if (currentCardItem.coef != 0) {
                    (activity as MainActivity).playOnWin()
                    balance += observableBid.get() * currentCardItem.coef
                } else {
                    balance -= observableBid.get()
                    if (balance <= 0) {
                        balance = 1000
                    }
                    (activity as MainActivity).playOnLose()
                }
                (activity as MainActivity).saveBalance(balance)
                observableBalance.set(balance)
                disableAllCardClick()
                openGameCard(card, frontalCard)
                lifecycleScope.launch {
                    delay(1000)
                    openAllGameCards(chosenIndex)
                    delay(2000)
                    closeAllGameCards()
                    delay(1000)
                    enableAllCardClick()
                    setNewGame()
                }
            }
        }
    }

    private fun setNewGame() {

        val cardItemList = getGameCards().shuffled()

        for (i in (0..3)) {
            setOnClickListenerOnCard(
                getCardViews()[i],
                getFrontCardViews()[i],
                cardItemList[i],
                i
            )
        }
    }

    private fun closeAllGameCards() {
        currentAnimatorSet.clear()
        for (i in (0..3)) {
            val f_a = loadFrontAnimator()
            val f_b = loadBackAnimator()
            f_a.setTarget(getFrontCardViews()[i])
            f_b.setTarget(getCardViews()[i])
            currentAnimatorSet.add(f_a)
            currentAnimatorSet.add(f_b)
        }
        groupAnimatorSet.playTogether(currentAnimatorSet)
        for (animator in currentAnimatorSet) {
            animator.start()
        }
    }

    private fun loadFrontAnimator(): AnimatorSet{
        return AnimatorInflater.loadAnimator(
            requireActivity().applicationContext,
            R.animator.front_animator
        ) as AnimatorSet
    }

    private fun loadBackAnimator(): AnimatorSet {
        return AnimatorInflater.loadAnimator(
            requireActivity().applicationContext,
            R.animator.back_animator
        ) as AnimatorSet
    }

    private fun openGameCard(
        card: ImageView,
        frontalCard: ImageView
    ) {
        front_animation.setTarget(card)
        back_animation.setTarget(frontalCard)
        back_animation.start()
        front_animation.start()
    }

    private fun openAllGameCards(chosenIndex: Int) {
        currentAnimatorSet.clear()
        for (i in (0..3)) {
            if (i != chosenIndex) {
                val f_a = loadFrontAnimator()
                val f_b = loadBackAnimator()
                f_a.setTarget(getCardViews()[i])
                f_b.setTarget(getFrontCardViews()[i])
                currentAnimatorSet.add(f_a)
                currentAnimatorSet.add(f_b)
            }
        }
        groupAnimatorSet.playTogether(currentAnimatorSet)
        for (animator in currentAnimatorSet) {
            animator.start()
        }
    }


    private fun getGameCards(): MutableList<CardItem> {
        val bonusListDrawable = mutableListOf<CardItem>()
        bonusListDrawable.add(
            CardItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.bonus_game_card1, requireContext().theme
                ), 3
            )
        )
        bonusListDrawable.add(
            CardItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.bonus_game_card2, requireContext().theme
                ), 4
            )
        )
        bonusListDrawable.add(
            CardItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources, R.drawable.bonus_game_card3, requireContext().theme
                ), 2
            )
        )
        bonusListDrawable.add(
            CardItem(
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.bonus_game_card_empty,
                    requireContext().theme
                ), 0
            )
        )
        return bonusListDrawable
    }
}