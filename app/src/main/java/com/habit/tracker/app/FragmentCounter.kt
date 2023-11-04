package com.habit.tracker.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class FragmentCounter : Fragment() {

    private var dateStart = Date()
    private lateinit var prefs: SharedPreferences
    private val APP_PREFERENCES_DAYS = "days"
    private val APP_PREFERENCES_KEY = "key"
    private val APP_PREFERENCES_FIRSTTIME = "FIRST_TIME"
    private val APP_PREFERENCES_MONEYSAVED = "money"
    private val APP_PREFERENCES_CIGS = "cigs"
    private var moneySavedVar = 0
    private var cigsPerDayTotalInt = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.counter_fragment, container, false)

        prefs = view.context.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val dateformat = sdf.format(dateStart)
        val editor = prefs.edit()
        /* editor.putBoolean(APP_PREFERENCES_FIRSTTIME,true)
         editor.apply()*/
        val isFirstTimeOpening = prefs.getBoolean(APP_PREFERENCES_FIRSTTIME, true)
        Log.d("firstTime", " " + isFirstTimeOpening)

        val totalDays = view.findViewById<TextView>(R.id.smokingDays)
        val moneySaved = view.findViewById<TextView>(R.id.moneySaved)
        val cigsPerDay = view.findViewById<TextView>(R.id.cigsTotal)
        val cigPriceEditTxt = view.findViewById<TextInputEditText>(R.id.cigPrice)
        val cigPerDayEditTxt = view.findViewById<TextInputEditText>(R.id.cigPerDay)
        val alcPriceEditTxt = view.findViewById<TextInputEditText>(R.id.alcPrice)
        val iFailedButton = view.findViewById<Button>(R.id.stopButton)
        if (isFirstTimeOpening) {
            view.findViewById<Button>(R.id.startButton).setOnClickListener {
                if (cigPerDayEditTxt.text!!.isEmpty() || cigPriceEditTxt.text!!.isEmpty() || alcPriceEditTxt.text!!.isEmpty()) {
                    Toast.makeText(view.context, "Fill all the fields", Toast.LENGTH_SHORT).show()
                } else {
                    view.findViewById<LinearLayout>(R.id.editTextLayout).visibility = View.GONE
                    view.findViewById<LinearLayout>(R.id.daysLayout).visibility = View.VISIBLE
                    val currentDate = sdf.format(Date())
                    val days = TimeUnit.DAYS.convert(
                        sdf.parse(currentDate).time -
                                sdf.parse(dateformat).time,
                        TimeUnit.MILLISECONDS
                    )
                    cigsPerDayTotalInt = cigPerDayEditTxt.text.toString().toInt()
                    totalDays.text = days.toString()
                    moneySavedVar =
                        cigPriceEditTxt.text.toString().toInt() + alcPriceEditTxt.text.toString()
                            .toInt()
                    if (totalDays.text.equals("0")) {

                        moneySaved.text = "0"
                        cigsPerDay.text = "0"
                    } else {
                        moneySaved.text = moneySavedVar.toString()
                        cigsPerDay.text = cigPerDayEditTxt.text
                    }
                    editor.putString(APP_PREFERENCES_DAYS, dateformat.toString()).apply()
                    editor.putInt(APP_PREFERENCES_MONEYSAVED, moneySavedVar).apply()
                    editor.putInt(APP_PREFERENCES_CIGS, cigsPerDayTotalInt).apply()
                    editor.putBoolean(APP_PREFERENCES_FIRSTTIME, false).apply()
                }

            }


        } else {

            Log.d("mon", " " + prefs.getInt(APP_PREFERENCES_MONEYSAVED, 0))
            Log.d("cig", " " + prefs.getInt(APP_PREFERENCES_CIGS, 0))
            view.findViewById<LinearLayout>(R.id.editTextLayout).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.daysLayout).visibility = View.VISIBLE
            val currentDate = sdf.format(Date())
            val dateStart2 = prefs.getString(APP_PREFERENCES_DAYS, "")
            val moneySavedInt = prefs.getInt(APP_PREFERENCES_MONEYSAVED, 0)
            val cigsTotalInt = prefs.getInt(APP_PREFERENCES_CIGS, 0)
            val moneyTotal =
                moneySavedInt + (moneySavedInt * (totalDays.text.toString().toInt()))
            val cigsTotal = cigsTotalInt + (cigsTotalInt * totalDays.text.toString().toInt())
            val days = TimeUnit.DAYS.convert(
                sdf.parse(currentDate).time -
                        sdf.parse(dateStart2).time,
                TimeUnit.MILLISECONDS
            )
            totalDays.text = days.toString()
            moneySaved.text = StringBuilder(moneyTotal.toString())
            cigsPerDay.text = StringBuilder(cigsTotal.toString())

        }

        iFailedButton.setOnClickListener {
            editor.putBoolean(APP_PREFERENCES_FIRSTTIME, true).apply()
            activity?.recreate()
        }
        return view
    }


}