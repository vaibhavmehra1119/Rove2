package com.example.roveapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class BarChart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)

        val barChart=findViewById<BarChart>(R.id.barChart)
        var entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(1f, 46f))
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



        var barDataSet : BarDataSet = BarDataSet(entries, "Crime Rate")
        var barData : BarData = BarData(barDataSet)
        barData.setValueFormatter(LargeValueFormatter())

        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 10f


        //barChart.setFitBars(true)
        barChart.data = barData
        barChart.barData.barWidth=1f
        val xVal :ArrayList<String> = ArrayList()
        xVal.add("Jan")
        xVal.add("Feb")
        xVal.add("Mar")
        xVal.add("Apr")
        xVal.add("May")
        xVal.add("Jun")
        xVal.add("Jul")
        xVal.add("Aug")
        xVal.add("Sep")
        xVal.add("Oct")
        xVal.add("Nov")
        xVal.add("Dec")

        var xAxis :XAxis=barChart.xAxis
        xAxis.position=XAxis.XAxisPosition.BOTTOM
        var indexAxisValue :IndexAxisValueFormatter= IndexAxisValueFormatter(xVal)
        xAxis.valueFormatter= indexAxisValue
        xAxis.setDrawGridLines(false)
        xAxis.setCenterAxisLabels(true)
        barChart.description.text = "Bar Chart Example"
        barChart.animateY(2000)

    }
}