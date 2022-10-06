package com.yongjincompany.hackerthonandroid.features.calendar.view

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.yongjincompany.hackerthonandroid.R
import com.yongjincompany.hackerthonandroid.database.RoomDatabase
import com.yongjincompany.hackerthonandroid.databinding.ActivityCalendarBinding
import com.yongjincompany.hackerthonandroid.features.calendar.vm.CalendarViewModel
import com.yongjincompany.hackerthonandroid.features.diary.view.StateDiaryActivity
import java.time.LocalDate

class CalendarActivity : AppCompatActivity() {

    lateinit var binding: ActivityCalendarBinding
    lateinit var calendarViewModel: CalendarViewModel

    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        calendarViewModel.database = RoomDatabase.getInstance(this)
        bindingView()
        observeLiveData()
    }

    private fun bindingView() {
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calendarViewModel.currentDate.value = "${year}-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
        }

        binding.fabAdd.setOnClickListener {
            toggleFab()
        }

        binding.layoutAddStateDiary.setOnClickListener {
            navigateToStateDiary()
        }

        binding.fabDiary.setOnClickListener { 
            navigateToStateDiary()
        }
    }

    private fun observeLiveData() = with(calendarViewModel) {
        currentDate.observe(this@CalendarActivity) {
            getStateDiaryByDate(it)
        }

        dayStateEntity.observe(this@CalendarActivity) {
            if (it == null) {
                binding.layoutDayState.visibility = View.GONE
            } else {
                binding.layoutDayState.visibility = View.VISIBLE
                binding.tvBodyState.text = it.bodyState
                if (it.mood == "좋아요!") {
                    binding.ivMood.setImageResource(R.drawable.ic_happy)
                } else {
                    binding.ivMood.setImageResource(R.drawable.ic_sad)
                }
                binding.tvBehaviorChange.text = it.behaviorChange
            }
        }
    }

    private fun toggleFab() {
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.fabDiary, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabPeriodEnd, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabPeriodStart, "translationY", 0f).apply { start() }

            ObjectAnimator.ofFloat(binding.tvDiary, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.tvPeriodEnd, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.tvPeriodStart, "translationY", 0f).apply { start() }

            binding.tvDiary.visibility = View.GONE
            binding.tvPeriodEnd.visibility = View.GONE
            binding.tvPeriodStart.visibility = View.GONE

            binding.fabAdd.setImageResource(R.drawable.ic_add)
        } else {
            ObjectAnimator.ofFloat(binding.fabDiary, "translationY", -150f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabPeriodEnd, "translationY", -300f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabPeriodStart, "translationY", -450f).apply { start() }

            ObjectAnimator.ofFloat(binding.tvDiary, "translationY", -150f).apply { start() }
            ObjectAnimator.ofFloat(binding.tvPeriodEnd, "translationY", -300f).apply { start() }
            ObjectAnimator.ofFloat(binding.tvPeriodStart, "translationY", -450f).apply { start() }

            binding.tvDiary.visibility = View.VISIBLE
            binding.tvPeriodEnd.visibility = View.VISIBLE
            binding.tvPeriodStart.visibility = View.VISIBLE

            binding.fabAdd.setImageResource(R.drawable.ic_clear)
        }

        isFabOpen = !isFabOpen

    }

    private fun navigateToStateDiary() {
        val intent = Intent(this, StateDiaryActivity::class.java)
        intent.putExtra("date", calendarViewModel.currentDate.value ?: LocalDate.now().toString())
        startActivity(intent)
    }

    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar)
        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        binding.vm = calendarViewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
    }
}