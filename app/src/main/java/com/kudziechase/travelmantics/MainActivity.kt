package com.kudziechase.travelmantics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.kudziechase.travelmantics.utils.FirebaseHelper
import android.content.Intent
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kudziechase.travelmantics.adapters.TravelDealsAdapter


class MainActivity : AppCompatActivity(), InvalidateListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.insert_menu -> {
                val intent = Intent(this, DealActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.logout_menu -> {
                FirebaseHelper.signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onPause() {
        super.onPause()
        FirebaseHelper.detachListener()
    }

    override fun onResume() {
        super.onResume()
        FirebaseHelper.initFBReference("travel_deals",this, this)
        val rvDeals: RecyclerView = findViewById(R.id.rvDeals)
        val adapter = TravelDealsAdapter()
        rvDeals.adapter = adapter
        val dealsLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvDeals.layoutManager = dealsLayoutManager
        FirebaseHelper.attachListener()
    }


    override fun invalidateOptions() {
        invalidateOptionsMenu()
    }
}
