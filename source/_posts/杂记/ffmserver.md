category: 杂记
date: 2016-07-31
title: ffserver 配置文件
---
当我们开启一个ffserver时, 我们需要指定其配置文件
```bash
➜  ~ ./ffserver -f ffmpeg-3.1.1/doc/ffserver.conf
```
我们看一下官方给出的一个示例文件
```bash
##################################################################
# HTTP 服务器配置

# HTTP服务监听的端口号
HTTPPort 8090

# HTTP服务绑定的IP地址, 如果没有多个网卡的话，使用这个默认的就可以了
HTTPBindAddress 0.0.0.0

# HTTP最大连接数。注意这个值会限定 MaxClients的大小.
MaxHTTPConnections 2000

# 能处理的最大HTTP请求数. 由于FFServer运行的非常快， 因此
# is very fast, it is more likely that you will want to leave this high
# and use MaxBandwidth, below.
MaxClients 1000

# 与客户端交互时的最大吞吐量（ kbit/sec）.
MaxBandwidth 1000

# Access log file (uses standard Apache log file format)
# '-' is the standard output.
CustomLog -

##################################################################
# 下面展示了如何使用<Feed> 标签配置。
# 每一个live feed最少包含了一个来自ffmpeg编码器或者另一个ffserver视频或者音频流.
# 我们可以使用多个解码器对这些流数据进行解码。

<Feed feed1.ffm>

# 客户端必须使用'ffmpeg' 将live feed发送给ffserver. 如下例：
#
# ffmpeg http://localhost:8090/feed1.ffm

# ffserver 还可以进行time shifting. 我们将流数据存储在磁盘上, 然后等接到客户端请求的时候，
# 可以将这些存储的流数据再播出放出。例如发送一个下面请求：
# "http://xxxx?date=[YYYY-MM-DDT][[HH:]MM:]SS[.m...]".
# 可以指定存储该视频流的最大值(不指定大小的话,默认无限的).
# 默认存储是这样的 : File=/tmp/feed_name.ffm FileMaxSize=5M
File /tmp/feed1.ffm
FileMaxSize 200K

# 还可以指定某个视频流文件只可读, 不可删除和不可修改, 如下
# ReadOnlyFile /saved/specialvideo.ffm

# Specify launch in order to start ffmpeg automatically.
# First ffmpeg must be defined with an appropriate path if needed,
# after that options can follow, but avoid adding the http:// field
#Launch ffmpeg

# 我们还可以做IP白名单, 在下面的例子中我们只允许接收来自 localhost 的链接.
ACL allow 127.0.0.1

</Feed>


##################################################################
# Now you can define each stream which will be generated from the
# original audio and video stream. Each format has a filename (here
# 'test1.mpg'). FFServer will send this stream when answering a
# request containing this filename.
# 下面讲解如何使用<Stream>标签
# 经过上面的配置, 接下来就是将原生的视频/音频流重新定义成一个新的格式的流出来.
# 每个format都必须有一个文件名, 这样当请求中有这个文件名时, FFServer就会将这个流
# 最为客户端的应答

<Stream test1.mpg>

# coming from live feed 'feed1'
# 视频源, 例如下面的视频源来自feed1
Feed feed1.ffm

# 视频流要format成的格式, 你可以从下面选择
# mpeg       : MPEG-1 multiplexed video and audio
# mpegvideo  : only MPEG-1 video
# mp2        : MPEG-2 audio (use AudioCodec to select layer 2 and 3 codec)
# ogg        : Ogg format (Vorbis audio codec)
# rm         : RealNetworks-compatible stream. Multiplexed audio and video.
# ra         : RealNetworks-compatible stream. Audio only.
# mpjpeg     : Multipart JPEG (works with Netscape without any plugin)
# jpeg       : Generate a single JPEG image.
# mjpeg      : Generate a M-JPEG stream.
# asf        : ASF compatible streaming (Windows Media Player format).
# swf        : Macromedia Flash compatible stream
# avi        : AVI format (MPEG-4 video, MPEG audio sound)
Format mpeg

# 下面设定的是音频流的比特率. 一般编码器通常只支持少量种类的比特率
AudioBitRate 32

# 音频通道的数量: 1 = mono, 2 = stereo
AudioChannels 1

# 下面设定的是音频采样频率. 当使用比较低的比特率的时候, 应该将频率设定到22050 或者 11025.
# 支持的频率取决于采用的音频编码器.
AudioSampleRate 44100

# 下面的设定是视频比特率
VideoBitRate 64

# 码率控制的缓冲区大小 (Ratecontrol buffer size)
VideoBufferSize 40

# 每秒的帧数 (Number of frames per second)
VideoFrameRate 3

# 视频帧的大小: WxH (default: 160x128)
# 一般我们可以采用给我们提供好的帧大小, 例如下面这些:
# sqcif, qcif, cif, 4cif, qqvga, qvga, vga, svga, xga, uxga, qxga, sxga,
# qsxga, hsxga, wvga, wxga, wsxga, wuxga, woxga, wqsxga, wquxga, whsxga,
#  whuxga, cga, ega, hd480, hd720, hd1080
VideoSize 160x128

# Transmit only intra frames (useful for low bitrates, but kills frame rate).
#VideoIntraOnly

# If non-intra only, an intra frame is transmitted every VideoGopSize
# frames. Video synchronization can only begin at an intra frame.
VideoGopSize 12

# More MPEG-4 parameters
# VideoHighQuality
# Video4MotionVector

# 下面设定的编码器:
#AudioCodec mp2
#VideoCodec mpeg1video

# 关闭音频
#NoAudio

# 关闭视频
#NoVideo

#VideoQMin 3
#VideoQMax 31

#
# Set this to the number of seconds backwards in time to start. Note that
# most players will buffer 5-10 seconds of video, and also you need to allow
# for a keyframe to appear in the data stream.
#Preroll 15

# ACL:

# 通过上面的例子中我们看到了可以通过ACL来控制请求方的IP地址， 例如白名单功能
# 我们可以设定一个地址, 也可以设定一个地址区间   TODO？？？
#ACL ALLOW <first address> <last address>

# 当然我们也可以设定一个黑名单
#ACL DENY <first address> <last address>

# 你可以重复的声明 ACL allow/deny. 当第一个匹配之后,就可以执行定义的操作就不再继续匹配了
# 如果找不到匹配的
# You can repeat the ACL allow/deny as often as you like. It is on a per
# stream basis. The first match defines the action. If there are no matches,
# then the default is the inverse of the last ACL statement.
#
# 因此'ACL allow localhost' 只允许接收 localhost 的 request.
# 'ACL deny 1.0.0.0 1.255.255.255' 将会拒绝1开头的IPV4地址的所有请求，但是会接收其他的所有请求

</Stream>


##################################################################
# 下面是一些streams示例


# Multipart JPEG

#<Stream test.mjpg>
#Feed feed1.ffm
#Format mpjpeg
#VideoFrameRate 2
#VideoIntraOnly
#NoAudio
#Strict -1
#</Stream>


# Single JPEG

#<Stream test.jpg>
#Feed feed1.ffm
#Format jpeg
#VideoFrameRate 2
#VideoIntraOnly
##VideoSize 352x240
#NoAudio
#Strict -1
#</Stream>


# Flash

#<Stream test.swf>
#Feed feed1.ffm
#Format swf
#VideoFrameRate 2
#VideoIntraOnly
#NoAudio
#</Stream>


# ASF compatible

<Stream test.asf>
Feed feed1.ffm
Format asf
VideoFrameRate 15
VideoSize 352x240
VideoBitRate 256
VideoBufferSize 40
VideoGopSize 30
AudioBitRate 64
StartSendOnKey
</Stream>


# MP3 audio

#<Stream test.mp3>
#Feed feed1.ffm
#Format mp2
#AudioCodec mp3
#AudioBitRate 64
#AudioChannels 1
#AudioSampleRate 44100
#NoVideo
#</Stream>


# Ogg Vorbis audio

#<Stream test.ogg>
#Feed feed1.ffm
#Metadata title "Stream title"
#AudioBitRate 64
#AudioChannels 2
#AudioSampleRate 44100
#NoVideo
#</Stream>


# Real with audio only at 32 kbits

#<Stream test.ra>
#Feed feed1.ffm
#Format rm
#AudioBitRate 32
#NoVideo
#NoAudio
#</Stream>


# Real with audio and video at 64 kbits

#<Stream test.rm>
#Feed feed1.ffm
#Format rm
#AudioBitRate 32
#VideoBitRate 128
#VideoFrameRate 25
#VideoGopSize 25
#NoAudio
#</Stream>


##################################################################
# 在上面的例子中，我们看到视频来源是来自feed，现在我们看一下如何从文件系统里读取文件的码流，
# 然后进行格式转换等
# A stream coming from a file: you only need to set the input
# filename and optionally a new format. Supported conversions:
#    AVI -> ASF

#<Stream file.rm>
#File "/usr/local/httpd/htdocs/tlive.rm"
#NoAudio
#</Stream>

#<Stream file.asf>
#File "/usr/local/httpd/htdocs/test.asf"
#NoAudio
#Metadata author "Me"
#Metadata copyright "Super MegaCorp"
#Metadata title "Test stream from disk"
#Metadata comment "Test comment"
#</Stream>

<Stream file.rm>
File "/Users/mingwang/ffm/古城荆棘王.rmvb"
</Stream>

##################################################################
# RTSP examples
#
# 下面我们演示一些通过 RTSP URL来访问流数据:
#   rtsp://localhost:5454/test1-rtsp.mpg
#
# 还可以通过HTTP协议来访问RTSP资源
#   http://localhost:8090/test1-rtsp.rtsp

#<Stream test1-rtsp.mpg>
#Format rtp
#File "/usr/local/httpd/htdocs/test1.mpg"
#</Stream>


# 下面的例子使用libx264将一个入站的 live feed 转换成另一个 live feed,

#<Stream live.h264>
#Format rtp
#Feed feed1.ffm
#VideoCodec libx264
#VideoFrameRate 24
#VideoBitRate 100
#VideoSize 480x272
#AVPresetVideo default
#AVPresetVideo baseline
#AVOptionVideo flags +global_header
#
#AudioCodec libfaac
#AudioBitRate 32
#AudioChannels 2
#AudioSampleRate 22050
#AVOptionAudio flags +global_header
#</Stream>

##################################################################
# SDP/multicast examples
#
# 如果想要将stream广播出去，则必须使用MulticastAddress配置广播地址
#
# An SDP file is automatically generated by ffserver by adding the
# 'sdp' extension to the stream name (here
# http://localhost:8090/test1-sdp.sdp). You should usually give this
# file to your player to play the stream.
#
# The 'NoLoop' option can be used to avoid looping when the stream is
# terminated.

#<Stream test1-sdp.mpg>
#Format rtp
#File "/usr/local/httpd/htdocs/test1.mpg"
#MulticastAddress 224.124.0.1
#MulticastPort 5000
#MulticastTTL 16
#NoLoop
#</Stream>


##################################################################
# Special streams

# Server status

<Stream stat.html>
Format status

# Only allow local people to get the status
ACL allow localhost
ACL allow 192.168.0.0 192.168.255.255

#FaviconURL http://pond1.gladstonefamily.net:8080/favicon.ico
</Stream>


# Redirect index.html to the appropriate site

<Redirect index.html>
URL http://www.ffmpeg.org/
</Redirect>
```