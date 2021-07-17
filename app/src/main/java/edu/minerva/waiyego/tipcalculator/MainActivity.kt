package edu.minerva.waiyego.tipcalculator

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

//create variables to be used universally
private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //create the initial settings of the app when it launches
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentage.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        updateEmojiReactions(INITIAL_TIP_PERCENT)

        //set on seekbar change listener class
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //document the change in the value of progress in the logcat as scrubbing occurs
                Log.i(TAG, "onProgressChange $progress")
                //change the value of the tip percentage as scrubbing occurs
                tvTipPercentage.text= "$progress%"
                //call the computing and updating functions
                computeTipandTotal()
                updateTipDescription(progress)
                updateEmojiReactions(progress)
            }
            //these functions are not used but must be called as they are part of the class
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        //
        etBase.addTextChangedListener(object: TextWatcher{
            //first 2 functions are not used but must still be called
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            //function that allows a null input value in the edit text
            override fun afterTextChanged(s: Editable?) {
                //show change in edittext on the logcat
                Log.i(TAG, "afterTextChanged $s")
                //call function that computes tip and total based on the bill amount
                computeTipandTotal()
            }
        })
        etNumberofPeople.addTextChangedListener(object: TextWatcher{
           //first 2 functions are not used but still must be called
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            //function that allows the edit text to stay empty
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                //call function that computes total per person based on the input in the edit text
                computeTipandTotal()
            }
        })
    }

    private fun updateEmojiReactions(tipPercent: Int) {
        //Change the emoji shown as the tip percentage changes
        val emoji = ivEmojiReaction
        when(tipPercent){
            //different emoji pics used as scrubbing occurs
            in 0..9->emoji.setImageResource(R.drawable.new_bad_emoji_pic)
            in 10..14->emoji.setImageResource(R.drawable.new_acceptable_emoji_pic)
            in 15..19-> emoji.setImageResource(R.drawable.new_good_emoji_pic)
            in 20..24->emoji.setImageResource(R.drawable.new_great_emoji_pic)
            else ->emoji.setImageResource(R.drawable.new_amazing_emoji_pic)
        }
    }

    private fun updateTipDescription(tipPercent: Int) {
        //Give a description of the tip as the value changes on the seek bar
        val tipDescription: String = when (tipPercent){
            in 0..9-> "Bad"
            in 10..14-> "Acceptable"
            in 15..19-> "Good"
            in 20..24-> "Great"
            else-> "Amazing"
        }
        tvTipDescription.text = tipDescription
        //change the color of the descriptive text on a scale from red to green as the value of the seekbar changes
        val color= ArgbEvaluator().evaluate(tipPercent.toFloat()/seekBarTip.max,
            ContextCompat.getColor(this, R.color.worst_tip),
            ContextCompat.getColor(this, R.color.best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)

    }

    private fun computeTipandTotal() {
        //Check if value of base is empty and do an early return to avoid crashing
        if (etBase.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvTotalperPersonAmount.text = ""
            return }

        //Get the value of the base, tip percent
        val baseAmount = etBase.text.toString().toDouble()
        val tipPercent = seekBarTip.progress

        //Compute Tip amount
        val tipAmount = baseAmount * tipPercent /100
        //Compute Total Amount
        val totalAmount = baseAmount + tipAmount

        //Assign the vales to their text views on the UI and format to show two decimal places only
        "$%.2f".format(tipAmount).also { tvTipAmount.text = it }
        "$%.2f".format(totalAmount).also { tvTotalAmount.text = it }

        //compute per person total
        if(etNumberofPeople.text.isEmpty()) {
            tvTotalperPersonAmount.text = ""
            return }
        //get the value of the number of people
        val noOfPeople = etNumberofPeople.text.toString().toDouble()
        //Compute Total per person
        val totalPerPerson =totalAmount.div(noOfPeople)
        //Assign the value to the text view and show 2 decimal places
        "$%.2f".format(totalPerPerson).also { tvTotalperPersonAmount.text = it }






    }
}