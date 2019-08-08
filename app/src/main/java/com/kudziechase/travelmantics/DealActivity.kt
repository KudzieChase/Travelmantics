package com.kudziechase.travelmantics

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_deal.*
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kudziechase.travelmantics.model.TravelDeal
import com.kudziechase.travelmantics.utils.FirebaseHelper

class DealActivity : AppCompatActivity() {

    val mDatabaseReference = FirebaseHelper.mDatabaseRef

    private val RESULT_PICTURE = 20
    private var deal: TravelDeal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)

        deal = intent.getSerializableExtra("Deal") as TravelDeal?

        if (deal == null) {
            deal = TravelDeal()
        }

        deal?.let {
            txtTitle.setText(it.title)
            txtDescription.setText(it.description)
            txtPrice.setText(it.price)
            showImage(it.imageUrl)
        }

        btnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Upload Picture"), RESULT_PICTURE)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                saveDeal()
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show()
                clean()
                backToMain()
                return true
            }
            R.id.delete_menu -> {
                deleteDeal()
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show()
                backToMain()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        menu!!.findItem(R.id.delete_menu).isVisible
        menu.findItem(R.id.save_menu).isVisible
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_PICTURE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val storageRef = imageUri!!.lastPathSegment?.let {
                FirebaseHelper.mStorageRef.child(it)
            }
            storageRef?.putFile(imageUri)!!.addOnSuccessListener(this) {
                val pictureName = it.storage.path
                val urlTask = it.storage.downloadUrl
                while (!urlTask.isSuccessful);
                val downloadUrl = urlTask.result
                val url = downloadUrl.toString()
                deal?.imageName = pictureName
                deal?.imageUrl = url
                showImage(url)
            }.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                Toast.makeText(this, "Upload is $progress% done", Toast.LENGTH_SHORT).show()
            }.addOnPausedListener {
                Toast.makeText(this, "Upload is paused", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveDeal() {
        deal?.title = txtTitle.text.toString()
        deal?.description = txtDescription.text.toString()
        deal?.price = txtPrice.text.toString()

        if (deal?.id.isNullOrEmpty()) {
            //Create
            mDatabaseReference.push().setValue(deal)
        } else {
            //Edit
            mDatabaseReference.child(deal!!.id).setValue(deal)
        }
    }

    private fun deleteDeal() {
        if (deal?.id!!.isEmpty()) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show()
            return
        }
        mDatabaseReference.child(deal!!.id).removeValue()
        if (deal?.imageName!!.isNotEmpty()) {
            val pictureReference = FirebaseHelper.mStorage.reference.child(deal!!.imageName)
            pictureReference.delete().addOnSuccessListener {
                Log.d("DELETE", "picture successfully deleted")
            }.addOnFailureListener {
                Log.d("DELETE", "picture not deleted")
                Log.d("DELETE", it.message!!)
            }
        }
    }

    private fun backToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun clean() {
        txtPrice.setText("")
        txtDescription.setText("")
        txtTitle.setText("")
        txtTitle.requestFocus()
    }

    private fun enableEditTexts(isEnabled: Boolean) {
        txtTitle.isEnabled = isEnabled
        txtDescription.isEnabled = isEnabled
        txtPrice.isEnabled = isEnabled
    }

    private fun showImage(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            val width = Resources.getSystem().displayMetrics.widthPixels;

            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .apply(RequestOptions().override(width, width * 2 / 3))
                .into(image)
        }

    }


}