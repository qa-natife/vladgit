package com.natife.voobrazharium.resultgame

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View.inflate
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.natife.voobrazharium.R
import com.natife.voobrazharium.init_game.InitGameActivity
import com.natife.voobrazharium.init_game.Player
import java.util.ArrayList
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.natife.voobrazharium.utils.audio.AudioUtil
import com.natife.voobrazharium.utils.restorePlayerList
import com.natife.voobrazharium.utils.savePlayerList
import kotlinx.android.synthetic.main.activity_result.view.*
import java.io.File
import java.io.FileOutputStream

class ResultGameActivity : AppCompatActivity() {
    private lateinit var mPresenter: ResultContract.Presenter
    private lateinit var playerList: List<Player>
    private lateinit var localPayerList: MutableList<Player>
    private lateinit var toolbar: Toolbar
    private var timeGameFlag: Boolean = true
    private lateinit var layoutResult: LinearLayout
    private var dialog: Dialog? = null
    private lateinit var audio: AudioUtil
    private lateinit var mAdView: AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Создаём Presenter и в аргументе передаём ему this - эта Activity расширяет интерфейс GameContract.View
        mPresenter = ResultPresenter()

        val viewResult = layoutInflater.inflate(R.layout.activity_result, null)
        layoutResult = viewResult.findViewById(R.id.layoutResult)//контейнер для вставки item
        toolbar = viewResult.findViewById(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        audio = AudioUtil.getInstance()
        volumeControlStream = AudioManager.STREAM_MUSIC//volume on the volumeButton

        timeGameFlag = intent.getBooleanExtra("timeGameFlag", false)

        playerList = mPresenter.getPlayerList()
        if (playerList.isEmpty()){
            playerList = restorePlayerList(this)
            mPresenter.restorePlayerListOnRepository(playerList)
        }else{
            savePlayerList(playerList, this)
        }

        localPayerList = ArrayList(playerList)

        val btnBack: ImageView = viewResult.findViewById(R.id.back)
        btnBack.isSoundEffectsEnabled = false
        btnBack.setOnClickListener {
            audio.soundClickPlayer(this)
            setResult(RESULT_OK, intent.putExtra("flagNextPlayer", true))
            this.finish()
        }
        btnBack.visibility = if (timeGameFlag) View.VISIBLE else View.INVISIBLE

        val buttonAgain: RelativeLayout = viewResult.findViewById(R.id.buttonAgain)
        buttonAgain.isSoundEffectsEnabled = false
        buttonAgain.setOnClickListener {
            audio.soundClickPlayer(this)
            android.support.v7.app.AlertDialog.Builder(this, R.style.ColorDialogTheme)
                    .setMessage(R.string.you_are_sure)
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        audio.soundClickPlayer(this)
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        audio.soundClickPlayer(this)
                        dialog.dismiss()
                        finishAffinity()
                        val intent = Intent(this, InitGameActivity::class.java)
                        intent.putParcelableArrayListExtra("playerList", playerList as ArrayList<out Parcelable>)
                        startActivity(intent)
                    }
                    .show()
        }

        localPayerList = mPresenter.sortPlayerList()

        createResult()
        setContentView(viewResult)

        starAdvertising()
    }


    private fun createResult() {
        val isWin = mPresenter.checkWin()
        for (i in localPayerList.indices) {
            val newItem = inflate(this, R.layout.item_result, null)//добавляемый item
            val image = newItem.findViewById<ImageView>(R.id.image_result)
            val nameResult = newItem.findViewById<TextView>(R.id.name_result)//inserted name
            val totalPointsResult = newItem.findViewById<TextView>(R.id.total_points)
            val guessedWordsResult = newItem.findViewById<TextView>(R.id.guessed_words)

            val name = localPayerList[i].name!!.substring(0, 1).toUpperCase() + localPayerList[i].name!!.substring(1)
            nameResult.text = name
            val guessedWords = String.format("%s %s %s",
                    resources.getString(R.string.guessed),
                    localPayerList[i].countWords,
                    resources.getString(R.string.words))
            guessedWordsResult.text = guessedWords
            totalPointsResult.text = localPayerList[i].countScore.toString()
            if (isWin && i == 0) {
                image.visibility = View.VISIBLE
                if (!timeGameFlag) {
                    audio.soundApplausePlayer(this)
                    dialog = Dialog(this)
                    dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog!!.setContentView(R.layout.dialog_win)
                    val nameWiner: TextView = dialog!!.findViewById(R.id.name_win)
                    nameWiner.text = name
                    dialog!!.show()
                }
            } else
                image.visibility = View.INVISIBLE
            layoutResult.addView(newItem)

            if (!isWin && !timeGameFlag && i == 0) {
                dialog = Dialog(this)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog!!.setContentView(R.layout.dialog_draw)
                dialog!!.show()
            }
        }
    }

    private fun starAdvertising() {
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

    override fun onBackPressed() {
        if (timeGameFlag) {
            super.onBackPressed()
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                audio.soundClickPlayer(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    layoutResult.background = resources.getDrawable(R.mipmap.gradient,theme)
                }else{
                    layoutResult.background = resources.getDrawable(R.mipmap.gradient)
                }

                val bitmap = mPresenter.getBitmapFromView(layoutResult)
                layoutResult.background = null
                System.gc()
                val builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                startShare(bitmap)
            }
        }
        return true
    }

    private fun startShare(bitmap: Bitmap) {
        try {
            val file = File(this.externalCacheDir, "rezult_game.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)

            val intent = Intent(android.content.Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_text))
            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_URI))
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            intent.type = "image/png"
            startActivity(Intent.createChooser(intent, resources.getString(R.string.share_via)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_result, menu)
        return true
    }
}
