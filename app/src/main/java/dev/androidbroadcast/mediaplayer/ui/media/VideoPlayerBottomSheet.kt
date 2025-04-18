package dev.androidbroadcast.mediaplayer.ui.media

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global.putString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.androidbroadcast.mediaplayer.R
import dev.androidbroadcast.mediaplayer.databinding.BottomSheetVideoBinding

class VideoPlayerBottomSheet : BottomSheetDialogFragment() {

    private var videoUri: Uri? = null
    private var player: ExoPlayer? = null
    private lateinit var binding: BottomSheetVideoBinding

    private var playbackPosition: Long = 0
    private var isPlaying: Boolean = true

    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            player?.let {
                val position = it.currentPosition
                val duration = it.duration
                binding.seekBar.max = duration.toInt()
                binding.seekBar.progress = position.toInt()
                binding.tvCurrent.text = "${formatTime(position)} / ${formatTime(duration)}"
                updateHandler.postDelayed(this, 500)
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            binding.btnPlayPause.text =
                if (isPlaying) getString(R.string.pause) else getString(R.string.play)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoUri = arguments?.getString(ARG_URI)?.toUri()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            playbackPosition = it.getLong("playback_position", 0)
            isPlaying = it.getBoolean("is_playing", true)
        }

        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.videoView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(videoUri!!)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.seekTo(playbackPosition)
            if (isPlaying) exoPlayer.play()

            exoPlayer.addListener(playerListener)

            binding.btnPlayPause.setOnClickListener {
                exoPlayer.playPause()
            }

            updateHandler.post(updateRunnable)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.tvCurrent.text = formatTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                updateHandler.removeCallbacks(updateRunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    player?.seekTo(it.progress.toLong())
                }
                updateHandler.post(updateRunnable)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playback_position", player?.currentPosition ?: 0)
        outState.putBoolean("is_playing", player?.isPlaying ?: true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateHandler.removeCallbacks(updateRunnable)
        player?.removeListener(playerListener)
        player?.release()
        player = null
    }

    private fun formatTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        val hours = ms / (1000 * 60 * 60)
        return if (hours > 0) "%02d:%02d:%02d".format(hours, minutes, seconds)
        else "%02d:%02d".format(minutes, seconds)
    }

    companion object {
        const val TAG = "VideoPlayerSheet"
        private const val ARG_URI = "video_uri"

        fun newInstance(uri: String): VideoPlayerBottomSheet {
            val args = Bundle().apply {
                putString(ARG_URI, uri)
            }
            return VideoPlayerBottomSheet().apply {
                arguments = args
            }
        }
    }

    private fun ExoPlayer.playPause() = if (isPlaying) pause() else play()
}
