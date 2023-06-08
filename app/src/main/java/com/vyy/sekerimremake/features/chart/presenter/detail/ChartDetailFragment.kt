package com.vyy.sekerimremake.features.chart.presenter.detail

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vyy.sekerimremake.MainViewModel
import com.vyy.sekerimremake.R
import com.vyy.sekerimremake.databinding.ChartEditTableBinding
import com.vyy.sekerimremake.databinding.FragmentChartDetailBinding
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.utils.ChartConstants.DAY_OF_MONTH
import com.vyy.sekerimremake.features.chart.utils.ChartConstants.MONTH
import com.vyy.sekerimremake.features.chart.utils.ChartConstants.STATE_KEYS
import com.vyy.sekerimremake.features.chart.utils.ChartConstants.YEAR
import com.vyy.sekerimremake.features.chart.utils.ChartUtils.convertToSingleDigit
import com.vyy.sekerimremake.features.chart.utils.ChartUtils.generateRowId
import com.vyy.sekerimremake.features.chart.utils.GlucoseLevelChecker
import com.vyy.sekerimremake.features.chart.utils.OnFocusChangeListenerInsulinLimits
import com.vyy.sekerimremake.utils.Response
import com.vyy.sekerimremake.utils.filters.InputFilterMax
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ChartDetailFragment : Fragment(), View.OnClickListener {

    private val arguments: ChartDetailFragmentArgs by navArgs()

    private var _binding: FragmentChartDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var includedBinding: ChartEditTableBinding
    private val viewModelMain: MainViewModel by activityViewModels()
    private val viewModelChartDetail: ChartDetailViewModel by viewModels()

    private var editTexts = listOf<EditText>()

    private var theDay = ChartDayModel(dayOfMonth = "0", month = "0", year = "2023")
    private lateinit var allChartDays: List<ChartDayModel>
    private var isAddNewConfiguration = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartDetailBinding.inflate(inflater, container, false)
        includedBinding = binding.includedChartEditTable
        return binding.root
    }

    //TODO: Loading Dialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE

        createEditTextLists()
        setListeners()

        val chart = viewModelMain.chartResponse.value

        var isInitialState = false
        if (chart is Response.Success) {
            allChartDays = chart.data
            setInitialState(savedInstanceState)
            setButtonLayout()
            fillTextViews()
            fillSingleRowHeader()
            addSharedElementAnimation()
            isInitialState = true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelMain.chartResponse.collectLatest { response ->
                        if (!isInitialState) {
                            when (response) {
                                is Response.Success -> {
                                    allChartDays = response.data
                                    checkTheDay(
                                        dayOfMonth = theDay.dayOfMonth,
                                        month = theDay.month,
                                        year = theDay.year
                                    )
                                    setButtonLayout()
                                    fillTextViews()
                                    fillSingleRowHeader()
                                }
                                is Response.Error -> {
                                    Log.e("ChartMasterFragment", response.message)
                                }
                                else -> {
                                    //TODO
                                }
                            }
                        } else {
                            isInitialState = false
                        }
                    }
                }
                launch {
                    viewModelChartDetail.addDayResponse.collect { response ->
                        doOnResponse(
                            response = response,
                            loadingMessage = getString(R.string.saving),
                            successfulMessage = getString(R.string.saved)
                        )
                    }
                }
                launch {
                    viewModelChartDetail.deleteDayResponse.collect { response ->
                        doOnResponse(
                            response = response,
                            loadingMessage = getString(R.string.deleting),
                            successfulMessage = getString(R.string.deleted)
                        )
                    }
                }
            }
        }
    }

    //TODO: Review
    private fun setInitialState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            checkTheDay(
                dayOfMonth = savedInstanceState.getString(DAY_OF_MONTH),
                month = savedInstanceState.getString(MONTH),
                year = savedInstanceState.getString(YEAR)
            )
        } else {
            checkTheDay(
                dayOfMonth = arguments.day, month = arguments.month, year = arguments.year
            )
        }
    }

    private fun createEditTextLists() {
        editTexts = listOf(
            includedBinding.editTextNumberMorningEmpty,
            includedBinding.editTextNumberMorningFull,
            includedBinding.editTextNumberAfternoonEmpty,
            includedBinding.editTextNumberAfternoonFull,
            includedBinding.editTextNumberEveningEmpty,
            includedBinding.editTextNumberEveningFull,
            includedBinding.editTextNumberNight
        )
    }

    private fun addSharedElementAnimation() {
        val chartDayBinding = binding.includedChartDay
        ViewCompat.setTransitionName(chartDayBinding.chartDay, theDay.id)
        val transition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)
        sharedElementEnterTransition = transition
        setEnterSharedElementCallback(object : androidx.core.app.SharedElementCallback() {
            override fun onMapSharedElements(
                names: List<String>, sharedElements: MutableMap<String, View>
            ) {
                super.onMapSharedElements(names, sharedElements)
                sharedElements[binding.includedChartDay.chartDay.transitionName] =
                    binding.includedChartDay.chartDay
            }
        })
    }

    private fun setListeners() {
        val buttons = arrayOf(
            includedBinding.buttonAddRow,
            includedBinding.buttonUpdate,
            binding.imageButtonCalendar,
            includedBinding.buttonMorningEmptyEdit,
            includedBinding.buttonMorningFullEdit,
            includedBinding.buttonAfternoonEmptyEdit,
            includedBinding.buttonAfternoonFullEdit,
            includedBinding.buttonEveningEmptyEdit,
            includedBinding.buttonEveningFullEdit,
            includedBinding.buttonNightEdit
        )

        buttons.forEach { it.setOnClickListener(this) }

        val inputFilters = arrayOf<InputFilter>(InputFilterMax(500))
        val minValue = resources.getInteger(R.integer.min_measurement_value)
        for (i in editTexts.indices) {
            editTexts[i].filters = inputFilters
            val lowerLimit: Int
            val upperLimit: Int
            when (i) {
                0, 2, 4 -> {
                    lowerLimit = resources.getInteger(R.integer.low_measurement_value_empty)
                    upperLimit = resources.getInteger(R.integer.high_measurement_value_empty)
                }
                1, 3, 5 -> {
                    lowerLimit = resources.getInteger(R.integer.low_measurement_value_full)
                    upperLimit = resources.getInteger(R.integer.high_measurement_value_full)
                }
                else -> {
                    lowerLimit = resources.getInteger(R.integer.low_measurement_value_night)
                    upperLimit = resources.getInteger(R.integer.high_measurement_value_night)
                }
            }

            editTexts[i].onFocusChangeListener = OnFocusChangeListenerInsulinLimits(
                requireContext(), minValue, lowerLimit, upperLimit
            )
        }
    }

    private fun setButtonLayout() {
        includedBinding.buttonAddRow.visibility =
            if (isAddNewConfiguration) View.VISIBLE else View.GONE
        includedBinding.buttonUpdate.visibility =
            if (isAddNewConfiguration) View.GONE else View.VISIBLE
    }

    private fun fillTextViews() {
        val dayFields = arrayOf(
            theDay.morningEmpty,
            theDay.morningFull,
            theDay.afternoonEmpty,
            theDay.afternoonFull,
            theDay.eveningEmpty,
            theDay.eveningFull,
            theDay.night
        )

        val editImageButtons = listOf(
            includedBinding.buttonMorningEmptyEdit,
            includedBinding.buttonMorningFullEdit,
            includedBinding.buttonAfternoonEmptyEdit,
            includedBinding.buttonAfternoonFullEdit,
            includedBinding.buttonEveningEmptyEdit,
            includedBinding.buttonEveningFullEdit,
            includedBinding.buttonNightEdit
        )

        dayFields.forEachIndexed { index, field ->
            setEditTextState(field, editTexts[index], editImageButtons[index])
        }
    }

    private fun setEditTextState(field: String?, editText: EditText, imageButton: ImageButton) {
        if (field.isNullOrBlank() || field == "0") {
            editText.isEnabled = true
            editText.setText("")
            editText.setBackgroundResource(R.drawable.edit_text_measurement)
            imageButton.visibility = View.GONE
        } else {
            editText.setText(field)
            editText.isEnabled = false
            editText.setBackgroundResource(R.drawable.edit_text_measurement_passive)
            imageButton.visibility = View.VISIBLE
        }
    }

    private fun fillSingleRowHeader() {
        val chartDayBinding = binding.includedChartDay

        val headerTextViews = arrayOf(
            chartDayBinding.textViewDayMorningEmpty,
            chartDayBinding.textViewDayMorningFull,
            chartDayBinding.textViewDayAfternoonEmpty,
            chartDayBinding.textViewDayAfternoonFull,
            chartDayBinding.textViewDayEveningEmpty,
            chartDayBinding.textViewDayEveningFull,
            chartDayBinding.textViewDayNight
        )

        chartDayBinding.textViewDayDayOfMonth.text = theDay.dayOfMonth.toString()
        val monthAndYearTogether =
            resources.getStringArray(R.array.Months)[convertToSingleDigit(theDay.month)?.toInt()
                ?: 0] + "\n" + (theDay.year ?: "0")
        chartDayBinding.textViewDayMonthAndYear.text = monthAndYearTogether

        val dayFields = arrayOf(
            theDay.morningEmpty,
            theDay.morningFull,
            theDay.afternoonEmpty,
            theDay.afternoonFull,
            theDay.eveningEmpty,
            theDay.eveningFull,
            theDay.night
        )

        //Chance background according to value range.
        //TODO: Same lines of code exist in ChartAdapter. Fix this!
        var warningFlag: Int
        dayFields.forEachIndexed() { i, field ->
            if (field.isNullOrBlank() || field == "0") {
                headerTextViews[i].text = ""
                headerTextViews[i].setBackgroundResource(R.drawable.text_frame)
            } else {
                headerTextViews[i].text = field.toString()
                warningFlag =
                    GlucoseLevelChecker.checkGlucoseLevel(requireContext(), i, field.toInt())
                when (warningFlag) {
                    2 -> {
                        headerTextViews[i].setBackgroundResource(R.drawable.text_frame_warning_range)
                    }
                    3 -> {
                        headerTextViews[i].setBackgroundResource(R.drawable.text_frame_margin_range)
                    }
                    else -> {
                        headerTextViews[i].setBackgroundResource(R.drawable.text_frame_normal_range)
                    }
                }
            }
        }
    }

    //Check all values while exiting this ChartDetailFragment if there is any value outside our range.
    private fun lastCheckValues() {
        editTexts.forEach { e ->
            if (!(e.text.isNullOrBlank())) {
                val value = e.text.toString().toInt()
                if (value < resources.getInteger(R.integer.min_measurement_value) || value > resources.getInteger(
                        R.integer.max_measurement_value
                    )
                ) {
                    e.setText("")
                }
            }
        }
    }

    private fun startParent(buttonId: Int) {
        if (theDay.id.isNullOrBlank()) {
            Toast.makeText(requireContext(), getString(R.string.choose_a_date), Toast.LENGTH_SHORT)
                .show()
            return
        }

        val userResponse = viewModelMain.userResponse.value
        if (userResponse is Response.Success) {
            setChartDayModel()

            val isAllMeasurementsEmpty = isAllMeasurementsEmpty()
            if (buttonId == R.id.button_addRow && isAllMeasurementsEmpty) {
                Toast.makeText(
                    requireContext(), getString(R.string.enter_a_measurement), Toast.LENGTH_SHORT
                ).show()
            } else {
                if (isAllMeasurementsEmpty) {
                    viewModelChartDetail.deleteDay(theDay.id!!)
                } else {
                    viewModelChartDetail.addDay(theDay)
                }
            }
        } else {
            Toast.makeText(
                requireContext(), getString(R.string.user_info_not_found), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun doOnResponse(
        response: Response<Boolean>,
        loadingMessage: String,
        successfulMessage: String
    ) {
        val message = when (response) {
            is Response.Success -> {
                if (response.data) {
                    requireActivity().onBackPressed()
                }
                successfulMessage

            }
            is Response.Error -> {
                Log.e("SettingsFragment", response.message)
                response.message
            }
            else -> {
                loadingMessage
            }
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setChartDayModel() {
        theDay.morningEmpty = includedBinding.editTextNumberMorningEmpty.text?.toString() ?: ""
        theDay.morningFull = includedBinding.editTextNumberMorningFull.text?.toString() ?: ""
        theDay.afternoonEmpty = includedBinding.editTextNumberAfternoonEmpty.text?.toString() ?: ""
        theDay.afternoonFull = includedBinding.editTextNumberAfternoonFull.text?.toString() ?: ""
        theDay.eveningEmpty = includedBinding.editTextNumberEveningEmpty.text?.toString() ?: ""
        theDay.eveningFull = includedBinding.editTextNumberEveningFull.text?.toString() ?: ""
        theDay.night = includedBinding.editTextNumberNight.text?.toString() ?: ""
    }

    private fun isAllMeasurementsEmpty() =
        theDay.morningEmpty.isNullOrBlank() && theDay.morningFull.isNullOrBlank()
                && theDay.afternoonEmpty.isNullOrBlank() && theDay.afternoonFull.isNullOrBlank()
                && theDay.eveningEmpty.isNullOrBlank() && theDay.eveningFull.isNullOrBlank()
                && theDay.night.isNullOrBlank()

    private fun checkTheDay(
        dayOfMonth: String? = null,
        month: String? = null,
        year: String? = null,
    ) {
        val id = generateRowId(dayOfMonth, month, year)
        val day = allChartDays.find { it.id == id }

        if (day != null) {
            isAddNewConfiguration = false
            theDay = day
        } else {
            isAddNewConfiguration = true
            theDay = ChartDayModel()
            theDay.id = generateRowId(dayOfMonth, month, year)
            theDay.dayOfMonth = dayOfMonth
            theDay.month = month
            theDay.year = year
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(), { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                editTexts.forEach { it.setText("") }
                checkTheDay(dayOfMonth.toString(), month.toString(), year.toString())
                setButtonLayout()
                fillTextViews()
                fillSingleRowHeader()
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }

    override fun onClick(v: View) {
        when (val viewId = v.id) {
            R.id.button_addRow, R.id.button_update -> {
                lastCheckValues()
                startParent(viewId)
            }
            R.id.imageButton_calendar -> {
                showDatePickerDialog()
            }
            R.id.button_morningEmpty_edit -> {
                setEditTextState(
                    null,
                    includedBinding.editTextNumberMorningEmpty,
                    includedBinding.buttonMorningEmptyEdit
                )
            }
            R.id.button_morningFull_edit -> {
                setEditTextState(
                    null,
                    includedBinding.editTextNumberMorningFull,
                    includedBinding.buttonMorningFullEdit
                )
            }
            R.id.button_afternoonEmpty_edit -> {
                setEditTextState(
                    null,
                    includedBinding.editTextNumberAfternoonEmpty,
                    includedBinding.buttonAfternoonEmptyEdit
                )
            }
            R.id.button_afternoonFull_edit -> {
                setEditTextState(
                    null,
                    includedBinding.editTextNumberAfternoonFull,
                    includedBinding.buttonAfternoonFullEdit
                )
            }
            R.id.button_eveningEmpty_edit -> {
                setEditTextState(
                    null,
                    includedBinding.editTextNumberEveningEmpty,
                    includedBinding.buttonEveningEmptyEdit
                )
            }
            R.id.button_eveningFull_edit -> {
                setEditTextState(
                    null,
                    includedBinding.editTextNumberEveningFull,
                    includedBinding.buttonEveningFullEdit
                )
            }
            R.id.button_night_edit -> {
                setEditTextState(
                    null, includedBinding.editTextNumberNight, includedBinding.buttonNightEdit
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(DAY_OF_MONTH, theDay.dayOfMonth)
        outState.putString(MONTH, theDay.month)
        outState.putString(YEAR, theDay.year)
        for (i in STATE_KEYS.indices) {
            outState.putBoolean(STATE_KEYS[i], editTexts[i].isEnabled)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//  private suspend fun fillChartWithRandomData(
//        monthStart: Int,
//        monthEnd: Int,
//        dayStart: Int,
//        dayEnd: Int
//    ) {
//        var model: ChartDayModel?
//        for (i in monthStart until monthEnd) {
//            for (j in dayStart until dayEnd) {
//                model = ChartDayModel(
//                    generateRowId(j.toString(), i.toString(), "2023"),
//                    j.toString(),
//                    i.toString(),
//                    "2023",
//                    (if (Random().nextInt(10) > 1) Random().nextInt(75) + 65 else 0).toString(),
//                    (if (Random().nextInt(10) > 1) Random().nextInt(95) + 95 else 0).toString(),
//                    (if (Random().nextInt(10) > 1) Random().nextInt(75) + 65 else 0).toString(),
//                    (if (Random().nextInt(10) > 1) Random().nextInt(95) + 95 else 0).toString(),
//                    (if (Random().nextInt(10) > 1) Random().nextInt(75) + 65 else 0).toString(),
//                    (if (Random().nextInt(10) > 1) Random().nextInt(95) + 95 else 0).toString(),
//                    (if (Random().nextInt(10) > 5) Random().nextInt(75) + 85 else 0).toString()
//                )
//                val userIdResponse = viewModelMain.userResponse.value
//                if (userIdResponse is Response.Success) {
//                    viewModelChartDetail.addDay(model)
//                    delay(500)
//                }
//
//            }
//        }
//    }
}