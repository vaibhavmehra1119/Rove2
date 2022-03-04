package com.example.roveapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

class BarChart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)

        val barChart=findViewById<BarChart>(R.id.barChart)
        var entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(1f,46f))
        entries.add(BarEntry(2f, 48f))
        entries.add(BarEntry(3f, 44f))
        entries.add(BarEntry(4f, 364f))
        entries.add(BarEntry(5f, 300f))
        entries.add(BarEntry(6f, 234f))
        entries.add(BarEntry(7f, 51f))
        entries.add(BarEntry(8f, 51f))
        entries.add(BarEntry(9f, 48f))
        entries.add(BarEntry(10f, 41f))
        entries.add(BarEntry(11f, 45f))
        entries.add(BarEntry(12f, 47f))


        var barDataSet : BarDataSet = BarDataSet(entries, "Visitors")
        var barData : BarData = BarData(barDataSet)
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 8f

        barChart.setFitBars(true)
        barChart.data = barData
        barChart.description.text = "Bar Chart Example"
        barChart.animateY(2000)
    }
}