import os
def convertFile(path, inputFile, outputFile):
    # inputFile. outputFile should have their format like .flac or .mp3

    # path is  <your inputFile's absolute path>

    # !! you should use \\ (not \) !!
    #    ex ) C:\\Users\\Administrator\\Desktop\\

    os.system('ffmpeg -i '+path+inputFile+' -ac 1 -ar 16000 '+path+outputFile)


def installFFmepg():
    os.system('sudo apt-get update')
    os.system('sudo apt-get -y install autoconf automake build-essential libass-dev libfreetype6-dev libgpac-dev libsdl1.2-dev libtheora-dev libtool libva-dev libvdpau-dev libvorbis-dev libx11-dev libxext-dev libxfixes-dev pkg-config texi2html zlib1g-dev')
    os.system('mkdir ~/ffmpeg_sources')

    #install yasm
    os.system('cd ~/ffmpeg_sources')
    os.system('wget http://www.tortall.net/projects/yasm/releases/yasm-1.2.0.tar.gz')
    os.system('tar xzvf yasm-1.2.0.tar.gz')
    os.system('cd yasm-1.2.0')
    os.system('./configure --prefix="$HOME/ffmpeg_build" --bindir="$HOME/bin"')
    os.system('make')
    os.system('make install')
    os.system('make distclean')

    #install NASM
    os.system('cd ~/ffmpeg_sources')
    os.system('wget http://www.nasm.us/pub/nasm/releasebuilds/2.13.01/nasm-2.13.01.tar.xz')
    os.system('tar -xvf nasm-2.13.01.tar.xz')
    os.system('cd nasm-2.13.01')
    os.system('./configure')
    os.system('make')
    os.system('sudo make install')


    #install libx264 
    os.system('cd ~/ffmpeg_sources')
    os.system('wget http://download.videolan.org/pub/x264/snapshots/last_x264.tar.bz2')
    os.system('tar xjvf last_x264.tar.bz2')
    os.system('cd x264-snapshot*')
    os.system('PATH="$PATH:$HOME/bin" ./configure --prefix="$HOME/ffmpeg_build" --bindir="$HOME/bin" --enable-static')
    os.system('make')
    os.system('make install')
    os.system('make distclean')


    #install libfdk-aac
    os.system('cd ~/ffmpeg_sources')
    os.system('wget -O fdk-aac.zip https://github.com/mstorsjo/fdk-aac/zipball/master')
    os.system('unzip fdk-aac.zip')
    os.system('cd mstorsjo-fdk-aac*')
    os.system('autoreconf -fiv')
    os.system('./configure --prefix="$HOME/ffmpeg_build" --disable-shared')
    os.system('make')
    os.system('make install')
    os.system('make distclean')

    #install libmp3lame        
    os.system('sudo apt-get install libmp3lame-dev')

    #install libopus
    os.system('cd ~/ffmpeg_sources')
    os.system('wget http://downloads.xiph.org/releases/opus/opus-1.1.tar.gz')
    os.system('tar xzvf opus-1.1.tar.gz')
    os.system('cd opus-1.1')
    os.system('./configure --prefix="$HOME/ffmpeg_build" --disable-shared')
    os.system('make')
    os.system('make install')
    os.system('make distclean')

    #install libvpx 
    os.system('cd ~/ffmpeg_sources')
    os.system('wget http://github.com/webmproject/libvpx/archive/v1.7.0/libvpx-1.7.0.tar.gz')
    os.system('tar xzvf libvpx-1.7.0.tar.gz')
    os.system('cd libvpx-1.7.0')
    os.system('./configure --prefix="$HOME/ffmpeg_build" --disable-examples')
    os.system('make')
    os.system('make install')
    os.system('make distclean')

    
    #install ffmpeg
    os.system('cd ~/ffmpeg_sources')
    os.system('wget http://ffmpeg.org/releases/ffmpeg-snapshot.tar.bz2')
    os.system('tar xjvf ffmpeg-snapshot.tar.bz2')
    os.system('cd ffmpeg')
    os.system('PATH="$HOME/bin:$PATH" PKG_CONFIG_PATH="$HOME/ffmpeg_build/lib/pkgconfig" ./configure   --prefix="$HOME/ffmpeg_build"   --pkg-config-flags="--static"   --extra-cflags="-I$HOME/ffmpeg_build/include"   --extra-ldflags="-L$HOME/ffmpeg_build/lib"   --extra-libs="-lpthread -lm"   --bindir="$HOME/bin"   --enable-gpl   --enable-libass   --enable-libfdk-aac   --enable-libfreetype   --enable-libmp3lame   --enable-libopus   --enable-libtheora   --enable-libvorbis   --enable-libvpx   --enable-libx264  --enable-nonfree')
    os.system('make')
    os.system('make install')
    os.system('make distclean')
    os.system('hash -r')

    #set PATH
    os.system('echo "MANPATH_MAP $HOME/bin $HOME/ffmpeg_build/share/man" >> ~/.manpath')
    os.system('. ~/.profile')

    #install ffserver
    os.system('sudo apt install ffmpeg')
