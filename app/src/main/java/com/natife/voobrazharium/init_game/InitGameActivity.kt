package com.natife.voobrazharium.init_game

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Parcelable
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.natife.voobrazharium.R
import com.natife.voobrazharium.choose_how_play.ChooseHowPlayActivity
import com.natife.voobrazharium.utils.audio.AudioUtil
import kotlinx.android.synthetic.main.activity_initgame.*
import kotlinx.android.synthetic.main.select_difficulty_level.*
import java.util.*

class InitGameActivity : AppCompatActivity(), InitGameContract.View {

    private lateinit var adapterPlayers: PlayersAdapter
    private lateinit var mPresenter: InitGameContract.Presenter
    private lateinit var onItemVoiceIconListener: OnItemVoiceIconListener
    private lateinit var nameForVoiceTemp: EditText
    private val VOICE_RECOGNIZER: Int = 1000
    private val LEVEL_EASE = 1
    private val LEVEL_NORMAL = 2
    private val LEVEL_HARD = 3
    private var audio: AudioUtil? = null
    private lateinit var audioManager: AudioManager
    private lateinit var mAdView : AdView

    companion object {

        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, InitGameActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initgame)

        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //Создаём Presenter
        mPresenter = InitGamePresenter()
        mPresenter.viewAttach(this)

        initView()

        recyclerViewListPlayer.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapterPlayers = PlayersAdapter(this@InitGameActivity, onItemVoiceIconListener)
        recyclerViewListPlayer.adapter = adapterPlayers

        val callback = object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val swipedPosition = viewHolder.adapterPosition

                if (adapterPlayers.itemCount > 3) {
                    adapterPlayers.deleteFromListAdapter(swipedPosition)
                }
            }

            override fun getSwipeDirs(recyclerView: RecyclerView, holder: RecyclerView.ViewHolder): Int {
                return if (adapterPlayers.itemCount > 3) super.getSwipeDirs(recyclerView, holder) else 0
            }
        }
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerViewListPlayer)

        val playerList = intent.getParcelableArrayListExtra<Player>("playerList")
        (mPresenter as InitGamePresenter).initPlayerList(playerList)

        volumeControlStream = AudioManager.STREAM_MUSIC//volume on the volumeButton

        starAdvertising()
    }

    private fun starAdvertising() {
        MobileAds.initialize(this, resources.getString(R.string.mobileAds))
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                mAdView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                mAdView.visibility = View.INVISIBLE
            }

            override fun onAdOpened() {
            }

            override fun onAdLeftApplication() {
            }

            override fun onAdClosed() {
            }
        }
    }


    private fun initView() {
        radio_easy.setOnClickListener{
            audio!!.soundClickPlayer(this)
        }
        radio_easy.isSoundEffectsEnabled= false
        radio_normal.setOnClickListener{
            audio!!.soundClickPlayer(this)
        }
        radio_normal.isSoundEffectsEnabled= false
        radio_hard.setOnClickListener{
            audio!!.soundClickPlayer(this)
        }
        radio_hard.isSoundEffectsEnabled= false
        buttonAddPlayer.setOnClickListener {
            audio!!.soundClickPlayer(this)
            mPresenter.btnAddPlayerClicked() }
        buttonAddPlayer.isSoundEffectsEnabled= false
        buttonNext.setOnClickListener {
            audio!!.soundClickPlayer(this)
            mPresenter.btnNextClicked(checkDifficultLevel()) }
        buttonNext.isSoundEffectsEnabled= false
        back.setOnClickListener {
            audio!!.soundClickPlayer(this)
            mPresenter.btnBackClicked() }
        back.isSoundEffectsEnabled= false
        settings.setOnClickListener {
            audio!!.soundClickPlayer(this)
            mPresenter.btnSettingsClicked() }
        settings.isSoundEffectsEnabled= false
        onItemVoiceIconListener = object : OnItemVoiceIconListener {
            override fun onItemVoiceIconClick(position: Int, editText: EditText) {
                // call the voice dialing activity
                nameForVoiceTemp = editText
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
                try {
                    startActivityForResult(intent, VOICE_RECOGNIZER)
                } catch (a: ActivityNotFoundException) {
                    Toast.makeText(this@InitGameActivity,
                            R.string.error_voice_not_support, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }//initView

    private fun checkDifficultLevel(): Int {
        var levelDifficult = LEVEL_EASE
        if (radio_easy.isChecked) levelDifficult = LEVEL_EASE
        if (radio_normal.isChecked) levelDifficult = LEVEL_NORMAL
        if (radio_hard.isChecked) levelDifficult = LEVEL_HARD
        return levelDifficult
    }

    override fun showListPlayers(listPlayer: MutableList<Player>) {
        adapterPlayers.setData(listPlayer)
    }


    override fun changeScreen(flagChange: Boolean) {
        recyclerViewListPlayer.visibility = if (flagChange) View.GONE else View.VISIBLE
        viewRadioButton.visibility = if (flagChange) View.VISIBLE else View.GONE
        back.visibility = if (flagChange) View.VISIBLE else View.GONE
        settings.setImageResource(if (flagChange) R.drawable.ic_settings_black_24dp else R.drawable.ic_error_outline_black_24dp)
        buttonAddPlayer.visibility = if (flagChange) View.GONE else View.VISIBLE
        textBtnNext.setText(if (flagChange) R.string.text_play else R.string.text_next)
        textSelection.setText(if (flagChange) R.string.text_selection_difficulty_level else R.string.text_selection_name)
    }

    override fun contextActivity(): Context {
        return this
    }

    override fun showSettingsDialog(flagStartGame: Boolean) {
        if (flagStartGame) {
            val dialogSettings = DialogSettings()
            dialogSettings.show(supportFragmentManager, "dialogSettings")
        } else {
            val dialog = Dialog(this)
            dialog.window!!.setLayout(1000, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.setContentView(R.layout.dialog_inform)
            val rules: TextView = dialog.findViewById<TextView>(R.id.textRules)
            rules.setOnClickListener{
                val dialogRules = DialogRules()
                dialogRules.show(supportFragmentManager, "dialogRules")
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun showAlert(message: Int) {
        android.support.v7.app.AlertDialog.Builder(this, R.style.ColorDialogTheme)
                .setMessage(message)
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    override fun createWordList(mRepository: InitGameContract.Repository, difficultLevel: Int): MutableList<String> {
        return mRepository.createListWords(difficultLevel, this)
    }


    override fun startGame(playerList: MutableList<Player>, difficultLevel: Int) {
        val intent = Intent(this, ChooseHowPlayActivity::class.java)
        intent.putExtra("difficultLevel", difficultLevel)
        intent.putParcelableArrayListExtra("playerList", playerList as ArrayList<out Parcelable>)
        startActivity(intent)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            VOICE_RECOGNIZER -> {
                // result of voice dialing
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val yourResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)[0]
                    nameForVoiceTemp.setText(yourResult)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        audio = AudioUtil.getInstance()
    }

    override fun onStop() {
        super.onStop()
        audio = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.viewDetach()
    }

    override fun onBackPressed() {
        if (mPresenter.getFlagChangeScreen()) {
            return
        }else
        super.onBackPressed()
    }
}