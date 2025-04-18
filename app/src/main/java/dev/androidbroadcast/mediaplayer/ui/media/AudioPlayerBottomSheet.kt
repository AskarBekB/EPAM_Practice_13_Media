package dev.androidbroadcast.mediaplayer.ui.media

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.androidbroadcast.mediaplayer.R
import dev.androidbroadcast.mediaplayer.databinding.BottomSheetAudioBinding

class AudioPlayerBottomSheet : BottomSheetDialogFragment() {

    private var audioUri: Uri? = null
    private var player: ExoPlayer? = null
    private lateinit var binding: BottomSheetAudioBinding

    private var playbackPosition: Long = 0
    private var isPlaying: Boolean = true

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            binding.btnPlayPause.text =
                if (isPlaying) getString(R.string.pause) else getString(R.string.play)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioUri = arguments?.getString(ARG_URI)?.toUri()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            playbackPosition = it.getLong("playback_position", 0)
            isPlaying = it.getBoolean("is_playing", true)
        }

        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            val mediaItem = MediaItem.fromUri(audioUri!!)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.seekTo(playbackPosition)
            if (isPlaying) exoPlayer.play()

            exoPlayer.addListener(playerListener)

            binding.btnPlayPause.setOnClickListener {
                exoPlayer.playPause()
            }
        }

        binding.tvPath.text = audioUri.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playback_position", player?.currentPosition ?: 0)
        outState.putBoolean("is_playing", player?.isPlaying ?: true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.removeListener(playerListener)
        player?.release()
        player = null
    }

    private fun ExoPlayer.playPause() = if (isPlaying) pause() else play()

    companion object {
        const val TAG = "AudioPlayerSheet"
        private const val ARG_URI = "audio_uri"

        fun newInstance(uri: String): AudioPlayerBottomSheet {
            val args = Bundle().apply {
                putString(ARG_URI, uri)
            }
            return AudioPlayerBottomSheet().apply {
                arguments = args
            }
        }
    }
}
