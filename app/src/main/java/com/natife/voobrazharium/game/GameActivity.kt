package com.natife.voobrazharium.game

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.widget.*
import com.natife.voobrazharium.R
import com.natife.voobrazharium.game.UtilForDraw.PaintView
import com.natife.voobrazharium.init_game.Player
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.text.TextUtils.substring
import android.view.*
import android.widget.TextView
import android.widget.RelativeLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.natife.voobrazharium.utils.audio.AudioUtil
import com.natife.voobrazharium.utils.restoreColorDraw
import com.natife.voobrazharium.utils.restoreTimeMove
import com.natife.voobrazharium.utils.saveColorDraw
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*

class GameActivity : AppCompatActivity(), GameContract.View, ColorPickerDialogListener {
    private lateinit var mPresenter: GameContract.Presenter
    private lateinit var howExplain: String
    private lateinit var whoseTurn: TextView
    private lateinit var drawClear: ImageView
    private lateinit var timer: RelativeLayout
    private lateinit var circularProgressbar: ProgressBar
    private lateinit var textTimer: TextView
    private lateinit var layoutBtnFromTellAndShow: View
    private lateinit var theyGuessed: RelativeLayout
    private lateinit var theyNotGuessed: RelativeLayout
    private lateinit var remindWord: RelativeLayout
    private lateinit var layoutBtnPlayer: LinearLayout
    private lateinit var word: String
    private lateinit var paintView: PaintView
    private lateinit var buttonAction: RelativeLayout
    private lateinit var buttonPointBrush: RelativeLayout
    private var flagShowBtn: Boolean = false
    private lateinit var layoutForDraw: RelativeLayout
    private var positionPlayer: Int = 0
    private lateinit var playerList: MutableList<Player>
    private var timerBig: Boolean = false
    private var gd: GradientDrawable? = null
    private var timeMove: Int = 0
    private val DIALOG_ID_COLOR = 0
    private lateinit var colorDialog: ColorPickerDialog.Builder
    private var colorForStartDialog: Int = 0
    private lateinit var audio: AudioUtil
    private lateinit var mAdView: AdView
    private var flagSelectWhoGuessed: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //Создаём Presenter и в аргументе передаём ему this - эта Activity расширяет интерфейс GameContract.View
        mPresenter = GamePresenter(this)

        howExplain = intent.getStringExtra("how_explain")
        positionPlayer = intent.getIntExtra("positionPlayer", 0)
        word = intent.getStringExtra("word")
        timeMove = restoreTimeMove(this)//get info from preferences
        colorForStartDialog = restoreColorDraw(this)

        initView()

        audio = AudioUtil.getInstance()
        volumeControlStream = AudioManager.STREAM_MUSIC//volume on the volumeButton
        playerList = mPresenter.getPlayerList()

        starAdvertising()
    }

    private fun starAdvertising() {
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                if (layoutForDraw.visibility != View.VISIBLE) {
                    mAdView.visibility = View.VISIBLE
                }
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

    private fun showView(howExplain: String) {
        whoseTurn.setTextColor(ContextCompat.getColor(this, playerList[positionPlayer].color))
        val name = playerList[positionPlayer].name
        when (howExplain) {
            "tell" -> {
                whoseTurn.text = mPresenter.setLocaleRes(name, resources.getString(R.string.describes))
                selectedTellOrShow()
                playerList[positionPlayer].tell = false
            }
            "show" -> {
                whoseTurn.text = mPresenter.setLocaleRes(name, resources.getString(R.string.shows))
                selectedTellOrShow()
                playerList[positionPlayer].show = false
            }
            "draw" -> {
                whoseTurn.text = mPresenter.setLocaleRes(name, resources.getString(R.string.draws))
                selectedDraw()
                playerList[positionPlayer].draw = false
            }
        }
    }

    private fun selectedDraw() {
        flagShowBtn = false
        layoutForDraw.visibility = View.VISIBLE
        layoutBtnFromTellAndShow.visibility = View.GONE
        text_timer_draw.visibility = View.VISIBLE
        drawClear.visibility = View.VISIBLE
        back_image.visibility = View.VISIBLE
        timerBig = false
        mPresenter.initTimer(false, timeMove)

        paintView = (findViewById<View>(R.id.paintView) as PaintView?)!!
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        paintView.init(metrics)
        paintView.normal()
        //        paintView.emboss();//чеканка
        //        paintView.blur();//пятно
    }

    private fun selectedTellOrShow() {
        flagShowBtn = true
        timer.visibility = View.VISIBLE
        layoutBtnFromTellAndShow.visibility = View.VISIBLE
        timerBig = true
        mPresenter.initTimer(true, timeMove)
    }

    private fun initView() {
        back_image.setOnClickListener { _ ->
            audio.soundClickPlayer(this)
            paintView.backPaths()
        }
        back_image.isSoundEffectsEnabled = false
        whoseTurn = findViewById(R.id.whose_turn)
        drawClear = findViewById(R.id.draw_clear)
        drawClear.setOnClickListener { _ ->
            audio.soundClickPlayer(this)
            paintView.clear()
        }
        drawClear.isSoundEffectsEnabled = false
        timer = findViewById(R.id.timer)
        circularProgressbar = findViewById(R.id.circularProgressbar)
        circularProgressbar.max = timeMove
        textTimer = findViewById(R.id.text_timer)
        layoutBtnFromTellAndShow = findViewById(R.id.layout_btn_from_tell_and_show)
        theyGuessed = findViewById(R.id.they_guessed)
        theyNotGuessed = findViewById(R.id.they_not_guessed)
        remindWord = findViewById(R.id.remind_word)
        layoutBtnPlayer = findViewById(R.id.layout_btn_player)
        buttonAction = findViewById(R.id.buttonAction)
        layoutForDraw = findViewById(R.id.layout_for_draw)
        buttonPointBrush = findViewById(R.id.buttonPointBrush)
        buttonPointBrush.isSoundEffectsEnabled = false
        buttonPointBrush.setOnClickListener { _ ->
            audio.soundClickPlayer(this)
            colorDialog = ColorPickerDialog.newBuilder()
            colorDialog.setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowPresets(false)
                    .setDialogId(DIALOG_ID_COLOR)
                    .setColor(colorForStartDialog)
                    .setDialogTitle(R.string.select_color)
                    .setSelectedButtonText(R.string.select)
                    .setShowAlphaSlider(false)
                    .setPresetsButtonText(R.string.presets)
                    .setCustomButtonText(R.string.custom)
                    .setShowAlphaSlider(false)
                    .show(this)
        }
        buttonAction.isSoundEffectsEnabled = false
        buttonAction.setOnClickListener { _ ->
            audio.soundClickPlayer(this)
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.btn_block)
            val theyGuessed = dialog.findViewById<RelativeLayout>(R.id.they_guessed)
            val theyNotGuessed = dialog.findViewById<RelativeLayout>(R.id.they_not_guessed)
            val remindWord = dialog.findViewById<RelativeLayout>(R.id.remind_word)
            theyGuessed.isSoundEffectsEnabled = false
            theyGuessed.setOnClickListener { _ ->
                dialog.dismiss()
                btnTheyGuessed()
            }
            theyNotGuessed.isSoundEffectsEnabled = false
            theyNotGuessed.setOnClickListener { _ ->
                dialog.dismiss()
                btnTheyNotGuessed()
            }
            remindWord.isSoundEffectsEnabled = false
            remindWord.setOnClickListener { _ ->
                dialog.dismiss()
                btnRemindWord()
            }
            dialog.show()
        }
        remindWord.setOnClickListener { _ -> btnRemindWord() }
        remindWord.isSoundEffectsEnabled = false
        theyGuessed.setOnClickListener { btnTheyGuessed() }
        theyGuessed.isSoundEffectsEnabled = false
        theyNotGuessed.setOnClickListener { btnTheyNotGuessed() }
        theyNotGuessed.isSoundEffectsEnabled = false
    }


    private fun btnRemindWord() {
        audio.soundClickPlayer(this)
        val toast = Toast.makeText(this, word, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    override fun dialogTimeMoveGone(flag: Boolean) {
        if (flag) {
            btnTheyGuessed()
        } else {
            btnTheyNotGuessed()
        }
    }

    private fun btnTheyNotGuessed() {
        audio.soundClickPlayer(this)
        mPresenter.stopCountDownTimer()
        mPresenter.notWin()
    }


    private fun btnTheyGuessed() {
        mPresenter.stopCountDownTimer()
        whoseTurn.text = resources.getString(R.string.who_guessed)
        whoseTurn.setTextColor(ContextCompat.getColor(this, R.color.colorTextSelection))

        if (flagShowBtn) {
            timer.visibility = View.GONE
        } else {
            text_timer_draw.visibility = View.GONE
            drawClear.visibility = View.GONE
            back_image.visibility = View.GONE
            layoutForDraw.visibility = View.GONE
        }
        layoutBtnFromTellAndShow.visibility = View.GONE
        layoutBtnPlayer.visibility = View.VISIBLE

        if (!flagSelectWhoGuessed) {
            audio.soundClickPlayer(this)
            for (i in playerList.indices) {
                if (positionPlayer != i) {
                    val newItem = LayoutInflater.from(this).inflate(R.layout.item_player_button, null)//добавляемый item
                    val btn = newItem.findViewById<RelativeLayout>(R.id.btnPlayer)
                    val textBtnPlayer = newItem.findViewById<TextView>(R.id.textBtnPlayer)
                    val name = playerList[i].name!!.substring(0, 1).toUpperCase() + playerList[i].name!!.substring(1)
                    textBtnPlayer.text = name
                    gd = btn.background as GradientDrawable
                    gd!!.setColor(ContextCompat.getColor(this, playerList[i].color))
                    btn.isSoundEffectsEnabled = false
                    btn.setOnClickListener { _ ->
                        //                    audio.soundClick(this)
                        audio.soundClickPlayer(this)
                        mPresenter.playerWin(playerList, i, positionPlayer)
                    }
                    layoutBtnPlayer.addView(newItem)
                }
            }
        }

        flagSelectWhoGuessed = true
    }

    override fun contextActivity(): Context {
        return this
    }


    override fun finishCurrentGame() {
        mPresenter.gameActivityDestroyed()
        this.finish()
    }


    override fun setCircularProgressbar(progress: Int) {
        circularProgressbar.progress = progress
    }


    override fun setTextTimer(time: String) {
        if (timerBig) {
            textTimer.text = time
        } else
            text_timer_draw.text = time
    }

    override fun onResume() {
        super.onResume()
        if (flagSelectWhoGuessed) {
            btnTheyGuessed()
        } else {
            showView(howExplain)
        }
    }

    override fun onPause() {
        super.onPause()
        mPresenter.stopCountDownTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (gd != null) {
            gd!!.setColor(ContextCompat.getColor(this, R.color.colorButton))
        }
    }


    override fun onColorSelected(dialogId: Int, selectedColor: Int) {
        when (dialogId) {
            DIALOG_ID_COLOR -> {
                paintView.setColorPaint(selectedColor)
                colorDialog.setColor(selectedColor)
                colorForStartDialog = selectedColor
                saveColorDraw(this, selectedColor)
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {
        //auto generate
    }


    override fun onBackPressed() {}
}

