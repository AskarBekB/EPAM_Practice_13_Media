Media Player

Task Description
In this task, you should create a media player application that should be able to play video and audio files. This application should contain screens with a video player and an audio player.

Complete the Task
Create an application with screens, including the video player screen and the audio player screen.
To select a media file (video or audio), use the default intent with the action Intent.ACTION_GET_CONTENT and the appropriate type.
Landscape orientation should be supported by the app.
After a configuration change, audio and video playback should continue from the moment before the configuration changes took place.
The main screen should contain two buttons – Play Audio and Play Video. After clicking on each button, a standard system picker should appear. After picking a media file, the corresponding screen should open.
The audio player screen should contain a button to play/pause an audio file and TextView with a path to the file.
The video player screen should contain VideoView, SeekBar to show the progress of a video, and TextView to show the current time and the total duration of the video.
