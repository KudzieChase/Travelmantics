package com.kudziechase.travelmantics.adapters

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.kudziechase.travelmantics.model.TravelDeal
import android.widget.TextView
import com.kudziechase.travelmantics.R
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.kudziechase.travelmantics.DealActivity
import com.kudziechase.travelmantics.utils.FirebaseHelper
import android.view.LayoutInflater
import com.bumptech.glide.request.RequestOptions

class TravelDealsAdapter : RecyclerView.Adapter<TravelDealsAdapter.ItemHolder>() {

    var deals: ArrayList<TravelDeal> = FirebaseHelper.mTravelDeals
    var mDatabaseReference: DatabaseReference = FirebaseHelper.mDatabaseRef
    var mChildListener: ChildEventListener

    init {
        mChildListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val td = dataSnapshot.getValue(TravelDeal::class.java)
                Log.d("Deal: ", td!!.title)
                td.id = dataSnapshot.key!!
                deals.add(td)
                notifyItemInserted(deals.size - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }

        mDatabaseReference.addChildEventListener(mChildListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_row_card, parent, false)
        return ItemHolder(itemView)
    }

    override fun getItemCount(): Int {
        return deals.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val deal = deals[position]
        holder.bind(deal)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        var tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        var imageDeal: ImageView = itemView.findViewById(R.id.imageDeal)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(deal: TravelDeal) {
            tvTitle.text = deal.title
            tvDescription.text = deal.description
            tvPrice.text = deal.price
            showImage(deal.imageUrl)
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            Log.d("Click", position.toString())
            val selectedDeal = deals[position]
            val intent = Intent(view!!.context, DealActivity::class.java)
            intent.putExtra("Deal", selectedDeal)
            view.context.startActivity(intent)
        }

        fun showImage(imageUrl: String) {
            Glide.with(imageDeal.context)
                .load(imageUrl)
                .apply(RequestOptions().override(160, 160))
                .centerCrop()
                .into(imageDeal)
        }
    }
}