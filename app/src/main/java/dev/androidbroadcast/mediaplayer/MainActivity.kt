package dev.androidbroadcast.mediaplayer

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.androidbroadcast.mediaplayer.databinding.ActivityMainBinding
import dev.androidbroadcast.mediaplayer.ui.media.AudioPlayerBottomSheet
import dev.androidbroadcast.mediaplayer.ui.media.VideoPlayerBottomSheet

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val audioPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val sheet = AudioPlayerBottomSheet.newInstance(it.toString())
            sheet.show(supportFragmentManager, AudioPlayerBottomSheet.TAG)
        }
    }

    private val videoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val sheet = VideoPlayerBottomSheet.newInstance(it.toString())
            sheet.show(supportFragmentManager, VideoPlayerBottomSheet.TAG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickAudio.setOnClickListener {
            audioPickerLauncher.launch("audio/*")
        }

        binding.btnPickVideo.setOnClickListener {
            videoPickerLauncher.launch("video/*")
        }
    }
}