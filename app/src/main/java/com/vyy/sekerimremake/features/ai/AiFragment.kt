package com.vyy.sekerimremake.features.ai

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.vyy.sekerimremake.R
import com.vyy.sekerimremake.databinding.FragmentAiBinding
import com.vyy.sekerimremake.features.chart.utils.GlucoseLevelChecker
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.pow


class AiFragment : Fragment(R.layout.fragment_ai), View.OnClickListener {

    companion object {
        private const val VALUE_SIZE = 6
    }

    private var _binding: FragmentAiBinding? = null
    private val binding get() = _binding!!

    private lateinit var editTexts: List<EditText>
    private lateinit var headerNumberTexts: List<TextView>
    private val values = FloatArray(VALUE_SIZE)
    private var mean: Float = 0.0f
    private var std: Float = 1.0f


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPredict.setOnClickListener(this@AiFragment)

        bindEditTexts()
        handleViewInitialState()
        addGlucoseTextChangeListeners()
    }

    private fun makeRequiredCalculations() {
        calculateMean()
        getStandardDeviation()
        standardizeValues()
    }

    private fun calculateMean() {
        mean = values.average().toFloat()
        Log.d("AI", "calculateMean: $mean")
    }

    private fun getStandardDeviation() {
        var sum = 0.0
        for (i in values.indices) {
            sum += (values[i] - mean).pow(2.0f)
        }
        std = kotlin.math.sqrt(sum / values.size).toFloat()
        Log.d("AI", "getStandardDeviation: $std")
    }

    private fun standardizeValues() {
        for (i in values.indices) {
            values[i] = (values[i] - mean) / std
        }
        Log.d("AI", "standardizeValues: $values")
    }

    // Inverse transform
    private fun inverseTransform(standardValue: Float) = (standardValue * std) + mean

    private fun predict() {
        binding.progressBarPredicting.visibility = View.VISIBLE
        getValuesFromEditTexts()
        makeRequiredCalculations()

        val tfliteModel = loadModelFile(requireActivity())

        //inputData.array(value2)
        val data = arrayOf(
            arrayOf(values)
        )

        val labelProbArray: Array<FloatArray> = Array(1) { FloatArray(VALUE_SIZE) }
        val tflite = Interpreter(tfliteModel, Interpreter.Options())
        tflite.run(data, labelProbArray)

        Log.d("AI", "labelProbArray: $labelProbArray")

        val result = inverseTransform(labelProbArray[0][5])

        handleResultUi(result)
    }

    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd("lstmmodel.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun bindEditTexts() {
        with(binding) {
            editTexts = listOf(
                editTextNumberGlucose1,
                editTextNumberGlucose2,
                editTextNumberGlucose3,
                editTextNumberGlucose4,
                editTextNumberGlucose5,
                editTextNumberGlucose6,
            )

            headerNumberTexts = listOf(
                textViewHeaderNumber1,
                textViewHeaderNumber2,
                textViewHeaderNumber3,
                textViewHeaderNumber4,
                textViewHeaderNumber5,
                textViewHeaderNumber6,
            )
        }
    }

    private fun handleViewInitialState() {
        editTexts.forEachIndexed { index, editText ->
            if (index == 0) {
                editText.visibility = View.VISIBLE
                headerNumberTexts[index].visibility = View.VISIBLE
            } else {
                editText.visibility = View.GONE
                headerNumberTexts[index].visibility = View.GONE
            }
        }

        binding.apply {
            progressBarEntering.progress = 0
            textViewPredictedResultValue.text = ""
            buttonPredict.visibility = View.GONE
            constraintLayoutPredictedResult.visibility = View.GONE
        }
    }

    private fun addGlucoseTextChangeListeners() {
        val glucoseEditTexts = listOf(
            binding.editTextNumberGlucose1, binding.editTextNumberGlucose2, binding.editTextNumberGlucose3,
            binding.editTextNumberGlucose4, binding.editTextNumberGlucose5, binding.editTextNumberGlucose6
        )
        glucoseEditTexts.forEachIndexed { index, editText  ->
            editText.doAfterTextChanged { text ->
                editText.postDelayed({
                    if (text.isNullOrEmpty() || text.toString().toInt() == 0) {
                        editText.setBackgroundResource(R.drawable.text_frame)

                        if (index != glucoseEditTexts.size - 1) {
                            glucoseEditTexts[index + 1].visibility = View.GONE
                            headerNumberTexts[index + 1].visibility = View.GONE
                        } else {
                            binding.buttonPredict.visibility = View.GONE
                        }
                        binding.progressBarEntering.apply {
                            progress = (max / VALUE_SIZE) * (index)
                        }
                    } else {
                        if (index != glucoseEditTexts.size - 1) {
                            glucoseEditTexts[index + 1].visibility = View.VISIBLE
                            headerNumberTexts[index + 1].visibility = View.VISIBLE

                            binding.progressBarEntering.apply {
                                progress = (max / VALUE_SIZE) * (index + 1)
                            }
                        } else {
                            binding.buttonPredict.visibility = View.VISIBLE
                            binding.progressBarEntering.apply {
                                progress = max
                            }
                        }

                        when (GlucoseLevelChecker.checkGlucoseValuesForAI(
                            this.context,
                            text.toString().toInt()
                        )) {
                            2 -> {
                                editText.setBackgroundResource(R.drawable.text_frame_warning_range)
                            }
                            3 -> {
                                editText.setBackgroundResource(R.drawable.text_frame_margin_range)
                            }
                            else -> {
                                editText.setBackgroundResource(R.drawable.text_frame_normal_range)
                            }
                        }
                    }
                }, 500)
            }
        }
    }

    private fun handleResultUi(result: Float) {
        // Hide keyboard
        requireActivity().currentFocus?.let { view ->
            val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        binding.progressBarPredicting.visibility = View.INVISIBLE
        val resultBackground: Int = when (GlucoseLevelChecker.checkGlucoseValuesForAI(
            this.context,
            result.toInt()
        )) {
            2 -> {
                R.drawable.text_frame_warning_range
            }
            3 -> {
                R.drawable.text_frame_margin_range
            }
            else -> {
                R.drawable.text_frame_normal_range
            }
        }
        binding.constraintLayoutPredictedResult.setBackgroundResource(resultBackground)
        binding.textViewPredictedResultValue.text = "$result"
        binding.constraintLayoutPredictedResult.visibility = View.VISIBLE
    }

    private fun getValuesFromEditTexts() {
        if (editTexts.size == values.size) {
            for (i in editTexts.indices) {
                if (editTexts[i].text.toString().isNotEmpty()) {
                    values[i] = editTexts[i].text.toString().toFloat()
                } else {
                    values[i] = 0.0f
                }
            }
        }
        Log.d("TAG", "getValuesFromEditTexts: $values")
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.button_predict -> predict()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}