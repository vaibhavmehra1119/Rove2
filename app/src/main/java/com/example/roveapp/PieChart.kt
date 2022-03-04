package com.example.roveapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.*

class PieChart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_chart)

        val pieChart=findViewById<PieChart>(R.id.pieChart)
        var entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(31f, "L1"))
        entries.add(PieEntry(92f, "L2"))
        entries.add(PieEntry(1107f, "L3"))


        val pieDataSet = PieDataSet(entries, "Level Distribution")
        pieDataSet?.setColors(*ColorTemplate.COLORFUL_COLORS)
        val pieData = PieData(pieDataSet)
        pieChart.data=pieData
        pieChart.description.isEnabled=true

        pieChart.animate()


    }
}