package com.natife.voobrazharium.init_game

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout

import com.natife.voobrazharium.R

import java.util.ArrayList

class PlayersAdapter(private val context: Context, private val voiceIconListener: OnItemVoiceIconListener) : RecyclerView.Adapter<PlayersAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var playerList: MutableList<Player> = ArrayList()


    override fun getItemCount(): Int {
        return playerList.size
    }//getItemCount

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }//getItemId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_player, parent, false)
        val holder = ViewHolder(view)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                playerList[holder.adapterPosition].name = charSequence.toString()
                Log.d("ddd", "onTextChanged list = $playerList")
            }

            override fun afterTextChanged(editable: Editable) {}
        }

        holder.editTextPlayerName.addTextChangedListener(textWatcher)

        return holder
    } // onCreateViewHolder

    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private var constraintItemPlayer: ConstraintLayout
        internal var imageColor: ImageView
        internal var editTextPlayerName: EditText
        internal var imageVoice: RelativeLayout

        init {
            constraintItemPlayer = view.findViewById(R.id.item_player_constraint)
            imageColor = view.findViewById(R.id.imageColor)
            imageVoice = view.findViewById(R.id.imageVoice)
            editTextPlayerName = view.findViewById(R.id.editTextPlayerName)
        }//ViewHolder
    }//class ViewHolder


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("ddd", "onBindViewHolder list = $playerList")

        holder.editTextPlayerName.hint = context.resources.getString(R.string.name_player) + " " + (position + 1)
        holder.editTextPlayerName.setText(playerList[position].name)

        holder.imageColor.setColorFilter(ContextCompat.getColor(context, playerList.get(position).color))

        val listener = View.OnClickListener {
            voiceIconListener.onItemVoiceIconClick(holder.adapterPosition, holder.editTextPlayerName)
        }
        holder.imageVoice.isSoundEffectsEnabled= false
        holder.imageVoice.setOnClickListener(listener)
    }//onBindViewHolder

    override fun onViewRecycled(holder: ViewHolder) {

    }

    fun setData(playerList: MutableList<Player>) {
        Log.d("ddd", "setData this.list = " + this.playerList)
        this.playerList = playerList
        notifyDataSetChanged()
    }


    fun deleteFromListAdapter(position: Int) {
        playerList.removeAt(position)

        Log.d("ddd", "deleteFromListAdapter list = $playerList")
        notifyItemRemoved(position)//updates after removing Item at position
        notifyDataSetChanged()
    }//deleteFromListAdapter

}//class AdapterProductList